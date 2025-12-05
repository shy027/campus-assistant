package top.shy.campusassistant.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.shy.campusassistant.common.result.Result;
import top.shy.campusassistant.common.result.ResultCode;

/**
 * 全局异常处理器
 *
 * @author mqxu
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(ServerException.class)
    public Result<String> handleServerException(ServerException ex) {
        log.error("业务异常：{}", ex.getMessage());
        return Result.fail(ex.getCode(), ex.getMessage());
    }

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String errorMsg = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.error("参数校验异常：{}", errorMsg);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), errorMsg);
    }

    /**
     * 参数绑定异常处理
     */
    @ExceptionHandler(BindException.class)
    public Result<String> handleBindException(BindException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String errorMsg = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        log.error("参数绑定异常：{}", errorMsg);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), errorMsg);
    }

    /**
     * 其他异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception ex) {
        log.error("系统异常：", ex);
        return Result.fail("系统异常，请联系管理员");
    }
}
