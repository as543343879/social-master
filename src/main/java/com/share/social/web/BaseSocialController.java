
package com.share.social.web;

import com.share.social.support.SocialUserInfo;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;


/**
 * The class Social controller.
 *
 * @author 谢小平
 */
public abstract class BaseSocialController {

	/**
	 * 根据Connection信息构建SocialUserInfo
	 *
	 * @param connection the connection
	 *
	 * @return social user info
	 */
	protected SocialUserInfo buildSocialUserInfo(ConnectionData connectionData, String devceid) {
		SocialUserInfo userInfo = new SocialUserInfo();
		userInfo.setProviderId(connectionData.getProviderId()  );
		userInfo.setProviderUserId(connectionData.getProviderUserId() );
		userInfo.setNickname(connectionData.getDisplayName() );
		userInfo.setHeadimg(connectionData.getImageUrl() );
		userInfo.setDeviceId(devceid);
		return userInfo;
	}

}
