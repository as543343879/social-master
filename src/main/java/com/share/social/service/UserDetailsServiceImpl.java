package com.share.social.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;

/**
 * UserDetailsServiceImpl class
 *
 * @author 谢小平
 * @date 2019/6/25
 */
@Component
public class UserDetailsServiceImpl implements SocialUserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        return new SocialUser(userId, passwordEncoder.encode(""),
                true, true, true, true,
                AuthorityUtils.commaSeparatedStringToAuthorityList(""));
    }
}
