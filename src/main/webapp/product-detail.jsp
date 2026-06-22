<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${product.name} - Shop</title>
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
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
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
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                <c:if test="${not empty product.categoryName}">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home?category=${product.categoryId}">${product.categoryName}</a></li>
                </c:if>
                <li class="breadcrumb-item active">${product.name}</li>
            </ol>
        </nav>

        <div class="row g-4">
            <div class="col-md-5">
                <c:choose>
                    <c:when test="${not empty product.imageUrl}">
                        <img src="${pageContext.request.contextPath}/assets/images/${product.imageUrl}" class="img-fluid rounded shadow" alt="${product.name}">
                    </c:when>
                    <c:otherwise>
                        <div class="no-image rounded shadow d-flex align-items-center justify-content-center" style="height:400px;"><i class="bi bi-image fs-1"></i></div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="col-md-7">
                <h2>${product.name}</h2>
                <c:if test="${not empty product.categoryName}">
                    <p class="text-muted"><i class="bi bi-tag"></i> ${product.categoryName}</p>
                </c:if>
                <h3 class="text-danger fw-bold fs-1 mb-3"><fmt:formatNumber value="${product.price}" pattern="#,###" /> VND</h3>
                <p class="mb-2"><span class="badge bg-${product.stock > 5 ? 'success' : product.stock > 0 ? 'warning' : 'danger'} fs-6">${product.stock > 0 ? 'Còn hàng' : 'Hết hàng'}</span> <span class="text-muted">(${product.stock} sản phẩm)</span></p>
                <c:if test="${not empty product.description}">
                    <p class="mt-3">${product.description}</p>
                </c:if>
                <c:if test="${not empty variantsByType}">
                    <c:forEach var="entry" items="${variantsByType}">
                        <c:set var="typeVariants" value="${entry.value}" />
                        <c:if test="${not empty typeVariants}">
                            <div class="mb-3">
                                <label class="form-label fw-bold">${entry.key == 'COLOR' ? 'Màu sắc' : entry.key == 'CAPACITY' ? 'Dung lượng' : 'Kích thước'}</label>
                                <div class="d-flex flex-wrap gap-2">
                                    <c:forEach var="v" items="${typeVariants}">
                                        <div class="form-check">
                                            <input class="form-check-input variant-radio" type="radio" name="variant_${entry.key}" value="${v.id}" data-adjustment="${v.priceAdjustment}" data-stock="${v.stock}">
                                            <label class="form-check-label">${v.variantName}</label>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </c:if>
                <form action="${pageContext.request.contextPath}/cart" method="post" class="add-to-cart-form mt-4">
                    <input type="hidden" name="productId" value="${product.id}">
                    <div class="row g-2 align-items-center">
                        <div class="col-auto">
                            <input type="number" name="quantity" class="form-control form-control-lg text-center" value="1" min="1" max="${product.stock}" style="width:100px;">
                        </div>
                        <div class="col-auto">
                            <button type="submit" class="btn btn-primary btn-lg" <c:if test="${product.stock <= 0}">disabled</c:if>>
                                <i class="bi bi-cart-plus"></i> Thêm vào giỏ
                            </button>
                        </div>
                        <div class="col-auto">
                            <c:if test="${not empty user}">
                                <button class="btn btn-outline-danger btn-lg wishlist-btn" data-product-id="${product.id}">
                                    <i class="bi bi-heart${isWishlisted ? '-fill' : ''}"></i>
                                </button>
                            </c:if>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <%@ include file="/WEB-INF/footer.jsp" %>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>window.contextPath = '${pageContext.request.contextPath}';</script>
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
