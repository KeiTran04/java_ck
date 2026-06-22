<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dashboard - Admin</title>
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
                    <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/products">Sản phẩm</a></li>
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
        <h2 class="mb-4"><i class="bi bi-speedometer2"></i> Dashboard</h2>
        <div class="row g-3 mb-4">
            <div class="col-6 col-md-3">
                <div class="stat-card">
                    <i class="bi bi-people fs-1 text-primary"></i>
                    <h3 class="stat-number">${totalUsers}</h3>
                    <p class="text-muted">Người dùng</p>
                </div>
            </div>
            <div class="col-6 col-md-3">
                <div class="stat-card">
                    <i class="bi bi-box fs-1 text-success"></i>
                    <h3 class="stat-number">${totalProducts}</h3>
                    <p class="text-muted">Sản phẩm</p>
                </div>
            </div>
            <div class="col-6 col-md-3">
                <div class="stat-card">
                    <i class="bi bi-receipt fs-1 text-warning"></i>
                    <h3 class="stat-number">${totalOrders}</h3>
                    <p class="text-muted">Đơn hàng</p>
                </div>
            </div>
            <div class="col-6 col-md-3">
                <div class="stat-card">
                    <i class="bi bi-currency-dollar fs-1 text-danger"></i>
                    <h3 class="stat-number"><fmt:formatNumber value="${totalRevenue}" pattern="#,###" /></h3>
                    <p class="text-muted">VND</p>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="col-md-8 mb-4">
                <div class="card shadow-sm">
                    <div class="card-header"><h5 class="mb-0"><i class="bi bi-bar-chart"></i> Thống kê</h5></div>
                    <div class="card-body">
                        <canvas id="statsChart" height="200"></canvas>
                    </div>
                </div>
            </div>
            <div class="col-md-4 mb-4">
                <div class="card shadow-sm">
                    <div class="card-header"><h5 class="mb-0"><i class="bi bi-box-seam"></i> Tồn kho</h5></div>
                    <div class="card-body">
                        <p>Tổng tồn kho: <strong>${totalStock}</strong></p>
                        <canvas id="stockChart" height="200"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <h3 class="mb-3">Đơn hàng gần đây</h3>
        <c:choose>
            <c:when test="${empty recentOrders}">
                <div class="alert alert-info">Chưa có đơn hàng nào.</div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-bordered table-hover bg-white">
                        <thead class="table-light">
                            <tr>
                                <th>Mã ĐH</th>
                                <th>Người mua</th>
                                <th>Ngày đặt</th>
                                <th class="text-end">Tổng tiền</th>
                                <th>Trạng thái</th>
                                <th class="text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="o" items="${recentOrders}">
                                <tr>
                                    <td>#${o.id}</td>
                                    <td>${o.username}</td>
                                    <td><fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm" /></td>
                                    <td class="text-end"><fmt:formatNumber value="${o.totalAmount}" pattern="#,###" /> VND</td>
                                    <td><span class="badge bg-${o.status == 'PENDING' ? 'warning' : o.status == 'PROCESSING' ? 'info' : o.status == 'SHIPPED' ? 'primary' : o.status == 'COMPLETED' ? 'success' : 'secondary'}">${o.status}</span></td>
                                    <td class="text-center"><a href="${pageContext.request.contextPath}/admin/orders?action=view&id=${o.id}" class="btn btn-sm btn-outline-primary">Xem</a></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <%@ include file="/WEB-INF/footer.jsp" %>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script>
        var ctx = document.getElementById('statsChart').getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['Sản phẩm', 'Đơn hàng', 'Người dùng'],
                datasets: [{
                    label: 'Số lượng',
                    data: [${totalProducts}, ${totalOrders}, ${totalUsers}],
                    backgroundColor: ['#198754', '#ffc107', '#0d6efd']
                }]
            }
        });
        var ctx2 = document.getElementById('stockChart').getContext('2d');
        new Chart(ctx2, {
            type: 'doughnut',
            data: {
                labels: ['Tồn kho'],
                datasets: [{
                    data: [${totalStock}, 1],
                    backgroundColor: ['#0d6efd', '#e9ecef']
                }]
            }
        });
    </script>
    <script>window.contextPath = '${pageContext.request.contextPath}';</script>
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
