package com.shop.controller;

import com.shop.dao.CategoryDAO;
import com.shop.dao.ProductDAO;
import com.shop.model.Category;
import com.shop.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String search = req.getParameter("search");
        String categoryParam = req.getParameter("category");
        String pageParam = req.getParameter("page");
        String minPriceParam = req.getParameter("minPrice");
        String maxPriceParam = req.getParameter("maxPrice");
        String sort = req.getParameter("sort");
        int page = 1;
        int size = 12;

        if (pageParam != null) try { page = Integer.parseInt(pageParam); if (page < 1) page = 1; } catch (Exception e) {}

        int catId = 0;
        if (categoryParam != null && !categoryParam.isEmpty()) {
            try { catId = Integer.parseInt(categoryParam); } catch (Exception e) {}
        }

        Double minPrice = null;
        Double maxPrice = null;
        if (minPriceParam != null && !minPriceParam.isEmpty()) { try { minPrice = Double.parseDouble(minPriceParam); } catch (Exception e) {} }
        if (maxPriceParam != null && !maxPriceParam.isEmpty()) { try { maxPrice = Double.parseDouble(maxPriceParam); } catch (Exception e) {} }

        boolean hasFilter = (search != null && !search.trim().isEmpty()) || catId > 0 || minPrice != null || maxPrice != null;

        List<Product> products;
        int totalProducts;

        if (hasFilter || (sort != null && !sort.isEmpty())) {
            products = productDAO.findFiltered(search, catId, minPrice, maxPrice, sort, page, size);
            totalProducts = productDAO.countFiltered(search, catId, minPrice, maxPrice);
        } else {
            totalProducts = productDAO.countAll();
            products = productDAO.findPage(page, size);
        }

        int totalPages = (int) Math.ceil((double) totalProducts / size);
        if (totalPages < 1) totalPages = 1;

        List<Category> categories = categoryDAO.findAll();

        req.setAttribute("products", products);
        req.setAttribute("categories", categories);
        req.setAttribute("page", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalProducts", totalProducts);
        if (search != null) req.setAttribute("search", search);
        if (catId > 0) req.setAttribute("selectedCategory", catId);
        if (minPrice != null) req.setAttribute("minPrice", minPrice);
        if (maxPrice != null) req.setAttribute("maxPrice", maxPrice);
        if (sort != null) req.setAttribute("sort", sort);
        req.getRequestDispatcher("home.jsp").forward(req, resp);
    }
}
