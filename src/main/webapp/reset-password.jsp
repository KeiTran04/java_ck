<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đặt lại mật khẩu - Shop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="bg-light">
    <div class="container">
        <div class="row justify-content-center mt-5">
            <div class="col-md-5 col-lg-4">
                <div class="card shadow-sm">
                    <div class="card-body p-4">
                        <h2 class="text-center mb-4"><i class="bi bi-shield-lock"></i> Đặt lại mật khẩu</h2>
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible auto-dismiss fade show">${error}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
                        </c:if>
                        <form action="${pageContext.request.contextPath}/forgot-password" method="post" class="needs-validation" novalidate>
                            <input type="hidden" name="action" value="reset-password">
                            <input type="hidden" name="token" value="${token}">
                            <div class="mb-3">
                                <label for="newPassword" class="form-label">Mật khẩu mới (≥ 6 ký tự)</label>
                                <input type="password" class="form-control" id="newPassword" name="newPassword" minlength="6" required autofocus>
                                <div class="invalid-feedback">Mật khẩu phải có ít nhất 6 ký tự</div>
                            </div>
                            <div class="mb-3">
                                <label for="confirmPassword" class="form-label">Xác nhận mật khẩu</label>
                                <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                                <div class="invalid-feedback">Mật khẩu xác nhận không khớp</div>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Đặt lại mật khẩu</button>
                        </form>
                        <p class="text-center mt-3 mb-0"><a href="login.jsp"><i class="bi bi-arrow-left"></i> Quay lại đăng nhập</a></p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
