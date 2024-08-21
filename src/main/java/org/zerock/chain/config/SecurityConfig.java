package org.zerock.chain.config;

import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/signup", "/assets/**").permitAll()
                        .anyRequest().authenticated() // 인증되지 않은 사용자는 로그인 페이지로 리다이렉트
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("emp_no")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .rememberMe((rememberMe) -> rememberMe
                        .key("uniqueAndSecret") // Remember Me 기능에 사용될 키
                        .tokenValiditySeconds(86400) // Remember Me 토큰의 유효 시간 (예: 1일 = 86400초)
                        .rememberMeParameter("remember-me") // 체크박스의 name 속성
                )
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession() // 세션 고정 보호를 위해 세션을 새로운 것으로 교체
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me") // 로그아웃 시 JSESSIONID와 함께 remember-me 쿠키도 삭제
                        .addLogoutHandler(new CustomLogoutHandler()) // 직접 정의한 핸들러 추가
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                // 로그인 성공 시 메인 페이지로 리다이렉트
                response.sendRedirect("/");
            }
        };
    }

    // 커스텀 로그아웃 핸들러
    public class CustomLogoutHandler implements LogoutHandler {

        @Override
        public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
            // 세션 무효화
            if (request.getSession() != null) {
                request.getSession().invalidate();
            }

            // JSESSIONID 쿠키 삭제
            Cookie cookie = new Cookie("JSESSIONID", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            // SecurityContext 초기화
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
    }
}