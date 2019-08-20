package com.share.social.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Utils class
 *
 * @author 谢小平
 * @date 2019/7/3
 */
public class Utils {
    public static String formEncode(String data) {
        try {
            return URLEncoder.encode(data, "UTF-8");
        } catch (UnsupportedEncodingException var3) {
            throw new IllegalStateException(var3);
        }
    }
}
