package com.shop.controller;

import com.shop.dao.CouponDAO;
import com.shop.model.Coupon;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/admin/coupons")
public class CouponAdminServlet extends HttpServlet {

    private CouponDAO couponDAO = new CouponDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("create".equals(action)) {
            req.getRequestDispatcher("/admin/coupon-form.jsp").forward(req, resp);
            return;
        }
        if ("edit".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            Coupon c = couponDAO.findById(id);
            if (c == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            req.setAttribute("coupon", c);
            req.getRequestDispatcher("/admin/coupon-form.jsp").forward(req, resp);
            return;
        }
        if ("toggle".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            couponDAO.toggleActive(id);
            resp.sendRedirect(req.getContextPath() + "/admin/coupons");
            return;
        }
        List<Coupon> coupons = couponDAO.findAll();
        req.setAttribute("coupons", coupons);
        req.getRequestDispatcher("/admin/coupons.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            Coupon c = couponDAO.findById(id);
            if (c != null) {
                c.setActive(false);
                couponDAO.update(c);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/coupons");
            return;
        }
        String code = req.getParameter("code");
        String discountType = req.getParameter("discountType");
        double discountValue = Double.parseDouble(req.getParameter("discountValue"));
        double minOrderAmount = req.getParameter("minOrderAmount") != null && !req.getParameter("minOrderAmount").isEmpty()
            ? Double.parseDouble(req.getParameter("minOrderAmount")) : 0;
        int maxUsage = req.getParameter("maxUsage") != null && !req.getParameter("maxUsage").isEmpty()
            ? Integer.parseInt(req.getParameter("maxUsage")) : 0;
        String expiryStr = req.getParameter("expiryDate");

        Coupon c = new Coupon();
        c.setCode(code);
        c.setDiscountType(discountType);
        c.setDiscountValue(discountValue);
        c.setMinOrderAmount(minOrderAmount);
        c.setMaxUsage(maxUsage);
        c.setActive(true);
        if (expiryStr != null && !expiryStr.isEmpty()) {
            try { c.setExpiryDate(new SimpleDateFormat("yyyy-MM-dd").parse(expiryStr)); } catch (Exception e) {}
        }

        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            c.setId(Integer.parseInt(idParam));
            couponDAO.update(c);
        } else {
            couponDAO.save(c);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/coupons");
    }
}