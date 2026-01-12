package org.ipn.mx.among.bugs.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.service.AuthService;
import org.ipn.mx.among.bugs.service.JwtService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final AuthenticationConfiguration authenticationConfiguration;
	private final JwtService jwtService;
	private final MessageSource messageSource;
	private final AuthService authService;

	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors(c -> c.configurationSource(this.corsConfigSource()))
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/auth/**").permitAll()
						.requestMatchers("/error").permitAll()
						.anyRequest().authenticated()
				)
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilter(new JwtAuthenticationFilter(
								authenticationManager(),
								new ObjectMapper(),
								jwtService,
								messageSource,
								authService
						)
				)
				.addFilter(new JwtValidationFilter(
								authenticationManager(),
								new ObjectMapper(),
								jwtService
						)
				);
		return http.build();
	}

	@Bean
    public CorsConfigurationSource corsConfigSource() {
        CorsConfiguration config = new CorsConfiguration();

        String allowedOrigins = System.getenv("CORS_ALLOWED_ORIGINS");

        if (allowedOrigins != null) {
            config.setAllowedOriginPatterns(List.of(allowedOrigins.split(",")));
        } else {
            // fallback local
            config.setAllowedOriginPatterns(
                    List.of("http://localhost:4200", "http://127.0.0.1:4200")
            );
        }

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept-Language"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

	@Bean
	public FilterRegistrationBean<CorsFilter> filterRegistrationBean() {
		FilterRegistrationBean<CorsFilter> cordsBean = new FilterRegistrationBean<>(new CorsFilter(this.corsConfigSource()));
		cordsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return cordsBean;
	}
}
