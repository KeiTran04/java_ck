<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Yêu thích - Shop</title>
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
                        <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/wishlist"><i class="bi bi-heart"></i> Yêu thích</a></li>
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
                <li class="breadcrumb-item active">Yêu thích</li>
            </ol>
        </nav>

        <h2 class="mb-4"><i class="bi bi-heart"></i> Sản phẩm yêu thích</h2>
        <c:choose>
            <c:when test="${empty wishlist}">
                <div class="text-center py-5">
                    <i class="bi bi-heartbreak text-muted" style="font-size:4rem;"></i>
                    <p class="mt-3">Bạn chưa có sản phẩm yêu thích nào.</p>
                    <a href="${pageContext.request.contextPath}/home" class="btn btn-primary"><i class="bi bi-shop"></i> Mua sắm ngay</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="row g-4 products-grid">
                    <c:forEach var="item" items="${wishlist}">
                        <div class="col-6 col-md-4 col-lg-3">
                            <div class="card h-100 shadow-sm">
                                <a href="${pageContext.request.contextPath}/product?id=${item.productId}">
                                    <c:choose>
                                        <c:when test="${not empty item.productImage}">
                                            <img src="${pageContext.request.contextPath}/assets/images/${item.productImage}" class="card-img-top" alt="${item.productName}">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="no-image card-img-top d-flex align-items-center justify-content-center"><i class="bi bi-image"></i></div>
                                        </c:otherwise>
                                    </c:choose>
                                </a>
                                <div class="card-body d-flex flex-column">
                                    <h5 class="card-title"><a href="${pageContext.request.contextPath}/product?id=${item.productId}" class="text-decoration-none text-dark">${item.productName}</a></h5>
                                    <p class="price mb-2"><fmt:formatNumber value="${item.productPrice}" pattern="#,###" /> VND</p>
                                    <a href="${pageContext.request.contextPath}/wishlist?action=remove&productId=${item.productId}" class="btn btn-outline-danger btn-sm mt-auto" onclick="return confirm('Xóa khỏi danh sách yêu thích?')"><i class="bi bi-heartbreak"></i> Bỏ yêu thích</a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <%@ include file="/WEB-INF/footer.jsp" %>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>window.contextPath = '${pageContext.request.contextPath}';</script>
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
