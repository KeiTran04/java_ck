package com.shop.controller;

import com.shop.model.User;
import com.shop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServletTest {

    @Mock private HttpServletRequest req;
    @Mock private HttpServletResponse resp;
    @Mock private RequestDispatcher rd;
    @Mock private HttpSession session;
    @Mock private UserService userService;

    private LoginServlet servlet;
    private User customerUser;
    private User adminUser;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new LoginServlet();
        java.lang.reflect.Field f = LoginServlet.class.getDeclaredField("userService");
        f.setAccessible(true);
        f.set(servlet, userService);

        customerUser = new User();
        customerUser.setId(1);
        customerUser.setUsername("customer");
        customerUser.setRole("CUSTOMER");

        adminUser = new User();
        adminUser.setId(2);
        adminUser.setUsername("admin");
        adminUser.setRole("ADMIN");
    }

    @Test
    void testMissingFields_returnsError() throws Exception {
        when(req.getParameter("username")).thenReturn("");
        when(req.getParameter("password")).thenReturn("");
        when(req.getRequestDispatcher("login.jsp")).thenReturn(rd);

        servlet.doPost(req, resp);

        verify(req).setAttribute(eq("error"), anyString());
        verify(rd).forward(req, resp);
    }

    @Test
    void testUserNotFound_returnsError() throws Exception {
        when(req.getParameter("username")).thenReturn("unknown");
        when(req.getParameter("password")).thenReturn("pass");
        when(userService.login("unknown", "pass")).thenReturn(null);
        when(req.getRequestDispatcher("login.jsp")).thenReturn(rd);

        servlet.doPost(req, resp);

        verify(req).setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
        verify(rd).forward(req, resp);
    }

    @Test
    void testWrongPassword_returnsError() throws Exception {
        when(req.getParameter("username")).thenReturn("customer");
        when(req.getParameter("password")).thenReturn("wrongpass");
        when(userService.login("customer", "wrongpass")).thenReturn(null);
        when(req.getRequestDispatcher("login.jsp")).thenReturn(rd);

        servlet.doPost(req, resp);

        verify(req).setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
        verify(rd).forward(req, resp);
    }

    @Test
    void testCustomerLogin_redirectsToHome() throws Exception {
        when(req.getParameter("username")).thenReturn("customer");
        when(req.getParameter("password")).thenReturn("pass123");
        when(userService.login("customer", "pass123")).thenReturn(customerUser);
        when(req.getSession()).thenReturn(session);
        when(req.getContextPath()).thenReturn("");

        servlet.doPost(req, resp);

        verify(session).setAttribute("user", customerUser);
        verify(resp).sendRedirect("/home");
    }

    @Test
    void testAdminLogin_redirectsToDashboard() throws Exception {
        when(req.getParameter("username")).thenReturn("admin");
        when(req.getParameter("password")).thenReturn("admin123");
        when(userService.login("admin", "admin123")).thenReturn(adminUser);
        when(req.getSession()).thenReturn(session);
        when(req.getContextPath()).thenReturn("");

        servlet.doPost(req, resp);

        verify(session).setAttribute("user", adminUser);
        verify(resp).sendRedirect("/admin/dashboard");
    }
}
