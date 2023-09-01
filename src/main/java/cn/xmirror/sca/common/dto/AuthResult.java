package cn.xmirror.sca.common.dto;

import lombok.Data;

/**
 * IDE认证结果
 * @author xingluheng
 * @date 2023/08/31 11:56
 **/
@Data
public class AuthResult {
    /**
     *  认证结果 200:成功 401:用户没有Token 404:稍后再试(没有点击授权|服务器响应较慢)
     */
    private Integer code;
    /**
     * token 结果
     */
    private String token;
}
