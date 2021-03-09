package com.codessquad.qna.controller;

import com.codessquad.qna.model.User;
import com.codessquad.qna.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/form")
    public String viewSingUp() {
        logger.info("회원가입 페이지 요청");
        return "user/form";
    }

    @PostMapping("/user/form")
    public String signUp(User user) {
        boolean result = this.userService.save(user);
        logger.info("회원가입 요청");
        return result ? "redirect:/user/list" : "redirect:/user/singUp";
    }

    @GetMapping("/user/login")
    public String viewLogin() {
        logger.info("로그인 페이지 요청");
        return "user/login";
    }

    @PostMapping("/user/login")
    public String login(String userId, String password, HttpSession session) {
        boolean isLogin = this.userService.login(userId, password, session);
        logger.info("로그인 요청");
        return isLogin ? "redirect:/" : "redirect:/user/login_failed";
    }

    @GetMapping("/user/list")
    public String viewUserList(Model model) {
        model.addAttribute("users", this.userService.findAll());
        logger.info("유저 리스트 페이지 요청");
        return "user/list";
    }

    @GetMapping("/user/{userId}")
    public String viewProfile(@PathVariable("userId") String userId, Model model) {
        User user = this.userService.findByUserId(userId);
        model.addAttribute("user", user);
        logger.info("유저 프로필 페이지 요청");
        return (user.getId() != null) ? "user/profile" : "redirect:/user/list";
    }

    @GetMapping("/user/{id}/update")
    public String viewUpdateProfile(@PathVariable("id") Long id, Model model) {
        User user = this.userService.findById(id);
        model.addAttribute("user", user);
        logger.info("유저 정보 수정 페이지 요청");
        return (user.getId() != null) ? "user/updateForm" : "redirect:/user/list";
    }

    @PutMapping("/user/{id}/update")
    public String updateProfile(@PathVariable("id") Long id, User user, String oldPassword) {
        boolean result = this.userService.update(id, user, oldPassword);
        logger.info("유저 정보 수정 요청");
        return result ? "redirect:/user/list" : "redirect:/user/" + id + "/update";
    }

}
