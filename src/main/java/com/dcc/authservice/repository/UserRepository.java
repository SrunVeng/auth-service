package com.dcc.authservice.repository;

import com.dcc.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByuserName(String username);
}
