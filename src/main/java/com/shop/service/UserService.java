package com.shop.service;

import com.shop.dao.UserDAO;
import com.shop.model.User;
import com.shop.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private UserDAO userDAO = new UserDAO();

    public User login(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user != null && PasswordUtil.verifyPassword(password, user.getPassword())) {
            log.info("User '{}' logged in", username);
            return user;
        }
        log.warn("Failed login attempt for '{}'", username);
        return null;
    }

    public String register(String username, String email, String password, String confirmPassword) {
        if (username == null || username.trim().isEmpty()) return "Vui lòng nhập tên đăng nhập";
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$")) return "Email không hợp lệ";
        if (password == null || password.length() < 6) return "Mật khẩu phải có ít nhất 6 ký tự";
        if (!password.equals(confirmPassword)) return "Mật khẩu xác nhận không khớp";
        if (userDAO.findByUsername(username) != null) return "Tên đăng nhập đã tồn tại";
        if (userDAO.findByEmail(email) != null) return "Email đã được sử dụng";

        User user = new User();
        user.setUsername(username.trim());
        user.setEmail(email.trim());
        user.setPassword(PasswordUtil.hashPassword(password));

        if (userDAO.register(user)) {
            log.info("User '{}' registered successfully", username);
            return null;
        }
        return "Đăng ký thất bại, vui lòng thử lại";
    }
}
