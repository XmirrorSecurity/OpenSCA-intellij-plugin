package cn.xmirror.sca.common;

import cn.xmirror.sca.common.constant.ExploitLevelEnum;
import cn.xmirror.sca.common.constant.SecurityLevelEnum;
import cn.xmirror.sca.common.dto.Component;
import cn.xmirror.sca.common.dto.Overview;
import cn.xmirror.sca.common.dto.Vulnerability;
import cn.xmirror.sca.common.exception.ErrorEnum;
import cn.xmirror.sca.common.exception.SCAException;
import cn.xmirror.sca.ui.window.tree.ComponentTreeNode;
import cn.xmirror.sca.ui.window.tree.RootTreeNode;
import cn.xmirror.sca.ui.window.tree.VulnerabilityTreeNode;
import com.alibaba.fastjson.JSON;
import com.intellij.openapi.util.text.StringUtil;
import lombok.Data;

import javax.swing.tree.MutableTreeNode;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 结果解析器。从json文件中解析结果——转换成树
 *
 * @author Yuan Shengjun
 */
public class ResultParser {

    /**
     * 解析结果
     *
     * @param path     结果位置
     * @param overview 需要展示的概览数据
     */
    public static MutableTreeNode parseResult(String path, Overview overview) {
        try (InputStream is = new FileInputStream(path)) {
            // 获取结果
            Result result = JSON.parseObject(is, StandardCharsets.UTF_8, Result.class);
            if (StringUtil.isNotEmpty(result.getError())) {
                throw new SCAException(ErrorEnum.ENGINE_UNREACHABLE_ERROR);
            }
            return generateTree(overview, result.getChildren());
        } catch (IOException e) {
            throw new SCAException(ErrorEnum.CHECK_PARSE_RESULT_ERROR);
        }
    }

    /**
     * 生成树
     *
     * @param overview
     * @param components
     * @return
     */
    public static MutableTreeNode generateTree(Overview overview, List<Component> components) {
        RootTreeNode rootTreeNode = new RootTreeNode(overview);
        Set<String> distinctVulnerability = new HashSet<>();
        if (components != null) {
            distinctAndSortComponent(components).forEach(component -> {
                ComponentTreeNode componentTreeNode = new ComponentTreeNode(component);
                for (Vulnerability vulnerability : component.getVulnerabilities()) {
                    checkVulnerability(vulnerability);
                    if (distinctVulnerability.add(vulnerability.getId())) {
                        SecurityLevelEnum.statistics(vulnerability.getSecurityLevelId(), overview.getVss());
                    }
                    VulnerabilityTreeNode vulnerabilityTreeNode = new VulnerabilityTreeNode(vulnerability);
                    componentTreeNode.add(vulnerabilityTreeNode);
                }
                SecurityLevelEnum.statistics(component.getSecurityLevelId(), overview.getCss());
                rootTreeNode.add(componentTreeNode);
            });
        }
        return rootTreeNode;
    }

    public static void checkVulnerability(Vulnerability v) {
        if (StringUtil.isEmpty(v.getName())) {
            v.setName("未知安全漏洞");
        }
        if (v.getExploitLevelId() == null) {
            v.setExploitLevelId(ExploitLevelEnum.DIFFICULT.getLevel());
        }
        if (v.getSecurityLevelId() == null) {
            v.setSecurityLevelId(SecurityLevelEnum.LOW.getLevel());
        }
    }

    /**
     * 去重并且排序组件
     *
     * @param components
     * @return
     */
    private static Collection<Component> distinctAndSortComponent(List<Component> components) {
        Map<String, Component> mergeComponents = new HashMap<>();
        for (Component component : components) {
            String path = component.getPath();
            String coordinate = component.getVendor() + component.getName() + component.getVersion();
            Component cpt = mergeComponents.get(coordinate);
            if (cpt == null) {
                List<String> paths = new ArrayList<>();
                if (path != null) paths.add(path);
                component.setPath(null);
                component.setPaths(paths);
                component.setVulnerabilities(component.getVulnerabilities().stream().sorted(Comparator.comparingInt(Vulnerability::getSecurityLevelId)).collect(Collectors.toList()));
                component.setSecurityLevelId(component.getVulnerabilities().get(0).getSecurityLevelId());
                mergeComponents.put(coordinate, component);
            } else if (path != null) {
                cpt.getPaths().add(path);
            }
        }
        return mergeComponents.values().stream().sorted(Comparator.comparingInt(Component::getSecurityLevelId)).collect(Collectors.toList());
    }

    @Data
    public static class Result {
        private List<Component> children;
        private String error;
    }
}
