package com.dcc.authservice.config;

import com.dcc.authservice.model.User;
import com.dcc.authservice.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupMockUser {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${demo.user.enabled:true}")
    private boolean enabled;

    @Value("${demo.user.username:john}")
    private String username;

    @Value("${demo.user.password:123456}")
    private String password;

    @PostConstruct
    public void createMockUser() {
        if (!enabled || userRepository.findByuserName(username) != null) {
            return;
        }

        User user = new User();
        user.setUserName(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(List.of());
        user.setIsVerified(true);
        user.setIsBlock(false);
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(true);
        user.setIsCredentialsNonExpired(true);
        user.setIsDeleted(false);

        userRepository.save(user);
    }
}
