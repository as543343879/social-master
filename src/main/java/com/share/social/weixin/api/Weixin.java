package com.share.social.weixin.api;

/**
 * 微信API调用接口
 *
 * @author 谢小平
 */
public interface Weixin {

	/**
	 * Gets user info.
	 *
	 * @param openId the open id
	 *
	 * @return the user info
	 */
	WeixinUserInfo getUserInfo(String openId);

}
