package com.share.social.weixin.config;

import com.share.social.properties.SocialProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * The class Security core config.
 *
 * @author 谢小平
 */
@Configuration
@EnableConfigurationProperties(SocialProperties.class)
public class SocialCoreConfig {

}
