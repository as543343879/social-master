package com.share.social.service;

import com.share.social.properties.SocialProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * UtilsServiceImpl class
 *
 * @author 谢小平
 * @date 2019-10-21
 */
@Service
public class UtilsServiceImpl {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SocialProperties socialProperties;
    @Autowired
    ConnectionFactoryLocator connectionFactoryLocator;
    private String selectFromUserConnection() {
        return "select userId, providerId, providerUserId, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime from " + socialProperties.getTablePref() + "UserConnection";
    }

    public String getInfoByOpenId(String openId) {
        String sql = "select userId from " + socialProperties.getTablePref() + "UserConnection where providerUserId =  '" + openId + "'";
        List<Map<String, Object>> datas = jdbcTemplate.queryForList(sql);
        if (datas == null || datas.size() <= 0) {
            return null;
        } else {
            return (String) datas.get(0).get("userId");
        }
    }




    public Map<String, Boolean> getStatus(String userId){
        String sql = "select providerId from " + socialProperties.getTablePref() + "UserConnection where userId =  '" + userId  + "'";
        List<Map<String, Object>> datas = jdbcTemplate.queryForList(sql);
        Set<String> registeredProviderIds = this.connectionFactoryLocator.registeredProviderIds();
        Map<String,Boolean> datasMap = new HashMap<>();
        for(String id: registeredProviderIds) {
            datasMap.put(id,false);
        }
        for(Map<String,Object> map : datas) {
            String  providerId = (String) map.get("providerId");
            if(datasMap.get(providerId).equals(false)) {
                datasMap.put(providerId,true);
            }
        }

        return datasMap;
    }
}
