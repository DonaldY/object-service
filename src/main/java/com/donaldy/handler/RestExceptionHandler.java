package com.donaldy.handler;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.donaldy.common.Const;
import com.donaldy.common.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    /**
     * 自定义运行异常
     * @param exception 自定义运行异常
     * @return          携带错误信息的消息体
     */
    @ExceptionHandler(RestfulException.class)
    @ResponseBody
    public ServerResponse restfulExceptionHandler(RestfulException exception) {
        log.error(exception.getMessage());
        exception.printStackTrace();
        return ServerResponse.createByErrorCodeMessage(exception.getCode() ,exception.getMsg());
    }

    /**
     * 参数不完整(400)
     * @param exception 方法参数异常
     * @return          携带错误信息的消息体
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ServerResponse methodExceptionHandler(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage());
        exception.printStackTrace();
        return ServerResponse.createByErrorCodeMessage(Const.HttpStatusCode.BAD_REQUEST.getCode(),
                "这貌似不送给我的吧");
    }

    /**
     * 404的拦截.
     * @param exception 资源没有找到
     * @return          携带错误信息的消息体
     */
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public ServerResponse notFoundHandler(NoHandlerFoundException exception) {
        log.error(exception.getMessage());
        exception.printStackTrace();
        return ServerResponse.createByErrorCodeMessage(Const.HttpStatusCode.NOT_FOUND.getCode(),
                "真没找到这个资源哇！");
    }

    /**
     * 参数格式错误(400)
     * @param exception 参数格式错误
     * @return          携带错误信息的消息体
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ServerResponse numberFormatHandler(MethodArgumentTypeMismatchException exception) {
        log.error(exception.getMessage());
        exception.printStackTrace();
        return ServerResponse.createByErrorCodeMessage(Const.HttpStatusCode.BAD_REQUEST.getCode(),
                "参数格式错误");
    }

    /**
     * 400错误
     * @param exception 缺少参数
     * @return          携带错误信息的消息体
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseBody
    public ServerResponse requestMissingHandler(MissingServletRequestParameterException exception) {
        log.error(exception.getMessage());
        exception.printStackTrace();
        return ServerResponse.createByErrorCodeMessage(Const.HttpStatusCode.BAD_REQUEST.getCode() ,
                "这快递不是我的");
    }

    /**
     * IO异常(500)
     * @param exception IO异常
     * @return          携带错误信息的消息体
     */
    @ExceptionHandler(IOException.class)
    @ResponseBody
    public ServerResponse ioExceptionHandler(IOException exception) {
        log.error(exception.getMessage());
        exception.printStackTrace();
        return ServerResponse.createByErrorCodeMessage(Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                "文件读取异常");
    }

    /**
     * OSS client
     * @param exception OSS异常
     * @return          携带错误信息的消息体
     */
    @ExceptionHandler({ClientException.class})
    @ResponseBody
    public ServerResponse ossCreateExceptionHandler(OSSException exception) {
        log.error(exception.getMessage());
        exception.printStackTrace();
        return ServerResponse.createByErrorCodeMessage(Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                "网貌似断开了");
    }

    /**
     * OSS 异常(500)
     * @param exception OSS异常
     * @return          携带错误信息的消息体
     */
    @ExceptionHandler({OSSException.class})
    @ResponseBody
    public ServerResponse ossExceptionHandler(OSSException exception) {
        log.error(exception.getMessage());
        exception.printStackTrace();
        return ServerResponse.createByErrorCodeMessage(Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                "OSS 读取异常");
    }

    /**
     * 默认异常处理(500)
     * @param exception 异常
     * @return          携带错误信息的消息体
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ServerResponse serverErrorHandler(Exception exception) {
        log.error(exception.getMessage());
        exception.printStackTrace();
        return ServerResponse.createByErrorCodeMessage(Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                "好像出错了哦");
    }
}