package cn.xmirror.sca.common.dto;

import lombok.Data;

/**
 * 用于上传的 SaaS 数据传输对象
 * @author xingluheng
 * @date 2023/11/06 11:07
 **/
@Data
public class IdeSyncResultDTO {
    /**
     * 用户Token
     */
    private String token;
    /**
     * 项目id
     */
    private String projectId;
    /**
     * 版本
     */
    private String version;
}
