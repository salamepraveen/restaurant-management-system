package com.prav.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages="com.prav" ,exclude = {
        UserDetailsServiceAutoConfiguration.class
})
@EnableDiscoveryClient
//@ComponentScan(basePackages = "com.prav")
//@EnableJpaRepositories(basePackages = "com.prav.user.repository")
@EntityScan(basePackages = "com.prav.user.model")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}