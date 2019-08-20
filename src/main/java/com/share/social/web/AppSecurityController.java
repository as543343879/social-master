package com.share.social.web;

import com.share.social.AppSingUpUtils;
import com.share.social.properties.SecurityConstants;
import com.share.social.result.ResponseMessage;
import com.share.social.result.Result;
import com.share.social.support.SocialUserInfo;
import com.share.social.web.BaseSocialController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * The class App security controller.
 *
 * @author 谢小平
 */
@RestController
@Slf4j
public class AppSecurityController extends BaseSocialController {

	@Resource
	private ProviderSignInUtils providerSignInUtils;
	@Resource
	private AppSingUpUtils appSingUpUtils;
	@Autowired(required = false)
	private SessionStrategy sessionStrategy;

	/**
	 * 需要注册时跳到这里用户信息给前端
	 *
	 * @param request the request
	 *
	 * @return social user info
	 */
	@GetMapping(SecurityConstants.DEFAULT_SOCIAL_USER_INFO_URL)
	public ResponseMessage<SocialUserInfo> getSocialUserInfo(HttpServletRequest request, String uuid) {
		try{
			if(sessionStrategy == null) {
				sessionStrategy = new HttpSessionSessionStrategy();
			}
			String devceid = System.currentTimeMillis()+"";
			ConnectionData connectionData;
			if(uuid != null) {
				connectionData = (ConnectionData) sessionStrategy.getAttribute(new ServletWebRequest(request),uuid);
			} else  {
				connectionData = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request)).createData();
			}
			appSingUpUtils.saveConnectionData(devceid, connectionData);
			return Result.success(buildSocialUserInfo(connectionData,devceid));
		}catch (Exception e) {
			log.error(e.getMessage());
			return Result.error(e.getMessage());
		}

	}

}
