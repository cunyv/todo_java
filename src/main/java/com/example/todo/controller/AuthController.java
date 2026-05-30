package com.example.todo.controller;

import com.example.todo.dto.LoginDTO;
import com.example.todo.dto.RegisterDTO;
import com.example.todo.facade.AuthFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (logout != null) {
            model.addAttribute("message", "已成功退出登录");
        }
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@ModelAttribute LoginDTO loginDTO, Model model) {
        try {
            authFacade.login(loginDTO);
            return "redirect:/";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("loginDTO", loginDTO);
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam(value = "error", required = false) String error,
                               Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterDTO registerDTO) {
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            return "redirect:/register?error=两次输入的密码不一致";
        }
        if (registerDTO.getUsername() == null || registerDTO.getUsername().trim().isEmpty()) {
            return "redirect:/register?error=用户名不能为空";
        }
        if (registerDTO.getPassword() == null || registerDTO.getPassword().length() < 6) {
            return "redirect:/register?error=密码长度不能少于6位";
        }
        try {
            registerDTO.setUsername(registerDTO.getUsername().trim());
            authFacade.register(registerDTO);
            return "redirect:/login";
        } catch (RuntimeException e) {
            return "redirect:/register?error=" + e.getMessage();
        }
    }

    @PostMapping("/logout")
    public String logout() {
        authFacade.logout();
        return "redirect:/login?logout=true";
    }
}
