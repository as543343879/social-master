package com.share.social.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 社交登录配置项
 *
 * @author 谢小平
 */
@Data
@ConfigurationProperties(prefix = "share.social")
public class SocialProperties {

	/**
	 * 社交登录功能拦截的url
	 */
	private String signupUrl="/social/user";
	private String filterProcessesUrl = "/shareSocial/auth";
	private String tablePref = "";


	private QQProperties qq = new QQProperties();

	private WeixinProperties weixin = new WeixinProperties();

}
