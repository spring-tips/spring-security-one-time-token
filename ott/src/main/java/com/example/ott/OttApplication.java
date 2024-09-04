package com.example.ott;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Map;

@SpringBootApplication
public class OttApplication {

    public static void main(String[] args) {
        SpringApplication.run(OttApplication.class, args);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(http -> http.anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .oneTimeTokenLogin(configurer -> configurer
                        .generatedOneTimeTokenSuccessHandler((request, response, oneTimeToken) -> {
                            var token = oneTimeToken.getTokenValue();

                            var msg = "please go to http://localhost:8080/login/ott?token=" + token;
                            System.out.println(msg);

                            response.setContentType(MediaType.TEXT_HTML_VALUE);
                            response.getWriter().write("you've got console mail!");

                        }))
                .build();
    }

    @Bean
    InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        var josh = User.withDefaultPasswordEncoder().username("josh").password("pw").roles("USER").build();
        var rob = User.withDefaultPasswordEncoder().username("rob").password("pw").roles("ADMIN", "USER").build();
        return new InMemoryUserDetailsManager(
                josh, rob
        );
    }

}

@Controller
@ResponseBody
class SecuredController {

    @GetMapping("/")
    Map<String, String> hello(Principal principal) {
        return Map.of("message", "Hello, " + principal.getName());
    }

}
