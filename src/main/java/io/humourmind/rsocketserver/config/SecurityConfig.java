package io.humourmind.rsocketserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;

@Configuration
@EnableRSocketSecurity
public class SecurityConfig {

	@Bean
	PayloadSocketAcceptorInterceptor rsocketInterceptor(RSocketSecurity rsocket) {
		rsocket.authorizePayload(authorize -> authorize.anyRequest().authenticated()
				.anyExchange().permitAll())
				.basicAuthentication(Customizer.withDefaults());
		return rsocket.build();
	}

	@Bean
	public MapReactiveUserDetailsService userDetailsService() {
		return new MapReactiveUserDetailsService(User.withUsername("spring")
				.password(passwordEncoder().encode("rsocket")).roles("").build());
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
