package com.mira.returnremind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableScheduling
public class ReturnremindApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReturnremindApplication.class, args);
	}

    @Bean
    public ServletRegistrationBean<?> h2ServletRegistration() {
        return new ServletRegistrationBean<>(new JakartaWebServlet(), "/h2-console/*");
    }

}
