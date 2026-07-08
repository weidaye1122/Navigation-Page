package com.tuituidan.openhub.config;

import com.tuituidan.openhub.service.AccessStatService;
import com.tuituidan.openhub.service.UserService;
import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        http.headers().frameOptions().disable();
        http.csrf().disable();
        http.userDetailsService(userService);

        setLogin(http.formLogin());
        setLogout(http.logout());

        http.authorizeRequests()
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
        handler.setUseReferer(true);
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

}
