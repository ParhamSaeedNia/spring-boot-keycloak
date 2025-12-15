package org.example.session20keycloak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public endpoint - no authentication required";
    }

    @GetMapping("/user/hello")
    public String userHello() {
        return "Hello, user!";
    }

    @GetMapping("/admin/hello")
    public String adminHello() {
        return "Hello, admin!";
    }
}
