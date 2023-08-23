package cn.xmirror.sca.common.pojo;

import lombok.*;

import java.util.List;

/**
 * 配置持久化配置类
 * @author xingluheng
 * @date 2023/07/19 15:07
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenSCASetting {
    /**
     * 服务器地址
     */
    private String serverAddress;
    /**
     * Token
     */
    private String token;
    /**
     * 自定义Cli地址
     */
    private Boolean useCustomerCli;
    /**
     * 自定义Cli地址
     */
    private String customerPath;
    /**
     * 远程数据源
     */
    private Boolean remoteDataSourceSelected;
    /**
     * 本地数据源
     */
    private Boolean localDataSourceSelected;
    /**
     * 数据源配置
     */
    private List<DsnConfig> dsnConfigList;
}
