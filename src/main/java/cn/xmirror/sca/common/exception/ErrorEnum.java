package cn.xmirror.sca.common.exception;

/**
 * 异常枚举类
 *
 * @author Yuan Shengjun
 */
public enum ErrorEnum {

    // 通用
    CREATE_DIR_ERROR(40001, "创建目录失败"),
    COMMAND_INJECTION_ERROR(40002,"存在命令注入风险"),

    // 配置相关
    SETTING_URL_EMPTY_ERROR(40021, "url不能为空"),
    SETTING_TOKEN_EMPTY_ERROR(40022, "token不能为空"),
    SETTING_URL_PATTERN_ERROR(40023, "url格式错误"),

    // 引擎相关
    ENGINE_UNSUPPORTED_SYSTEM_ERROR(40041, "不支持此操作系统"),
    ENGINE_DOWNLOAD_ERROR(40042, "下载引擎失败"),
    ENGINE_UNREACHABLE_ERROR(40043, "引擎连接失败"),
    ENGINE_SET_EXECUTABLE_ERROR(40044, "引擎设置可执行权限失败，请手动设置后重新检测"),

    // 检测相关
    CHECK_PARSE_RESULT_ERROR(40081,"结果解析错误"),

    // 服务器相关
    SERVER_UNREACHABLE_ERROR(40101,"连接服务器失败"),
    SERVER_REQUEST_FAILURE_ERROR(40102,"请求服务器失败"),
    ;

    private int code;
    private String message;

    ErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
