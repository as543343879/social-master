package com.share.social.authorize;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * 资源服务器配置
 *
 * @author 谢小平
 */
@Configuration
public class PcResourceServerConfig extends WebSecurityConfigurerAdapter   {



	@Autowired
	private SpringSocialConfigurer pcSocialSecurityConfig;

	@Autowired
	private AuthorizeConfigManager authorizeConfigManager;


	/**
	 * Configure.
	 *
	 * @param http the http
	 *
	 * @throws Exception the exception
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().disable();

		http.apply(pcSocialSecurityConfig)
				.and()
				.headers().frameOptions().disable()
				.and()
				.csrf().disable();

		authorizeConfigManager.config(http.authorizeRequests());
	}


}