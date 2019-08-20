package com.share.social.support;

import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * 继承默认的社交登录配置，加入自定义的后处理逻辑
 *
 * @author 谢小平
 */
public class PcSpringSocialConfigurer extends SpringSocialConfigurer {

	private String filterProcessesUrl;

	private SessionStrategy sessionStrategy;

	private SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor;

	/**
	 * Instantiates a new Pc spring social configurer.
	 *
	 * @param filterProcessesUrl the filter processes url
	 */
	public PcSpringSocialConfigurer(String filterProcessesUrl) {
		this.filterProcessesUrl = filterProcessesUrl;
	}

	public PcSpringSocialConfigurer(String filterProcessesUrl,SessionStrategy sessionStrategy) {
		this.filterProcessesUrl = filterProcessesUrl;
		this.sessionStrategy = sessionStrategy;
	}
	/**
	 * Post process t.
	 *
	 * @param <T>    the type parameter
	 * @param object the object
	 *
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <T> T postProcess(T object) {
		SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);
		filter.setFilterProcessesUrl(filterProcessesUrl);
		if(sessionStrategy != null ) {
			filter.setSessionStrategy(sessionStrategy);
		}
		if (socialAuthenticationFilterPostProcessor != null) {
			socialAuthenticationFilterPostProcessor.process(filter);
		}
		return (T) filter;
	}

	/**
	 * Gets filter processes url.
	 *
	 * @return the filter processes url
	 */
	public String getFilterProcessesUrl() {
		return filterProcessesUrl;
	}

	/**
	 * Sets filter processes url.
	 *
	 * @param filterProcessesUrl the filter processes url
	 */
	public void setFilterProcessesUrl(String filterProcessesUrl) {
		this.filterProcessesUrl = filterProcessesUrl;
	}

	/**
	 * Gets social authentication filter post processor.
	 *
	 * @return the social authentication filter post processor
	 */
	public SocialAuthenticationFilterPostProcessor getSocialAuthenticationFilterPostProcessor() {
		return socialAuthenticationFilterPostProcessor;
	}

	/**
	 * Sets social authentication filter post processor.
	 *
	 * @param socialAuthenticationFilterPostProcessor the social authentication filter post processor
	 */
	public void setSocialAuthenticationFilterPostProcessor(SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor) {
		this.socialAuthenticationFilterPostProcessor = socialAuthenticationFilterPostProcessor;
	}

}
