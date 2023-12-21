package cn.xmirror.sca.common;

import cn.xmirror.sca.common.constant.ExploitLevelEnum;
import cn.xmirror.sca.common.constant.SecurityLevelEnum;
import cn.xmirror.sca.common.dto.Component;
import cn.xmirror.sca.common.dto.FilePath;
import cn.xmirror.sca.common.dto.Overview;
import cn.xmirror.sca.common.dto.Vulnerability;
import cn.xmirror.sca.common.exception.ErrorEnum;
import cn.xmirror.sca.common.exception.SCAException;
import cn.xmirror.sca.service.CheckService;
import cn.xmirror.sca.ui.window.ConfigurablePanel;
import cn.xmirror.sca.ui.window.tree.ComponentTreeNode;
import cn.xmirror.sca.ui.window.tree.FilePathTreeNode;
import cn.xmirror.sca.ui.window.tree.RootTreeNode;
import cn.xmirror.sca.ui.window.tree.VulnerabilityTreeNode;
import com.alibaba.fastjson.JSON;
import com.intellij.openapi.diagnostic.Logger;
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
    private static final Logger LOG = Logger.getInstance(ConfigurablePanel.class);


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

            // 平铺获取有漏洞的组件
            List<Component> componentList = new ArrayList<>();
            result.getChildren().forEach(item -> {
                List<Component> allComponent = getAllComponent(item, new ArrayList<Component>());
                componentList.addAll(allComponent);
            });

            return generateTree(overview, componentList.isEmpty() ? result.getChildren() : componentList);
        } catch (IOException e) {
            LOG.error(e);
            throw new SCAException(ErrorEnum.CHECK_PARSE_RESULT_ERROR);
        }
    }

    /**
     * 递归获取有漏洞的组件树
     *
     * @param component
     * @param componentList
     * @return
     */
    private static List<Component> getAllComponent(Component component, ArrayList<Component> componentList) {
        if (component.getChildren() == null || component.getChildren().isEmpty()) {
            return componentList;
        }
        for (Component child : component.getChildren()) {
            if (child.getVulnerabilities() != null && !child.getVulnerabilities().isEmpty()) {
                componentList.add(child);
            }
            getAllComponent(child, componentList);
        }
        return componentList;
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
        Arrays.fill(overview.getCss(), 0);
        Arrays.fill(overview.getVss(), 0);
        if (components != null) {
            Collection<Component> distinctAndSortComponentCollection = distinctAndSortComponent(components);
            List<String> pathList = distinctAndSortComponentCollection.stream().map(Component::getPath)
                    .distinct().collect(Collectors.toList());
            pathList.forEach(path -> {
                FilePathTreeNode filePathTreeNode = new FilePathTreeNode(new FilePath(path));
                distinctAndSortComponentCollection.forEach(component -> {
                    if (component.getPath().equals(path)) {
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
                        filePathTreeNode.add(componentTreeNode);
                    }
                });
                rootTreeNode.add(filePathTreeNode);
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
            String coordinate = component.getVendor() + component.getName() + component.getVersion();
            Component cpt = mergeComponents.get(coordinate);
            if (cpt == null) {
                component.setPaths(component.getPaths());
                // 相同组件去重
                if (!component.getPaths().isEmpty()) {
                    String path = component.getPaths().get(0).replace(CheckService.PROJECT_BASE_PATH, "");
                    component.setPath(path);
                    if (path.contains("[")) {
                        component.setPath(path.substring(0, path.indexOf("[") - 1));
                    }
                }
                List<Vulnerability> vulnerabilities = component.getVulnerabilities();
                if (vulnerabilities != null && !vulnerabilities.isEmpty()) {
                    List<Vulnerability> sortVulnListBySecurityLevelId = vulnerabilities.stream().sorted(Comparator.comparingInt(Vulnerability::getSecurityLevelId)).collect(Collectors.toList());
                    component.setVulnerabilities(sortVulnListBySecurityLevelId);
                    component.setSecurityLevelId(sortVulnListBySecurityLevelId.get(0).getSecurityLevelId());
                } else {
                    component.setVulnerabilities(new ArrayList<>());
                    component.setSecurityLevelId(5);
                }

                mergeComponents.put(coordinate, component);
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
