package com.example.login.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class userController {

    private final UserService userService;

//    login처리하는 코드는 Controller에 작성할 필요 없음!

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login_page() {
        return "login";
    }

    @RequestMapping(value = "/join", method = RequestMethod.GET)
    public String join_page() {
        return "join";
    }

    @RequestMapping(value = "/join", method = RequestMethod.POST)
    public String joinUser(UserDto joinUser, RedirectAttributes redirectAttributes) {
        userService.saveUser(joinUser);
        redirectAttributes.addAttribute("userid", joinUser.getUserid());
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String user_page() {
        return "user";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String admin_page() {
        return "admin";
    }

}
