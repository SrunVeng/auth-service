package com.dcc.authservice.config;

import com.dcc.authservice.model.User;
import com.dcc.authservice.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupMockUser {

    private static final String MOCK_USERNAME = "john";
    private static final String MOCK_PASSWORD = "123456";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createMockUser() {
        if (userRepository.findByuserName(MOCK_USERNAME) != null) {
            return;
        }

        User user = new User();
        user.setUserName(MOCK_USERNAME);
        user.setPassword(passwordEncoder.encode(MOCK_PASSWORD));
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
