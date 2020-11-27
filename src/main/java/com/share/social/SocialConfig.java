
package com.share.social;

import com.share.social.properties.SocialProperties;
import com.share.social.support.PcSpringSocialConfigurer;
import com.share.social.support.SocialAuthenticationFilterPostProcessor;
import com.share.social.utils.SocialDataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * 社交登录配置主类
 *
 * @author 谢小平
 */
@Configuration
@EnableSocial
public class SocialConfig extends SocialConfigurerAdapter {

//	@Autowired
//	@Qualifier("dataSource") //配置中定义的名字
//	private DataSource dataSource;
	@Autowired
	SocialDataSource socialDataSource;
	@Autowired
	private SocialProperties socialProperties;

	@Autowired(required = false)
	private ConnectionSignUp connectionSignUp;

	@Autowired(required = false)
	private SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor;

	@Autowired(required = false)
	private SessionStrategy sessionStrategy;

	@Bean
	@ConditionalOnMissingBean(PasswordEncoder.class)
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	/**
	 * Gets users connection repository.
	 *
	 * @param connectionFactoryLocator the connection factory locator
	 *
	 * @return the users connection repository
	 */
	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		return usersConnectionRepository(connectionFactoryLocator);
	}

	@Bean
	public UsersConnectionRepository usersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(socialDataSource.socialDataSource() , connectionFactoryLocator, Encryptors.noOpText());
		if(StringUtils.isNotEmpty(socialProperties.getTablePref())){
			repository.setTablePrefix(socialProperties.getTablePref());
		}

		if (connectionSignUp != null) {
			repository.setConnectionSignUp(connectionSignUp);
		}
		return repository;
	}
	/**
	 * 社交登录配置类，供浏览器或app模块引入设计登录配置用。
	 *
	 * @return spring social configurer
	 */
	@Bean
	public SpringSocialConfigurer pcSocialSecurityConfig() {
		String filterProcessesUrl = socialProperties.getFilterProcessesUrl();
		PcSpringSocialConfigurer configurer = null;
		if(sessionStrategy != null) {
			configurer = new PcSpringSocialConfigurer(filterProcessesUrl,sessionStrategy);
		} else  {
			configurer = new PcSpringSocialConfigurer(filterProcessesUrl);
		}
//		configurer.signupUrl(securityProperties.getBrowser().getSignUpUrl());
		configurer.setSocialAuthenticationFilterPostProcessor(socialAuthenticationFilterPostProcessor);
		return configurer;
	}

	/**
	 * 用来处理注册流程的工具类
	 *
	 * @param connectionFactoryLocator the connection factory locator
	 *
	 * @return provider sign in utils
	 */
	@Bean
	public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator connectionFactoryLocator) {
		if(sessionStrategy != null) {
			return new ProviderSignInUtils(sessionStrategy,connectionFactoryLocator,
					getUsersConnectionRepository(connectionFactoryLocator));
		} else {
			return new ProviderSignInUtils(connectionFactoryLocator,
					getUsersConnectionRepository(connectionFactoryLocator));
		}

	}
}
