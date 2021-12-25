package cn.xmirror.sca.common.constant;

/**
 * 引擎和操作系统枚举类。定义操作系统和引擎命令行工具名称关系
 *
 * @author Yuan Shengjun
 */
public enum EngineOsEnum {
    MAC("mac","opensca-cli"),
    LINUX("linux","opensca-cli"),
    WINDOWS("windows","opensca-cli.exe"),
    ;

    private final String osName;
    private final String cliName;

    EngineOsEnum(String osName, String cliName) {
        this.osName = osName;
        this.cliName = cliName;
    }

    public String getOsName() {
        return osName;
    }

    public String getCliName() {
        return cliName;
    }

    public static EngineOsEnum getEngineOsEnum() {
        String systemName = System.getProperty("os.name").toLowerCase();
        for (EngineOsEnum value : values()) {
            if (systemName.contains(value.osName)) {
                return value;
            }
        }
        return null;
    }
}
