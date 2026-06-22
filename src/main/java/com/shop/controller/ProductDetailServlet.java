package com.shop.controller;

import com.shop.dao.CategoryDAO;
import com.shop.dao.ProductDAO;
import com.shop.dao.ProductVariantDAO;
import com.shop.dao.WishlistDAO;
import com.shop.model.Category;
import com.shop.model.Product;
import com.shop.model.ProductVariant;
import com.shop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebServlet("/product")
public class ProductDetailServlet extends HttpServlet {

    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private ProductVariantDAO variantDAO = new ProductVariantDAO();
    private WishlistDAO wishlistDAO = new WishlistDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        int id = Integer.parseInt(idParam);
        Product product = productDAO.findById(id);
        if (product == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        List<ProductVariant> variants = variantDAO.findByProductId(id);
        Map<String, List<ProductVariant>> variantsByType = new LinkedHashMap<>();
        for (ProductVariant v : variants) {
            variantsByType.computeIfAbsent(v.getVariantType(), k -> new ArrayList<>()).add(v);
        }

        User user = (User) req.getSession().getAttribute("user");
        boolean isWishlisted = user != null && wishlistDAO.isWishlisted(user.getId(), id);

        List<Category> categories = categoryDAO.findAll();
        req.setAttribute("product", product);
        req.setAttribute("categories", categories);
        req.setAttribute("variantsByType", variantsByType);
        req.setAttribute("isWishlisted", isWishlisted);
        req.getRequestDispatcher("product-detail.jsp").forward(req, resp);
    }
}
