package cn.xmirror.sca.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 组件实体类
 *
 * @author Yuan Shengjun
 */
@Data
public class Component implements Serializable {
    /**
     * 厂商
     */
    private String vendor;

    /**
     * 组件名
     */
    private String name;

    /**
     * 组件版本
     */
    private String version;

    /**
     * 许可证列表
     */
    private List<String> licenses;

    /**
     * 所属语言
     */
    private String language;

    /**
     * 组件文件路径
     */
    private String path;

    /**
     * 组件名风险等级
     */
    private Integer securityLevelId;

    /**
     * 组件漏洞
     */
    private List<Vulnerability> vulnerabilities;

    /**
     * 组件路径集合（辅助）
     */
    private List<String> paths;

    /**
     * 依赖类型 true直接 false间接
     */
    private boolean direct;

    /**
     * 子children
     */
    private List<Component> children;
}
