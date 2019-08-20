package com.share.social.web;

import com.share.social.AppSingUpUtils;
import com.share.social.properties.SecurityConstants;
import com.share.social.properties.SocialProperties;
import com.share.social.result.ResponseMessage;
import com.share.social.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.ConnectSupport;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * AddRestController class
 *
 * @author 谢小平
 * @date 2019/7/3
 */
@RestController
@Slf4j
@Api(description = "三方登录")
public class ConnectRestController implements InitializingBean {
    @Autowired
    SocialProperties socialProperties;
    @Autowired
    private  ConnectionFactoryLocator connectionFactoryLocator;
    @Autowired
    private  ConnectionRepository connectionRepository;
    private ConnectSupport connectSupport;
    @Autowired(required = false)
    private SessionStrategy sessionStrategy;
    private String applicationUrl;
    @Autowired
    AppSingUpUtils appSingUpUtils;

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    @ApiOperation(httpMethod = "DELETE", value = "解绑")
    @RequestMapping(value = {"/auth/remove"}, method = {RequestMethod.DELETE})
    public ResponseMessage remove(@RequestParam String userId, @RequestParam String providerId) {
        appSingUpUtils.removeConnections(userId,providerId);
        return Result.success();
    }

    @GetMapping(SecurityConstants.DEFAULT_SOCIAL_AUTHORIZEURL)
    @ApiOperation(httpMethod = "GET", value = "获取三方登录授权链接")
    public ResponseMessage  authorizeUrl(@ApiParam(name = "providerId",example = "qq/weixin") @RequestParam String providerId, @ApiParam("前端回调url") @RequestParam String redirectUri, NativeWebRequest request) {
        try{
            ConnectionFactory<?> connectionFactory = this.connectionFactoryLocator.getConnectionFactory(providerId);
            return Result.success(buildOAuth2Url((OAuth2ConnectionFactory<?>) connectionFactory, request, null,redirectUri));
        }catch (Exception e) {
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.connectSupport = new ConnectSupport(this.sessionStrategy);
        if (this.applicationUrl != null) {
            this.connectSupport.setApplicationUrl(this.applicationUrl);
        }
    }
    private String buildOAuth2Url(OAuth2ConnectionFactory<?> connectionFactory, NativeWebRequest request, MultiValueMap<String, String> additionalParameters, String callbackUrl) {
        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
        String defaultScope = connectionFactory.getScope();
        OAuth2Parameters parameters = this.getOAuth2Parameters(request, defaultScope, additionalParameters,callbackUrl);
        String state = connectionFactory.generateState();
        parameters.add("state", state);
        this.sessionStrategy.setAttribute(request, "oauth2State", state);
        return oauthOperations.buildAuthorizeUrl(parameters);
    }

    private OAuth2Parameters getOAuth2Parameters(NativeWebRequest request, String defaultScope, MultiValueMap<String, String> additionalParameters,String callbackUrl) {
        OAuth2Parameters parameters = new OAuth2Parameters(additionalParameters);
        parameters.setRedirectUri(callbackUrl);
        String scope = request.getParameter("scope");
        if (scope != null) {
            parameters.setScope(scope);
        } else if (defaultScope != null) {
            parameters.setScope(defaultScope);
        }

        return parameters;
    }

}
