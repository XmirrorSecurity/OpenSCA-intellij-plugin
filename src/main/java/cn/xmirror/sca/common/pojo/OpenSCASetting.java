package cn.xmirror.sca.common.pojo;

import lombok.*;

/**
 * 配置持久化配置类
 * @author xingluheng
 * @date 2023/07/19 15:07
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenSCASetting {
    private String serverAddress;
    private String token;
    private boolean useCustomerCli;
    private String customerPath;
}
