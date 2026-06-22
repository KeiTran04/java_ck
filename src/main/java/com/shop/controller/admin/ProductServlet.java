package com.shop.controller.admin;

import com.shop.dao.CategoryDAO;
import com.shop.model.Category;
import com.shop.model.Product;
import com.shop.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;

@WebServlet("/admin/products")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 5,
    maxRequestSize = 1024 * 1024 * 25
)
public class ProductServlet extends HttpServlet {

    private ProductService productService = new ProductService();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if ("add".equals(action)) {
            req.setAttribute("categories", categoryDAO.findAll());
            req.getRequestDispatcher("/admin/product-form.jsp").forward(req, resp);
            return;
        }
        if ("edit".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            Product product = productService.getProductById(id);
            req.setAttribute("product", product);
            req.setAttribute("categories", categoryDAO.findAll());
            req.getRequestDispatcher("/admin/product-form.jsp").forward(req, resp);
            return;
        }
        if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            productService.deleteProduct(id);
            resp.sendRedirect(req.getContextPath() + "/admin/products");
            return;
        }

        List<Product> products = productService.getAllProducts();
        req.setAttribute("products", products);
        req.getRequestDispatcher("/admin/products.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");
        String name = req.getParameter("name");
        double price = Double.parseDouble(req.getParameter("price"));
        int stock = Integer.parseInt(req.getParameter("stock"));
        String description = req.getParameter("description");
        int categoryId = 0;
        String catParam = req.getParameter("categoryId");
        if (catParam != null && !catParam.isEmpty()) categoryId = Integer.parseInt(catParam);

        Part filePart = req.getPart("imageFile");
        String fileName = extractFileName(filePart);
        String imageUrl = null;

        if (fileName != null && !fileName.isEmpty()) {
            String contentType = filePart.getContentType();
            boolean validType = false;
            for (String t : ALLOWED_TYPES) {
                if (t.equals(contentType)) { validType = true; break; }
            }
            if (!validType) {
                req.setAttribute("error", "Chỉ chấp nhận file ảnh (JPEG, PNG, GIF, WebP)");
                req.setAttribute("product", productService.getProductById(
                    idParam != null ? Integer.parseInt(idParam) : 0));
                req.setAttribute("categories", categoryDAO.findAll());
                req.getRequestDispatcher("/admin/product-form.jsp").forward(req, resp);
                return;
            }

            String uploadDir = getServletContext().getRealPath("/") + "assets" + File.separator + "images";
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) uploadDirFile.mkdirs();

            String uniqueName = System.currentTimeMillis() + "_" + fileName;
            try (InputStream is = filePart.getInputStream();
                 BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(uploadDir + File.separator + uniqueName))) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) bos.write(buffer, 0, bytesRead);
            }
            imageUrl = uniqueName;
        }

        String error;
        if (idParam != null && !idParam.isEmpty()) {
            int id = Integer.parseInt(idParam);
            if (imageUrl == null) {
                Product existing = productService.getProductById(id);
                imageUrl = existing.getImageUrl();
            }
            error = productService.updateProduct(id, name, price, stock, imageUrl, description, categoryId);
        } else {
            error = productService.saveProduct(name, price, stock, imageUrl, description, categoryId);
        }

        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("categories", categoryDAO.findAll());
            req.getRequestDispatcher("/admin/product-form.jsp").forward(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/products");
        }
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String cd : contentDisp.split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
