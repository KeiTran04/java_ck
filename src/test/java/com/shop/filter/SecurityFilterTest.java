package com.shop.filter;

import com.shop.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @Mock private HttpServletRequest req;
    @Mock private HttpServletResponse resp;
    @Mock private FilterChain chain;
    @Mock private HttpSession session;
    @Mock private RequestDispatcher rd;

    private SecurityFilter filter;

    @BeforeEach
    void setUp() {
        filter = new SecurityFilter();
    }

    @Test
    void testNoSession_redirectsToLogin() throws Exception {
        when(req.getSession(false)).thenReturn(null);
        when(req.getContextPath()).thenReturn("");

        filter.doFilter(req, resp, chain);

        verify(resp).sendRedirect("/login.jsp");
        verify(chain, never()).doFilter(req, resp);
    }

    @Test
    void testUserNotLoggedIn_redirectsToLogin() throws Exception {
        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        when(req.getContextPath()).thenReturn("");

        filter.doFilter(req, resp, chain);

        verify(resp).sendRedirect("/login.jsp");
        verify(chain, never()).doFilter(req, resp);
    }

    @Test
    void testCustomerUser_redirectsToLogin() throws Exception {
        User customer = new User(1, "cust", "c@t.com", "hash", "CUSTOMER");
        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(customer);
        when(req.getContextPath()).thenReturn("");

        filter.doFilter(req, resp, chain);

        verify(resp).sendRedirect("/login.jsp");
        verify(chain, never()).doFilter(req, resp);
    }

    @Test
    void testAdminUser_allowsAccess() throws Exception {
        User admin = new User(2, "admin", "a@t.com", "hash", "ADMIN");
        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(admin);

        filter.doFilter(req, resp, chain);

        verify(chain).doFilter(req, resp);
        verify(resp, never()).sendRedirect(anyString());
    }
}
