package com.share.social.result;
/**
 * @author ：冀保杰
 * @date：2017-11-10
 * @desc：
 */
public enum ResponseMessageCodeEnum {

    // 成功
    SUCCESS("0"),
    // 错误
    ERROR("-1"),
    // 异常
    EXCEPTION("-2"),
    // 错误1000
    VALID_ERROR("1000"),
    // 错误999
    RE_LOGIN("999");

    private String code;

    ResponseMessageCodeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
