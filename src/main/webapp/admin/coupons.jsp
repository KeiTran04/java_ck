<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quản lý mã giảm giá - Admin</title>
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
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/products">Sản phẩm</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/orders">Đơn hàng</a></li>
                    <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/admin/coupons">Mã giảm giá</a></li>
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
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/home"><i class="bi bi-shop"></i> Về trang chủ</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout"><i class="bi bi-box-arrow-right"></i> Đăng xuất</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container my-4 flex-grow-1">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h3><i class="bi bi-ticket"></i> Mã giảm giá</h3>
            <a href="${pageContext.request.contextPath}/admin/coupons?action=create" class="btn btn-primary"><i class="bi bi-plus-circle"></i> Thêm mã</a>
        </div>

        <c:if test="${empty coupons}">
            <div class="alert alert-info">Chưa có mã giảm giá nào.</div>
        </c:if>

        <c:if test="${not empty coupons}">
            <div class="table-responsive">
                <table class="table table-bordered table-hover">
                    <thead class="table-dark">
                        <tr>
                            <th>ID</th>
                            <th>Mã</th>
                            <th>Loại</th>
                            <th>Giá trị</th>
                            <th>Đơn tối thiểu</th>
                            <th>Đã dùng</th>
                            <th>Giới hạn</th>
                            <th>Hết hạn</th>
                            <th>Kích hoạt</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="c" items="${coupons}">
                            <tr class="${not c.active ? 'table-secondary' : ''}">
                                <td>${c.id}</td>
                                <td><strong>${c.code}</strong></td>
                                <td>${c.discountType == 'PERCENTAGE' ? '%' : 'VNĐ'}</td>
                                <td><fmt:formatNumber value="${c.discountValue}" pattern="#,###" />${c.discountType == 'PERCENTAGE' ? '%' : ' VND'}</td>
                                <td><fmt:formatNumber value="${c.minOrderAmount}" pattern="#,###" /> VND</td>
                                <td>${c.usedCount}</td>
                                <td>${c.maxUsage == 0 ? '∞' : c.maxUsage}</td>
                                <td><c:if test="${not empty c.expiryDate}"><fmt:formatDate value="${c.expiryDate}" pattern="dd/MM/yyyy" /></c:if><c:if test="${empty c.expiryDate}">-</c:if></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/coupons?action=toggle&id=${c.id}" class="btn btn-sm ${c.active ? 'btn-success' : 'btn-secondary'}">
                                        <i class="bi ${c.active ? 'bi-check-circle' : 'bi-x-circle'}"></i> ${c.active ? 'Bật' : 'Tắt'}
                                    </a>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/coupons?action=edit&id=${c.id}" class="btn btn-sm btn-warning"><i class="bi bi-pencil"></i></a>
                                    <form action="${pageContext.request.contextPath}/admin/coupons" method="post" style="display:inline;" onsubmit="return confirm('Vô hiệu hóa mã ${c.code}?')">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${c.id}">
                                        <button type="submit" class="btn btn-sm btn-danger"><i class="bi bi-trash"></i></button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>

    <%@ include file="/WEB-INF/footer.jsp" %>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>window.contextPath = '${pageContext.request.contextPath}';</script>
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>