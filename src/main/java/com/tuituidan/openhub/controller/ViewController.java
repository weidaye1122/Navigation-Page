package com.tuituidan.openhub.controller;

import com.tuituidan.openhub.service.UserService;
import com.tuituidan.openhub.util.RequestUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * IndexController.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2021/2/21
 */
@Controller
public class ViewController {

    @Resource
    private UserService userService;

    /**
     * index
     *
     * @return String
     */
    @GetMapping("/")
    public String index() {
        if (userService.needsSetup()) {
            return "redirect:/login";
        }
        return "forward:/index.html";
    }

    /**
     * login
     *
     * @return String
     */
    @GetMapping("/login")
    public String login() {
        HttpServletRequest request = RequestUtils.getRequest();
        Object error = request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (error instanceof Exception) {
            request.setAttribute("errorMsg", ((Exception) error).getMessage());
            // 只使用一次就移除，再刷新页面就不显示了
            request.getSession().removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
        request.setAttribute("needsSetup", userService.needsSetup());
        return "login";
    }
}
