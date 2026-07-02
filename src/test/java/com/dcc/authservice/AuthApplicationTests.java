package com.dcc.authservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dcc.authservice.model.User;
import com.dcc.authservice.repository.UserRepository;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = new User();
        user.setUserName("john");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRoles(List.of());
        user.setIsVerified(true);
        user.setIsBlock(false);
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(true);
        user.setIsCredentialsNonExpired(true);
        user.setIsDeleted(false);
        userRepository.save(user);
    }

    @Test
    void contextLoads() {}

    @Test
    void loginReturnsUnauthorizedForInvalidPassword() throws Exception {
        HttpResponse<String> response = postLogin(Map.of("username", "john", "password", "asd"));

        assertEquals(401, response.statusCode());
        assertTrue(response.body().contains("\"message\":\"Invalid username or password.\""));
    }

    @Test
    void loginAcceptsUserNameAlias() throws Exception {
        HttpResponse<String> response = postLogin(Map.of("user_name", "john", "password", "123456"));

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"accessToken\""));
    }

    @Test
    void loginReturnsBadRequestWhenUsernameMissing() throws Exception {
        HttpResponse<String> response = postLogin(Map.of("password", "123456"));

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("\"message\":\"Username is required\""));
    }

    private HttpResponse<String> postLogin(Map<String, String> payload) throws Exception {
        String requestBody = payload.entrySet().stream()
                        .map(entry -> "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"")
                        .reduce("{", (json, field) -> json.length() == 1 ? json + field : json + "," + field)
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}
