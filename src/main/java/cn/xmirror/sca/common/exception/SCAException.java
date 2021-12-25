package cn.xmirror.sca.common.exception;

/**
 * 异常类
 *
 * @author Yuan Shengjun
 */
public class SCAException extends RuntimeException {
    static final long serialVersionUID = 1L;
    private final ErrorEnum error;

    public SCAException(ErrorEnum error) {
        super("[" + error.getCode() + "]" + error.getMessage());
        this.error = error;
    }

    public SCAException(ErrorEnum error, String appendMessage) {
        super("[" + error.getCode() + "]" + error.getMessage() + "：" + appendMessage);
        this.error = error;
    }

    public ErrorEnum getError() {
        return error;
    }
}
