package com.tuituidan.openhub.config;

import com.tuituidan.openhub.service.AccessStatService;
import com.tuituidan.openhub.service.UserService;
import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * SecurityConfig.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2022/9/7
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Resource
    private SecurityProperties securityProperties;

    @Resource
    private UserService userService;

    @Resource
    private LoginSuccessHandler loginSuccessHandler;

    @Resource
    private AccessStatService accessStatService;

    /**
     * filterChain
     *
     * @param http http
     * @return SecurityFilterChain
     * @throws Exception Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers().frameOptions().sameOrigin();
        http.headers().contentTypeOptions();
        http.headers().referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN);
        http.headers().addHeaderWriter(new StaticHeadersWriter("Permissions-Policy",
                "camera=(), microphone=(), geolocation=()"));
        http.headers().addHeaderWriter(new StaticHeadersWriter("Content-Security-Policy",
                "object-src 'none'; base-uri 'self'; frame-ancestors 'self'; form-action 'self'"));
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        http.addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class);
        http.userDetailsService(userService);

        setLogin(http.formLogin());
        setLogout(http.logout());

        http.authorizeRequests()
                .antMatchers("/h2-console/**").denyAll()
                .antMatchers(HttpMethod.POST, "/api/v1/upload/attachments", "/api/v1/upload/images").authenticated()
                .antMatchers(HttpMethod.POST, "/api/v1/upload/**").hasAuthority("admin")
                .antMatchers(HttpMethod.DELETE, "/api/v1/icon/**").hasAuthority("admin")
                .antMatchers(HttpMethod.PATCH, "/api/v1/icon/**").hasAuthority("admin")
                .antMatchers("/api/v1/datasource/**").hasAuthority("admin")
                .antMatchers(securityProperties.getPermitUrl()).permitAll()
                .antMatchers(securityProperties.getGeneralUserUrl()).authenticated()
                .antMatchers("/api/v1/**").hasAuthority("admin")
                .anyRequest()
                .permitAll();
        http.exceptionHandling().defaultAuthenticationEntryPointFor((request, response, ex) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED),
                request -> "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With")));
        return http.build();
    }

    private void setLogin(FormLoginConfigurer<HttpSecurity> login) {
        loginSuccessHandler.setTargetUrlParameter("returnUrl");
        login.loginPage("/login").loginProcessingUrl("/login")
                .successHandler(loginSuccessHandler)
                .failureHandler((request, response, exception) -> handleLoginFailure(request, response));
    }

    private void setLogout(LogoutConfigurer<HttpSecurity> logout) {
        SimpleUrlLogoutSuccessHandler handler = new SimpleUrlLogoutSuccessHandler();
        handler.setDefaultTargetUrl("/");
        handler.setUseReferer(false);
        logout.logoutUrl("/logout").logoutSuccessHandler(handler).deleteCookies("JSESSIONID");
    }

    /**
     * bCryptPasswordEncoder
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder cryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * authenticationManager
     *
     * @param authenticationConfiguration authenticationConfiguration
     * @return AuthenticationManager
     * @throws Exception Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    private void handleLoginFailure(HttpServletRequest request, HttpServletResponse response) throws IOException {
        recordLoginFailure(request);
        if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("用户名或密码错误");
            return;
        }
        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION,
                new BadCredentialsException("用户名或密码错误"));
        response.sendRedirect(request.getContextPath() + "/login?error");
    }

    private void recordLoginFailure(HttpServletRequest request) {
        try {
            accessStatService.recordLogin(false, request);
        } catch (Exception ex) {
            log.warn("记录登录失败统计失败", ex);
        }
    }

    private static class CsrfCookieFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                FilterChain filterChain) throws ServletException, IOException {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                csrfToken.getToken();
            }
            filterChain.doFilter(request, response);
        }
    }

}
