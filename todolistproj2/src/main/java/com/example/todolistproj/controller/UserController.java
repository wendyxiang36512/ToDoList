package com.example.todolistproj.controller;

import com.example.todolistproj.entity.UserInfo;
import com.example.todolistproj.repo.UserInfoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.Optional;

/**
 * User controller class 用于控制和 user 相关的前后端连接
 * 其中每一个 method 表示不同的功能
 */
@RestController
@CrossOrigin("*")
//@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserInfoRepository userInfoRepository;

    /**
     * 用户登录接口
     * @param username 用户名
     * @param password 密码
     * @return 成功跳转到 ToDoList 页面，失败返回登录页
     */
    @PostMapping("/login")
    public ModelAndView login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        System.out.println("Received POST request for login: username=" + username);

        UserInfo user = userInfoRepository.findByUsernameAndPassword(username, password);

        if (user != null) {
            // 登录成功，跳转到 ToDoList 页面
            session.setAttribute("user", user);
            ModelAndView todo =  new ModelAndView("todolist");
            todo.addObject("user", user);
            return todo;
        } else {
            // 登录失败，返回登录页并提示错误信息
            ModelAndView errorView = new ModelAndView("login");
            errorView.addObject("errorMessage", "Invalid username or password");
            return errorView;
        }
    }

    /**
     * 用户注册接口
     * @param username 用户名
     * @param password 密码
     * @param birthday 生日
     * @return 注册成功跳转到 ToDoList 页面，失败返回注册页
     */
    @PostMapping("/register")
    public ModelAndView register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String birthday) {
        System.out.println("Received POST request for registration: "
                + "username=" + username + ", password=" + password + ", birthday=" + birthday);

        // 检查用户名是否已存在
        if (userInfoRepository.findByUsername(username) != null) {
            ModelAndView errorView = new ModelAndView("register");
            errorView.addObject("errorMessage", "Username already exists");
            return errorView;
        }

        // 创建新用户并存入数据库
        UserInfo newUser = new UserInfo(username, password, birthday);
        userInfoRepository.save(newUser);

        // 注册成功，跳转到 ToDoList 页面
        return new ModelAndView("todolist");
    }
    @PostMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> request, HttpSession session) {
        UserInfo currentUser = (UserInfo) session.getAttribute("user");

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        // Update only provided fields
        currentUser.setUsername(request.get("username"));
        currentUser.setBirthday(request.get("birthday"));

        // Only update password if provided
        if (request.containsKey("password") && !request.get("password").isEmpty()) {
            currentUser.setPassword(request.get("password"));
        }

        userInfoRepository.save(currentUser);

        // Invalidate session so the user needs to log in again
        session.invalidate();

        return ResponseEntity.ok().body("Profile updated. Please log in again.");
    }

    @GetMapping("/profile")
    public ModelAndView profilePage(HttpSession session) {
        UserInfo user = (UserInfo) session.getAttribute("user");

        if (user == null) {
            return new ModelAndView("redirect:index.html"); // Redirect if not logged in
        }

        ModelAndView modelAndView = new ModelAndView("profile");
        modelAndView.addObject("user", user);
        return modelAndView;
    }


}
