package com.espe.oauth_server.oauth.server.oauth;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import com.espe.oauth_server.oauth.server.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Bean
        @Order(1)
        public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
                        throws Exception {
                OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer
                                .authorizationServer();

                http
                                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                                .with(authorizationServerConfigurer, (authorizationServer) -> authorizationServer
                                                .oidc(Customizer.withDefaults()) // Enable OpenID Connect 1.0
                                )
                                .authorizeHttpRequests((authorize) -> authorize
                                                .anyRequest().authenticated())
                                // Redirect to the login page when not authenticated from the
                                // authorization endpoint
                                .exceptionHandling((exceptions) -> exceptions
                                                .defaultAuthenticationEntryPointFor(
                                                                new LoginUrlAuthenticationEntryPoint("/login"),
                                                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)));

                return http.build();
        }

        // @Bean
        // @Order(2)
        // public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
        //                 throws Exception {
        //         http
        //                         .authorizeHttpRequests((authorize) -> authorize
        //                                         .requestMatchers("/", "/.well-known/**", "/error", "/test",
        //                                                         "/user-test")
        //                                         .permitAll()
        //                                         .anyRequest().authenticated())
        //                         // Form login handles the redirect to the login page from the
        //                         // authorization server filter chain
        //                         .formLogin(form -> form
        //                                         .permitAll()
        //                                         .failureUrl("/login?error=true"));

        //         return http.build();
        // }
        @Bean 
	@Order(2)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, CustomUserDetailsService customUserDetailsService)
			throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers("/", "/.well-known/**", "/error", "/test", "/user-test", "/token-info", 
					"/oauth2/revoke", "/oauth2/introspect", "/api/auth/**").permitAll()
				.requestMatchers("/api/**").authenticated()
				.anyRequest().authenticated()
			)
			// Form login handles the redirect to the login page from the
			// authorization server filter chain
			.formLogin(form -> form
				.permitAll()
				.failureUrl("/login?error=true"))
			.userDetailsService(customUserDetailsService)
			.csrf(csrf -> csrf.disable());

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3001"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);
		configuration.setExposedHeaders(Arrays.asList("Authorization"));
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService(CustomUserDetailsService customUserDetailsService) {
		return customUserDetailsService;
	}

        @Bean
        public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(UserDetailsService userDetailsService) {
                return context -> {
                        if (context.getPrincipal() != null) {
                                var userDetails = userDetailsService.loadUserByUsername(context.getPrincipal().getName());
                                var authorities = userDetails.getAuthorities().stream()
                                                .map(auth -> auth.getAuthority())
                                                .toList();
                                context.getClaims().claim("roles", authorities);
                        }
                };
        }

        @Bean
        public RegisteredClientRepository registeredClientRepository() {
                RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                                .clientId("test")
                                .clientSecret("{noop}123456")
                                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                                .redirectUri("http://localhost:8002/login/oauth2/code/test")
                                .redirectUri("http://localhost:8002/authorized")
                                .redirectUri("http://localhost:3000")
                                .redirectUri("http://localhost:8123/login/oauth2/code/gateway-client")
                                .postLogoutRedirectUri("http://localhost:8082/logout")
                                .postLogoutRedirectUri("http://localhost:8123")
                                .scope(OidcScopes.OPENID)
                                .scope(OidcScopes.PROFILE)
                                .scope("read")
                                .scope("write")
                                .clientSettings(ClientSettings.builder()
                                                .requireAuthorizationConsent(false)
                                                .build())
                                .build();

                return new InMemoryRegisteredClientRepository(oidcClient);
        }

        @Bean
        public JWKSource<SecurityContext> jwkSource() {
                KeyPair keyPair = generateRsaKey();
                RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
                RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
                RSAKey rsaKey = new RSAKey.Builder(publicKey)
                                .privateKey(privateKey)
                                .keyID(UUID.randomUUID().toString())
                                .build();
                JWKSet jwkSet = new JWKSet(rsaKey);
                return new ImmutableJWKSet<>(jwkSet);
        }

        private static KeyPair generateRsaKey() {
                KeyPair keyPair;
                try {
                        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                        keyPairGenerator.initialize(2048);
                        keyPair = keyPairGenerator.generateKeyPair();
                } catch (Exception ex) {
                        throw new IllegalStateException(ex);
                }
                return keyPair;
        }

        @Bean
        public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
                return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
        }

        @Bean
        public org.springframework.security.oauth2.jwt.JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
                return new org.springframework.security.oauth2.jwt.NimbusJwtEncoder(jwkSource);
        }

        @Bean
        public AuthorizationServerSettings authorizationServerSettings() {
                return AuthorizationServerSettings.builder()
                                .issuer("http://localhost:9000")
                                .build();
        }

}