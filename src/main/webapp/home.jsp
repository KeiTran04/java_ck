<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Trang chủ - Shop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="d-flex flex-column min-vh-100">
    <c:set var="user" value="${sessionScope.user}" />
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/home"><i class="bi bi-shop"></i> Shop</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/cart"><i class="bi bi-cart3"></i> Giỏ hàng <span id="cart-badge" class="badge bg-danger d-none">0</span></a></li>
                    <c:if test="${not empty user}">
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/orders">Đơn hàng</a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/wishlist"><i class="bi bi-heart"></i> Yêu thích</a></li>
                    </c:if>
                </ul>
                <ul class="navbar-nav">
                    <li class="nav-item"><button id="darkModeToggle" class="btn btn-outline-light btn-sm me-2"><i class="bi bi-moon"></i></button></li>
                    <c:choose>
                        <c:when test="${not empty user}">
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                                    <i class="bi bi-person-circle"></i> ${user.fullName != null && user.fullName != '' ? user.fullName : user.username}
                                </a>
                                <ul class="dropdown-menu dropdown-menu-end">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person"></i> Thông tin</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile?action=change-password"><i class="bi bi-key"></i> Đổi mật khẩu</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/wishlist"><i class="bi bi-heart"></i> Yêu thích</a></li>
                                    <c:if test="${user.role == 'ADMIN'}">
                                        <li><hr class="dropdown-divider"></li>
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/dashboard"><i class="bi bi-gear"></i> Quản trị</a></li>
                                    </c:if>
                                    <li><hr class="dropdown-divider"></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout"><i class="bi bi-box-arrow-right"></i> Đăng xuất</a></li>
                                </ul>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="nav-item"><a class="nav-link" href="login.jsp"><i class="bi bi-box-arrow-in-right"></i> Đăng nhập</a></li>
                            <li class="nav-item"><a class="nav-link" href="register.jsp">Đăng ký</a></li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container my-4 flex-grow-1">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item active">Trang chủ</li>
            </ol>
        </nav>

        <div class="row g-3 mb-4">
            <div class="col-md-6">
                <form action="${pageContext.request.contextPath}/home" method="get" class="input-group">
                    <input type="text" name="search" class="form-control" placeholder="Tìm sản phẩm..." value="${search}">
                    <button type="submit" class="btn btn-primary"><i class="bi bi-search"></i></button>
                </form>
            </div>
            <div class="col-md-3">
                <form action="${pageContext.request.contextPath}/home" method="get" id="categoryForm">
                    <select name="category" class="form-select" onchange="this.form.submit()">
                        <option value="">Tất cả danh mục</option>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.id}" ${selectedCategory == cat.id ? 'selected' : ''}>${cat.name}</option>
                        </c:forEach>
                    </select>
                    <c:if test="${not empty search}"><input type="hidden" name="search" value="${search}"></c:if>
                    <c:if test="${not empty minPrice}"><input type="hidden" name="minPrice" value="${minPrice}"></c:if>
                    <c:if test="${not empty maxPrice}"><input type="hidden" name="maxPrice" value="${maxPrice}"></c:if>
                    <c:if test="${not empty sort}"><input type="hidden" name="sort" value="${sort}"></c:if>
                </form>
            </div>
            <div class="col-md-3">
                <form action="${pageContext.request.contextPath}/home" method="get" id="sortForm">
                    <select name="sort" class="form-select" onchange="this.form.submit()">
                        <option value="">Mặc định</option>
                        <option value="price_asc" ${sort == 'price_asc' ? 'selected' : ''}>Giá: Thấp → Cao</option>
                        <option value="price_desc" ${sort == 'price_desc' ? 'selected' : ''}>Giá: Cao → Thấp</option>
                        <option value="name_asc" ${sort == 'name_asc' ? 'selected' : ''}>Tên: A → Z</option>
                        <option value="name_desc" ${sort == 'name_desc' ? 'selected' : ''}>Tên: Z → A</option>
                    </select>
                    <c:if test="${not empty search}"><input type="hidden" name="search" value="${search}"></c:if>
                    <c:if test="${selectedCategory > 0}"><input type="hidden" name="category" value="${selectedCategory}"></c:if>
                    <c:if test="${not empty minPrice}"><input type="hidden" name="minPrice" value="${minPrice}"></c:if>
                    <c:if test="${not empty maxPrice}"><input type="hidden" name="maxPrice" value="${maxPrice}"></c:if>
                </form>
            </div>
        </div>

        <div class="row g-3 mb-3">
            <div class="col-md-8">
                <form action="${pageContext.request.contextPath}/home" method="get" class="row g-2">
                    <div class="col-auto">
                        <div class="input-group input-group-sm">
                            <span class="input-group-text">Giá</span>
                            <input type="number" name="minPrice" class="form-control" placeholder="Từ" value="${minPrice}" min="0" style="max-width:120px">
                            <span class="input-group-text">-</span>
                            <input type="number" name="maxPrice" class="form-control" placeholder="Đến" value="${maxPrice}" min="0" style="max-width:120px">
                            <button type="submit" class="btn btn-outline-primary btn-sm"><i class="bi bi-funnel"></i> Lọc</button>
                        </div>
                    </div>
                    <c:if test="${not empty search}"><input type="hidden" name="search" value="${search}"></c:if>
                    <c:if test="${selectedCategory > 0}"><input type="hidden" name="category" value="${selectedCategory}"></c:if>
                    <c:if test="${not empty sort}"><input type="hidden" name="sort" value="${sort}"></c:if>
                </form>
            </div>
            <div class="col-md-4 text-md-end">
                <small class="text-muted">${totalProducts} sản phẩm</small>
            </div>
        </div>

        <h2 class="mb-4"><i class="bi bi-grid"></i> Sản phẩm</h2>
        <c:if test="${empty products}">
            <div class="alert alert-info">Không tìm thấy sản phẩm nào.</div>
        </c:if>
        <div class="row g-4 products-grid">
            <c:forEach var="p" items="${products}">
                <div class="col-6 col-md-4 col-lg-3">
                    <div class="card h-100 shadow-sm">
                        <a href="${pageContext.request.contextPath}/product?id=${p.id}">
                            <c:choose>
                                <c:when test="${not empty p.imageUrl}">
                                    <img src="${pageContext.request.contextPath}/assets/images/${p.imageUrl}" class="card-img-top" alt="${p.name}">
                                </c:when>
                                <c:otherwise>
                                    <div class="no-image card-img-top d-flex align-items-center justify-content-center"><i class="bi bi-image"></i></div>
                                </c:otherwise>
                            </c:choose>
                        </a>
                        <div class="card-body d-flex flex-column">
                            <h5 class="card-title"><a href="${pageContext.request.contextPath}/product?id=${p.id}" class="text-decoration-none text-dark">${p.name}</a></h5>
                            <c:if test="${not empty p.categoryName}">
                                <p class="text-muted small mb-1"><i class="bi bi-tag"></i> ${p.categoryName}</p>
                            </c:if>
                            <p class="price mb-1"><fmt:formatNumber value="${p.price}" pattern="#,###" /> VND</p>
                            <p class="stock mb-2"><i class="bi bi-box"></i> ${p.stock}</p>
                            <form action="${pageContext.request.contextPath}/cart" method="post" class="add-to-cart-form mt-auto">
                                <input type="hidden" name="productId" value="${p.id}">
                                <div class="input-group input-group-sm">
                                    <input type="number" name="quantity" class="form-control text-center" value="1" min="1" max="${p.stock}">
                                    <button type="submit" class="btn btn-primary" <c:if test="${p.stock <= 0}">disabled</c:if>><i class="bi bi-cart-plus"></i></button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <c:if test="${totalPages > 1}">
            <c:set var="queryString" value="" />
            <c:if test="${not empty search}"><c:set var="queryString" value="${queryString}&search=${search}" /></c:if>
            <c:if test="${selectedCategory > 0}"><c:set var="queryString" value="${queryString}&category=${selectedCategory}" /></c:if>
            <c:if test="${not empty minPrice}"><c:set var="queryString" value="${queryString}&minPrice=${minPrice}" /></c:if>
            <c:if test="${not empty maxPrice}"><c:set var="queryString" value="${queryString}&maxPrice=${maxPrice}" /></c:if>
            <c:if test="${not empty sort}"><c:set var="queryString" value="${queryString}&sort=${sort}" /></c:if>
            <nav class="mt-4">
                <ul class="pagination justify-content-center">
                    <li class="page-item ${page <= 1 ? 'disabled' : ''}">
                        <a class="page-link" href="${pageContext.request.contextPath}/home?page=${page-1}${queryString}"><i class="bi bi-chevron-left"></i></a>
                    </li>
                    <c:forEach var="i" begin="1" end="${totalPages}">
                        <li class="page-item ${page == i ? 'active' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/home?page=${i}${queryString}">${i}</a>
                        </li>
                    </c:forEach>
                    <li class="page-item ${page >= totalPages ? 'disabled' : ''}">
                        <a class="page-link" href="${pageContext.request.contextPath}/home?page=${page+1}${queryString}"><i class="bi bi-chevron-right"></i></a>
                    </li>
                </ul>
            </nav>
        </c:if>
    </div>

    <%@ include file="/WEB-INF/footer.jsp" %>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>window.contextPath = '${pageContext.request.contextPath}';</script>
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
