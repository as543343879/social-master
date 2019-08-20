package com.share.social.result;
/**
 * @author ：冀保杰
 * @date：2017-11-10
 * @desc：
 */
public class ResponseMessage<T> {

    private String code;
    private String message;
    private T data;
    /**
     * 成功码.
     */
    public static final String SUCCESS_CODE = "0";
    /**
     * 成功信息.
     */
    public static final String SUCCESS_MESSAGE = "操作成功";
    /**
     * 错误码.
     */
    public static final String ERROR_CODE = "-1";

    /**
     * 错误信息.
     */
    public static final String ERROR_MESSAGE = "内部异常";

    /**
     * 错误码：参数非法
     */
    public static final String ILLEGAL_ARGUMENT_CODE_ = "100";

    /**
     * 错误信息：参数非法
     */
    public static final String ILLEGAL_ARGUMENT_MESSAGE = "参数非法";
    public ResponseMessage() {
    }

    public ResponseMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseMessage(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isOk() {
        return this.code.equals(ResponseMessageCodeEnum.SUCCESS.getCode());
    }
}
