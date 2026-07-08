package com.tuituidan.openhub.config;

import com.tuituidan.openhub.bean.entity.RoleUser;
import com.tuituidan.openhub.bean.entity.User;
import com.tuituidan.openhub.bean.entity.UserStar;
import com.tuituidan.openhub.repository.RoleUserRepository;
import com.tuituidan.openhub.repository.UserStarRepository;
import com.tuituidan.openhub.service.AccessStatService;
import com.tuituidan.openhub.util.SecurityUtils;
import java.io.IOException;
import java.net.URI;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * LoginSuccessHandler.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2022/10/26
 */
@Component
@Slf4j
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Resource
    private UserStarRepository userStarRepository;

    @Resource
    private RoleUserRepository roleUserRepository;

    @Resource
    private AccessStatService accessStatService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        recordLoginSuccess(request);
        User user = (User) authentication.getPrincipal();
        user.setPassword(null);
        user.setStarCardIds(userStarRepository.findByUserId(user.getId()).stream()
                .map(UserStar::getCardId).collect(Collectors.toSet()));
        user.setRoleIds(roleUserRepository.findByUserId(user.getId()).stream()
                .map(RoleUser::getRoleId).collect(Collectors.toSet()));
        if (!SecurityUtils.isAdmin(user)) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
                authentication.getCredentials(),
                AuthorityUtils.createAuthorityList("admin"));
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        super.onAuthenticationSuccess(request, response, newAuthentication);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        return normalizeTargetUrl(request, super.determineTargetUrl(request, response));
    }

    private void recordLoginSuccess(HttpServletRequest request) {
        try {
            accessStatService.recordLogin(true, request);
        } catch (Exception ex) {
            log.warn("记录登录成功统计失败", ex);
        }
    }

    private String normalizeTargetUrl(HttpServletRequest request, String targetUrl) {
        if (StringUtils.isBlank(targetUrl) || StringUtils.startsWith(targetUrl, "//")) {
            return "/";
        }
        if (StringUtils.startsWith(targetUrl, "/")) {
            return targetUrl;
        }
        try {
            URI uri = URI.create(targetUrl);
            if (!uri.isAbsolute() || !isSameOrigin(request, uri)) {
                return "/";
            }
            String path = StringUtils.defaultIfBlank(uri.getRawPath(), "/");
            String query = StringUtils.isBlank(uri.getRawQuery()) ? "" : "?" + uri.getRawQuery();
            return path + query;
        } catch (Exception ex) {
            log.warn("忽略不安全的登录跳转地址: {}", targetUrl);
            return "/";
        }
    }

    private boolean isSameOrigin(HttpServletRequest request, URI uri) {
        String requestScheme = StringUtils.defaultIfBlank(request.getHeader("X-Forwarded-Proto"), request.getScheme());
        String requestHost = StringUtils.defaultIfBlank(request.getHeader("X-Forwarded-Host"), request.getServerName());
        String host = uri.getHost();
        int port = uri.getPort();
        String uriHost = port < 0 ? host : host + ":" + port;
        return StringUtils.equalsIgnoreCase(requestScheme, uri.getScheme())
                && StringUtils.equalsIgnoreCase(requestHost, uriHost);
    }

}
