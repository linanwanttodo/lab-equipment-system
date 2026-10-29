package com.barezen.security;

import com.barezen.entity.User;
import com.barezen.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** 从数据库加载用户，返回携带数值 id 的 AppUser，供 JWT 过滤器使用。 */
@Service
public class DbUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public DbUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
        return new AppUser(user.getId(), user.getUsername(), user.getPassword(),
                user.getName(), user.getRole().name());
    }
}
