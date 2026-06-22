<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:choose><c:when test="${not empty product}">Sửa</c:when><c:otherwise>Thêm</c:otherwise></c:choose> sản phẩm - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="d-flex flex-column min-vh-100">
    <c:set var="user" value="${sessionScope.user}" />
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/home"><i class="bi bi-shop"></i> Shop Admin</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                    <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/admin/products">Sản phẩm</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/orders">Đơn hàng</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/coupons">Mã giảm giá</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/users">Người dùng</a></li>
                </ul>
                <ul class="navbar-nav">
                    <li class="nav-item"><button id="darkModeToggle" class="btn btn-outline-light btn-sm me-2"><i class="bi bi-moon"></i></button></li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                            <i class="bi bi-person-circle"></i> ${user.fullName != null && user.fullName != '' ? user.fullName : user.username}
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person"></i> Thông tin</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile?action=change-password"><i class="bi bi-key"></i> Đổi mật khẩu</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/wishlist"><i class="bi bi-heart"></i> Yêu thích</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout"><i class="bi bi-box-arrow-right"></i> Đăng xuất</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container my-4 flex-grow-1">
        <c:set var="isEdit" value="${not empty product}" />
        <nav aria-label="breadcrumb"><ol class="breadcrumb"><li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li><li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/products">Sản phẩm</a></li><li class="breadcrumb-item active">${isEdit ? 'Sửa' : 'Thêm'}</li></ol></nav>
        <h2 class="mb-4"><i class="bi bi-${isEdit ? 'pencil' : 'plus-lg'}"></i> ${isEdit ? 'Sửa' : 'Thêm'} sản phẩm</h2>
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible auto-dismiss fade show">${error}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/admin/products" method="post" enctype="multipart/form-data" class="needs-validation" novalidate>
                            <input type="hidden" name="action" value="${isEdit ? 'update' : 'insert'}">
                            <c:if test="${isEdit}"><input type="hidden" name="id" value="${product.id}"></c:if>
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label for="name" class="form-label">Tên sản phẩm</label>
                                    <input type="text" class="form-control" id="name" name="name" value="${product.name}" required autofocus>
                                </div>
                                <div class="col-md-3">
                                    <label for="price" class="form-label">Giá</label>
                                    <input type="number" class="form-control" id="price" name="price" step="1" min="0" value="${product.price}" required>
                                </div>
                                <div class="col-md-3">
                                    <label for="stock" class="form-label">Số lượng</label>
                                    <input type="number" class="form-control" id="stock" name="stock" min="0" value="${product.stock}" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="categoryId" class="form-label">Danh mục</label>
                                    <select class="form-select" id="categoryId" name="categoryId">
                                        <option value="">-- Chọn danh mục --</option>
                                        <c:forEach var="cat" items="${categories}">
                                            <option value="${cat.id}" ${product.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label for="imageFile" class="form-label">Ảnh</label>
                                    <input type="file" class="form-control image-preview-input" id="imageFile" name="imageFile" accept="image/png,image/jpeg,image/gif,image/webp" data-preview="imagePreview">
                                    <c:if test="${isEdit and not empty product.imageUrl}">
                                        <small class="text-muted">Hiện tại: ${product.imageUrl}</small>
                                    </c:if>
                                    <img id="imagePreview" class="img-fluid mt-2 d-none" style="max-width:200px;max-height:200px;">
                                </div>
                                <div class="col-12">
                                    <label for="description" class="form-label">Mô tả</label>
                                    <textarea class="form-control" id="description" name="description" rows="3">${product.description}</textarea>
                                </div>
                            </div>
                            <div class="mt-3 d-flex gap-2">
                                <button type="submit" class="btn btn-primary">${isEdit ? 'Cập nhật' : 'Thêm mới'}</button>
                                <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-secondary">Hủy</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%@ include file="/WEB-INF/footer.jsp" %>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>window.contextPath = '${pageContext.request.contextPath}';</script>
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
