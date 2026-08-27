package com.GroceryApplication.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class GroceryConfigs {

	
	@Bean
	public SecurityFilterChain filter(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf( csrf -> csrf.disable()).authorizeHttpRequests((requests)->requests
				.requestMatchers(HttpMethod.GET,"/grocery/getItems").hasAnyRole("ADMIN","USER")
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
	
}
