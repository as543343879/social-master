package com.share.social.qq.config;

import com.share.social.properties.QQProperties;
import com.share.social.properties.SocialProperties;
import com.share.social.qq.connet.QQConnectionFactory;
import com.share.social.social.SocialAutoConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.connect.ConnectionFactory;


/**
 * The class Qq auto config.
 *
 * @author 谢小平
 */
@Configuration
@ConditionalOnProperty(prefix = "share.social.qq", name = "app-id")
public class QQAutoConfig extends SocialAutoConfigurerAdapter {

	private final SocialProperties socialProperties;

	@Autowired
	public QQAutoConfig(SocialProperties socialProperties) {
		this.socialProperties = socialProperties;
	}

	/**
	 * Create connection factory connection factory.
	 *
	 * @return the connection factory
	 */
	@Override
	protected ConnectionFactory<?> createConnectionFactory() {
		QQProperties qqConfig = socialProperties.getQq();
		return new QQConnectionFactory(qqConfig.getProviderId(), qqConfig.getAppId(), qqConfig.getAppSecret());
	}

}
