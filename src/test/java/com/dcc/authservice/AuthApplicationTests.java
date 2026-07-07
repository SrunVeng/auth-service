package com.dcc.authservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dcc.authservice.model.User;
import com.dcc.authservice.repository.UserRepository;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        HttpResponse<String> response = postLogin(validLoginPayload("asd"));

        assertEquals(401, response.statusCode());
        assertTrue(response.body().contains("Invalid username or password"));
    }

    @Test
    void loginReturnsAccessTokenForValidCredentials() throws Exception {
        HttpResponse<String> response = postLogin(validLoginPayload("123456"));

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("access_token"));
        assertTrue(response.body().contains("Bearer"));
    }

    @Test
    void validateAcceptsValidAccessToken() throws Exception {
        String token = extractAccessToken(postLogin(validLoginPayload("123456")).body());

        HttpResponse<String> response = postValidate(Map.of("token", token));

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"valid\":true"));
    }

    @Test
    void validateRejectsInvalidAccessToken() throws Exception {
        HttpResponse<String> response = postValidate(Map.of("token", "bad-token"));

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"valid\":false"));
    }

    @Test
    void loginReturnsBadRequestWhenUsernameMissing() throws Exception {
        Map<String, String> payload = validLoginPayload("123456");
        payload.remove("username");
        HttpResponse<String> response = postLogin(payload);

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("Username is required"));
    }

    private Map<String, String> validLoginPayload(String password) {
        Map<String, String> payload = new HashMap<>();
        payload.put("username", "john");
        payload.put("password", password);
        return payload;
    }

    private HttpResponse<String> postLogin(Map<String, String> payload) throws Exception {
        return postJson("/auth/login", payload);
    }

    private HttpResponse<String> postValidate(Map<String, String> payload) throws Exception {
        return postJson("/auth/validate", payload);
    }

    private HttpResponse<String> postJson(String path, Map<String, String> payload) throws Exception {
        String requestBody = payload.entrySet().stream()
                        .map(entry -> "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"")
                        .reduce("{", (json, field) -> json.length() == 1 ? json + field : json + "," + field)
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String extractAccessToken(String responseBody) {
        Matcher matcher = Pattern.compile("\"access_token\":\"([^\"]+)\"").matcher(responseBody);
        assertTrue(matcher.find(), "access token should be present in login response");
        return matcher.group(1);
    }
}
