<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đơn hàng của tôi - Shop</title>
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
                    <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/orders">Đơn hàng</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/wishlist"><i class="bi bi-heart"></i> Yêu thích</a></li>
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
        <h2 class="mb-4"><i class="bi bi-receipt"></i> Đơn hàng của tôi</h2>
        <c:choose>
            <c:when test="${empty orders}">
                <div class="text-center py-5">
                    <i class="bi bi-inbox text-muted" style="font-size:4rem;"></i>
                    <p class="mt-3">Bạn chưa có đơn hàng nào.</p>
                    <a href="${pageContext.request.contextPath}/home" class="btn btn-primary"><i class="bi bi-shop"></i> Mua sắm ngay</a>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="o" items="${orders}">
                    <div class="card shadow-sm mb-3">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <strong>Đơn hàng #${o.id}</strong>
                            <div>
                                <span class="badge bg-${o.status == 'PENDING' ? 'warning' : o.status == 'PROCESSING' ? 'info' : o.status == 'SHIPPED' ? 'primary' : o.status == 'COMPLETED' ? 'success' : 'secondary'} me-2">${o.status}</span>
                                <c:if test="${o.status == 'PENDING'}">
                                    <form action="${pageContext.request.contextPath}/orders" method="post" style="display:inline;" onsubmit="return confirm('Bạn có chắc muốn hủy đơn hàng #${o.id}?')">
                                        <input type="hidden" name="action" value="cancel">
                                        <input type="hidden" name="id" value="${o.id}">
                                        <button type="submit" class="btn btn-sm btn-outline-danger"><i class="bi bi-x-circle"></i> Hủy</button>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                        <div class="card-body">
                            <p class="mb-2 text-muted"><i class="bi bi-calendar"></i> <fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm" /></p>
                            <p class="mb-2"><strong>Tổng tiền: <span class="text-danger"><fmt:formatNumber value="${o.totalAmount}" pattern="#,###" /> VND</span></strong>
                            <c:if test="${not empty o.invoicePath}">
                                <a href="${pageContext.request.contextPath}/orders?action=download-invoice&id=${o.id}" class="btn btn-sm btn-outline-info ms-2"><i class="bi bi-download"></i> Tải hóa đơn</a>
                            </c:if>
                            </p>
                            <div class="table-responsive">
                                <table class="table table-sm table-bordered mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th>Sản phẩm</th>
                                            <th class="text-center">SL</th>
                                            <th class="text-end">Đơn giá</th>
                                            <th class="text-end">Thành tiền</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="detail" items="${o.details}">
                                            <tr>
                                                <td>${detail.productName}</td>
                                                <td class="text-center">${detail.quantity}</td>
                                                <td class="text-end"><fmt:formatNumber value="${detail.price}" pattern="#,###" /> VND</td>
                                                <td class="text-end"><fmt:formatNumber value="${detail.subtotal}" pattern="#,###" /> VND</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

    <%@ include file="/WEB-INF/footer.jsp" %>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>window.contextPath = '${pageContext.request.contextPath}';</script>
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
