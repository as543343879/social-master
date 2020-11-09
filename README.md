### 1，环境以及配置说明
```` java
     1,父pom 中控制jar包版本
    <dependencyManagement>
		 <dependencies>
            <dependency>
                <groupId>org.springframework.social</groupId>
                <artifactId>spring-social-core</artifactId>
                <version>1.1.6.1.RELEASE</version>
            </dependency>
            <dependency>
                <groupId>org.springframework.social</groupId>
                <artifactId>spring-social-config</artifactId>
                <version>1.1.6.RELEASE</version>
            </dependency>
            <dependency>
                <groupId>org.springframework.social</groupId>
                <artifactId>spring-social-web</artifactId>
                <version>1.1.6.RELEASE</version>
            </dependency>
            <dependency>
                <groupId>org.springframework.social</groupId>
                <artifactId>spring-social-security</artifactId>
                <version>1.1.6.1.RELEASE</version>
            </dependency>
            <dependency>
                <artifactId>share-social-master</artifactId>
                <groupId>com.weepal</groupId>
                <version>2.0-SNAPSHOT</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
	
	2，子项目中映入jar
	   <dependency>
            <artifactId>share-social-master</artifactId>
            <groupId>com.weepal</groupId>
        </dependency>
	3, 创建表
	create table UserConnection (userId varchar(255) not null,
		providerId varchar(255) not null,
		providerUserId varchar(255),
		rank int not null,
		displayName varchar(255),
		profileUrl varchar(512),
		imageUrl varchar(512),
		accessToken varchar(512) not null,
		secret varchar(512),
		refreshToken varchar(512),
		expireTime bigint,
		primary key (userId, providerId, providerUserId));
		create unique index UserConnectionRank on UserConnection(userId, providerId, rank);
	4，启动类 扫描bean 加上 com.share 例如 @ComponentScan(basePackages = {"com.share", "com.weepal.wpcommon", "com.weepal.appwsclientdynamic"})

	
````
### 2，依赖的bean 以及 提供bean 例子
##### 	1，redisTemplate 操作redis
````java
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.share.social.utils.SocialDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
/**
 * @date：2017-11-10
 * @desc：
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(prefix = "spring.redis", name = "host")
public class RedisConfiguration {

    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }
    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }
    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setKeySerializer(stringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

}

````
##### 	 2，jdbcTemplate  
````java
		@Autowired
		@Qualifier("mdmbi") //配置中定义的名字
		private DataSource mdmbiDataSource;

		@Bean
		public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(mdmbiDataSource);
		}
````
##### 	 3，pcSocialAuthenticationSuccessHandler 微信扫码后跳转
````java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.share.social.result.Result;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.social.security.SocialUser;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * APP环境下认证成功处理器.
 *
 */
@Component("pcSocialAuthenticationSuccessHandler")
public class PcSocialAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	@Resource
	private ObjectMapper objectMapper;
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
		logger.info("登录成功");
		SocialUser principal = (SocialUser) authentication.getPrincipal();
		String loginName = principal.getUserId();
		// 根据 绑定操作的loginName， 业务关联，自定义数据给前端
		SecurityContextHolder.getContext().setAuthentication(null);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write((objectMapper.writeValueAsString(Result.success("登陆成功信息"))));
	}

}

````
##### 	  4，SocialDataSource 指定哪个数据源 存储 UserConnection 表
````java
import com.share.social.utils.SocialDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
/**
 * ISocialDataSource class
 *
 * @date 2019-10-10
 */
@Component
public class ISocialDataSource implements SocialDataSource {
    @Autowired
    @Qualifier("mdmbi") //配置中定义的名字
    private DataSource mdmbiDataSource;

    @Override
    public DataSource socialDataSource() {
        return mdmbiDataSource;
    }
}
````
##### 	  5，AuthRestController 
````java
import com.google.common.base.Preconditions;
import com.share.social.AppSingUpUtils;
import com.share.social.properties.SocialProperties;
import com.share.social.result.ResponseMessage;
import com.share.social.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * AuthRestController class
 *
 * @date 2019/6/25
 */
@RestController
@Slf4j
@Api(tags = "三方登录")
public class AuthRestController {
    @Resource
    private AppSingUpUtils appSingUpUtils;
    @Autowired
    SocialProperties socialProperties;
    @PostMapping(value = "/auth/registerSocialBing")
    @ApiOperation(httpMethod = "POST", value = "三方登录绑定用户")
    @ResponseBody
    public ResponseMessage registerSocialBing(HttpServletRequest request, HttpServletResponse response, String userName, String password, @RequestHeader("deviceId") String deviceId ) {
       try{
           Preconditions.checkArgument(StringUtils.isNotEmpty(userName),"登录名不能为空");
           Preconditions.checkArgument(StringUtils.isNotEmpty(password),"密码不能为空");
           // 根据传入的用户名密码进行业务校验，  业务关联，自定义数据给前端，或者进行登陆逻辑
           appSingUpUtils.doPostSignUpById(deviceId, userName);
           SecurityContextHolder.getContext().setAuthentication(null);
           return Result.success("绑定成功信息");
       }catch (Exception e) {
           return Result.error(e.getMessage());
       }
    }
}

````

##### 	  5，绑定解绑
```` java
    @Autowired
    UtilsServiceImpl utilsService;
	utilsService.getStatus(userId) //获取用户绑定状态
	
	
````

### 4 配置说明
````java
最简配置
share:
  social:
    signupUrl: http://127.0.0.1:80/social/user 扫描后 未绑定用户自动跳转方法。 
    shiro: true  指定redis存储 信息
    qq:
      app-id: 101386962
      app-secret: 4d18ce3d7eec1cd95d16a778665c7027
      providerId: qq
    weixin:
      app-id: wxde780db253813d05
      app-secret: ab7162714a2708da67b2f820f77953c7
      providerId: weixin
自定义配置
share:
  social: 
  	filterProcessesUrl： 配置拦截路径  默认"/shareSocial/auth"
	tablePref:"" UserConnection表前缀  默认未空
	  
````

### 5流程说明
![](http://192.168.1.111:4999/server/../Public/Uploads/2019-10-10/5d9ece18b7361.png)
1，前端访问 /auth/socialAuthorizeurl 获取三方登陆跳转路径，访问，扫描 跳转回来
![](http://192.168.1.111:4999/server/../Public/Uploads/2019-10-10/5d9ecf2891263.png)
把 ?code=081nQAPJ11FeR30Wd7OJ1O1RPJ1nQAPK&state=0bb04627-0b1b-4fe3-aeec-d6cb79fa9718 参数带上 访问 如果是app  按app 方式获取code

2，后端/shareSocial/auth/${providerId}?code=081nQAPJ11FeR30Wd7OJ1O1RPJ1nQAPK&state=0bb04627-0b1b-4fe3-aeec-d6cb79fa9718方法
![](http://192.168.1.111:4999/server/../Public/Uploads/2019-10-10/5d9eceafd9dd4.png)
	假如用户已经绑定 跳转  PcSocialAuthenticationSuccessHandler
	用户未绑定 返回三方登陆信息 执行 registerSocialBing 绑定方法完成绑定
	


