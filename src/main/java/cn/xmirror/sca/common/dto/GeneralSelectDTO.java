package cn.xmirror.sca.common.dto;

import lombok.Data;

/**
 * @author xingluheng
 */
@Data
public class GeneralSelectDTO {
    /**
     * 项目id编号
     */
    private String projectUid;

    /**
     * 主键id
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    public GeneralSelectDTO() {
    }

}
