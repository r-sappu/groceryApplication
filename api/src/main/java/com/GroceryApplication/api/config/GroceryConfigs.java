package com.GroceryApplication.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class GroceryConfigs {

	
	@Bean
	public SecurityFilterChain filter(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests)->requests
				.requestMatchers(HttpMethod.GET,"/grocery/**").hasAnyRole("ADMIN","USER")
				.requestMatchers(HttpMethod.GET,"/grocery/getInventoryDetails").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST,"/grocery/addItems", "/grocery/updateItem", "/grocery/removeItems/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST,"/grocery/order").hasRole("USER")
			)
		.httpBasic(Customizer.withDefaults());
		return httpSecurity.build();
	}
	
	@Bean
	public UserDetailsService userDetailService() {
		
		UserDetails admin = User.builder()
                .username("rohit")
                .password(passwordEncoder().encode("sappu"))
                .roles("ADMIN")
                .build();
				
		UserDetails user = User.builder()
                .username("roshan")
                .password(passwordEncoder().encode("mahto"))
                .roles("USER")
                .build();
		
		return new InMemoryUserDetailsManager(admin, user);
	}
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
	
}
