package com.share.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;

import java.util.concurrent.TimeUnit;

@ConditionalOnProperty(prefix = "share.social", name = "shiro", havingValue = "true")
@Component
public class ShiroSessionSessionStrategy implements SessionStrategy {
	@Autowired
	private  RedisTemplate redisTemplate;
	private final String redisKeyPref="share:";
	@Override
	public void setAttribute(RequestAttributes request, String name, Object value) {
		if(name.startsWith(redisKeyPref)) {
			redisTemplate.opsForValue().set(name, value, 10, TimeUnit.MINUTES);
		} else {
			request.setAttribute(name, value, 1);
		}
	}
	@Override
	public Object getAttribute(RequestAttributes request, String name) {
		if(name.startsWith(redisKeyPref)) {
			return redisTemplate.opsForValue().get(name);
		} else {
			return request.getAttribute(name, 1);
		}
	}
	@Override
	public void removeAttribute(RequestAttributes request, String name) {
		if(name.startsWith(redisKeyPref)) {
			request.removeAttribute(name, 1);
		} else {
			redisTemplate.delete(name);
		}
	}
}
