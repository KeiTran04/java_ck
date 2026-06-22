<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Thanh toán - Shop</title>
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
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/cart">Giỏ hàng</a></li>
                <li class="breadcrumb-item active">Thanh toán</li>
            </ol>
        </nav>

        <h2 class="mb-4"><i class="bi bi-credit-card"></i> Xác nhận đơn hàng</h2>
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible auto-dismiss fade show">${error}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>
        <c:if test="${not empty cart}">
            <div class="row g-4">
                <div class="col-md-8">
                    <div class="card shadow-sm mb-4">
                        <div class="card-header"><h5 class="mb-0"><i class="bi bi-receipt"></i> Sản phẩm</h5></div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-bordered mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th>Sản phẩm</th>
                                            <th class="text-end">Đơn giá</th>
                                            <th class="text-center">SL</th>
                                            <th class="text-end">Thành tiền</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="item" items="${cart}">
                                            <tr>
                                                <td>${item.product.name}</td>
                                                <td class="text-end"><fmt:formatNumber value="${item.product.price}" pattern="#,###" /> VND</td>
                                                <td class="text-center">${item.quantity}</td>
                                                <td class="text-end"><fmt:formatNumber value="${item.subtotal}" pattern="#,###" /> VND</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card shadow-sm mb-4">
                        <div class="card-header"><h5 class="mb-0"><i class="bi bi-ticket"></i> Mã giảm giá</h5></div>
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/checkout" method="post">
                                <input type="hidden" name="action" value="apply-coupon">
                                <div class="input-group">
                                    <input type="text" name="couponCode" class="form-control" placeholder="Nhập mã giảm giá">
                                    <button type="submit" class="btn btn-outline-primary"><i class="bi bi-check"></i> Áp dụng</button>
                                </div>
                            </form>
                            <c:if test="${not empty appliedCoupon}">
                                <div class="alert alert-success mt-2 mb-0">Mã ${appliedCoupon.code}: giảm <fmt:formatNumber value="${discountAmount}" pattern="#,###" /> VND</div>
                            </c:if>
                        </div>
                    </div>
                    <div class="card shadow-sm mb-4">
                        <div class="card-header"><h5 class="mb-0"><i class="bi bi-credit-card"></i> Thanh toán</h5></div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty appliedCoupon}">
                                    <p class="text-muted mb-1">Tạm tính: <span class="float-end"><fmt:formatNumber value="${total}" pattern="#,###" /> VND</span></p>
                                    <p class="text-muted mb-1">Giảm giá: <span class="float-end text-danger">-<fmt:formatNumber value="${discountAmount}" pattern="#,###" /> VND</span></p>
                                    <h4 class="text-end text-danger mb-3"><fmt:formatNumber value="${totalAfterDiscount}" pattern="#,###" /> VND</h4>
                                </c:when>
                                <c:otherwise>
                                    <h4 class="text-end text-danger mb-3"><fmt:formatNumber value="${total}" pattern="#,###" /> VND</h4>
                                </c:otherwise>
                            </c:choose>
                            <form action="${pageContext.request.contextPath}/checkout" method="post">
                                <div class="mb-3">
                                    <label class="form-label">Phương thức thanh toán</label>
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="paymentMethod" value="COD" id="cod" checked>
                                        <label class="form-check-label" for="cod"><i class="bi bi-cash"></i> Thanh toán khi nhận hàng</label>
                                    </div>
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="paymentMethod" value="BANK" id="bank">
                                        <label class="form-check-label" for="bank"><i class="bi bi-bank"></i> Chuyển khoản ngân hàng</label>
                                    </div>
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="paymentMethod" value="MOMO" id="momo">
                                        <label class="form-check-label" for="momo"><i class="bi bi-phone"></i> Ví MoMo</label>
                                    </div>
                                </div>
                                <div class="d-grid gap-2">
                                    <a href="${pageContext.request.contextPath}/cart" class="btn btn-secondary"><i class="bi bi-arrow-left"></i> Quay lại</a>
                                    <button type="submit" class="btn btn-success btn-lg" onclick="this.disabled=true;this.innerHTML='<span class=\'spinner-border spinner-border-sm me-1\'></span>Đang xử lý...';this.form.submit();"><i class="bi bi-check-circle"></i> Xác nhận</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
    </div>

    <div id="loadingSpinner" class="loading-overlay"><div class="spinner-border text-primary" style="width:3rem;height:3rem;"></div></div>
    <%@ include file="/WEB-INF/footer.jsp" %>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>window.contextPath = '${pageContext.request.contextPath}';</script>
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
