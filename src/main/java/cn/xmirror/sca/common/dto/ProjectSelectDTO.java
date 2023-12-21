package cn.xmirror.sca.common.dto;

import lombok.Data;

import java.util.List;

/**
 * @author xingluheng
 * @date 2023/11/06 15:38
 **/
@Data
public class ProjectSelectDTO {
    /**
     * 团队名称
     */
    private String teamName;
    private List<GeneralSelectDTO> projectList;
}
