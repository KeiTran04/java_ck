package com.shop.controller;

import com.shop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServletTest {

    @Mock private HttpServletRequest req;
    @Mock private HttpServletResponse resp;
    @Mock private RequestDispatcher rd;
    @Mock private UserService userService;

    private RegisterServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new RegisterServlet();
        java.lang.reflect.Field f = RegisterServlet.class.getDeclaredField("userService");
        f.setAccessible(true);
        f.set(servlet, userService);
    }

    @Test
    void testMissingFields_returnsError() throws Exception {
        when(req.getParameter("username")).thenReturn("");
        when(req.getParameter("email")).thenReturn("");
        when(req.getParameter("password")).thenReturn("");
        when(req.getParameter("confirmPassword")).thenReturn("");
        when(userService.register("", "", "", "")).thenReturn("Vui lòng nhập tên đăng nhập");
        when(req.getRequestDispatcher("register.jsp")).thenReturn(rd);

        servlet.doPost(req, resp);

        verify(req).setAttribute(eq("error"), anyString());
        verify(rd).forward(req, resp);
    }

    @Test
    void testPasswordMismatch_returnsError() throws Exception {
        when(req.getParameter("username")).thenReturn("user1");
        when(req.getParameter("email")).thenReturn("user1@test.com");
        when(req.getParameter("password")).thenReturn("pass123");
        when(req.getParameter("confirmPassword")).thenReturn("different");
        when(userService.register("user1", "user1@test.com", "pass123", "different"))
            .thenReturn("Mật khẩu xác nhận không khớp");
        when(req.getRequestDispatcher("register.jsp")).thenReturn(rd);

        servlet.doPost(req, resp);

        verify(req).setAttribute("error", "Mật khẩu xác nhận không khớp");
        verify(rd).forward(req, resp);
    }

    @Test
    void testDuplicateUsername_returnsError() throws Exception {
        when(req.getParameter("username")).thenReturn("existing");
        when(req.getParameter("email")).thenReturn("email@test.com");
        when(req.getParameter("password")).thenReturn("pass123");
        when(req.getParameter("confirmPassword")).thenReturn("pass123");
        when(userService.register("existing", "email@test.com", "pass123", "pass123"))
            .thenReturn("Tên đăng nhập đã tồn tại");
        when(req.getRequestDispatcher("register.jsp")).thenReturn(rd);

        servlet.doPost(req, resp);

        verify(req).setAttribute("error", "Tên đăng nhập đã tồn tại");
        verify(rd).forward(req, resp);
    }

    @Test
    void testSuccess_createsUserAndForwards() throws Exception {
        when(req.getParameter("username")).thenReturn("newuser");
        when(req.getParameter("email")).thenReturn("new@test.com");
        when(req.getParameter("password")).thenReturn("mypass");
        when(req.getParameter("confirmPassword")).thenReturn("mypass");
        when(userService.register("newuser", "new@test.com", "mypass", "mypass")).thenReturn(null);
        when(req.getRequestDispatcher("login.jsp")).thenReturn(rd);

        servlet.doPost(req, resp);

        verify(req).setAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
        verify(rd).forward(req, resp);
    }
}
