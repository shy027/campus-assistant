package top.shy.campusassistant.common.exception;

import lombok.Getter;
import top.shy.campusassistant.common.result.ResultCode;

import java.io.Serial;

/**
 * 自定义业务异常
 *
 * @author mqxu
 */
@Getter
public class ServerException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final int code;

    public ServerException(String message) {
        super(message);
        this.code = ResultCode.FAIL.getCode();
    }

    public ServerException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ServerException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }
}
