<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quản lý đơn hàng - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://cdn.datatables.net/1.13.6/css/dataTables.bootstrap5.min.css" rel="stylesheet">
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
                    <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/admin/orders">Đơn hàng</a></li>
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
        <h2 class="mb-4"><i class="bi bi-receipt"></i> Quản lý đơn hàng</h2>
        <c:choose>
            <c:when test="${not empty order}">
                <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-outline-secondary mb-3"><i class="bi bi-arrow-left"></i> Quay lại</a>
                <div class="card shadow-sm mb-3">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">Chi tiết đơn hàng #${order.id}</h5>
                        <span class="badge bg-${order.status == 'PENDING' ? 'warning' : order.status == 'PROCESSING' ? 'info' : order.status == 'SHIPPED' ? 'primary' : order.status == 'COMPLETED' ? 'success' : 'secondary'} fs-6">${order.status}</span>
                    </div>
                    <div class="card-body">
                        <p><strong>Người mua:</strong> ${order.username}</p>
                        <p><strong>Ngày đặt:</strong> <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm" /></p>
                        <p><strong>Tổng tiền:</strong> <span class="text-danger"><fmt:formatNumber value="${order.totalAmount}" pattern="#,###" /> VND</span></p>

                        <h6 class="mt-3">Chi tiết:</h6>
                        <div class="table-responsive">
                            <table class="table table-sm table-bordered">
                                <thead class="table-light">
                                    <tr>
                                        <th>Sản phẩm</th>
                                        <th class="text-center">SL</th>
                                        <th class="text-end">Đơn giá</th>
                                        <th class="text-end">Thành tiền</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="detail" items="${order.details}">
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

                        <form action="${pageContext.request.contextPath}/admin/orders" method="post" class="mt-3">
                            <input type="hidden" name="action" value="updateStatus">
                            <input type="hidden" name="id" value="${order.id}">
                            <div class="row g-2 align-items-center">
                                <div class="col-auto">
                                    <select name="status" class="form-select">
                                        <option value="PENDING" ${order.status == 'PENDING' ? 'selected' : ''}>Chờ xử lý</option>
                                        <option value="PROCESSING" ${order.status == 'PROCESSING' ? 'selected' : ''}>Đang xử lý</option>
                                        <option value="SHIPPED" ${order.status == 'SHIPPED' ? 'selected' : ''}>Đã giao hàng</option>
                                        <option value="COMPLETED" ${order.status == 'COMPLETED' ? 'selected' : ''}>Hoàn thành</option>
                                        <option value="CANCELLED" ${order.status == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option>
                                    </select>
                                </div>
                                <div class="col-auto">
                                    <button type="submit" class="btn btn-primary"><i class="bi bi-check-lg"></i> Cập nhật</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${empty orders}">
                        <div class="alert alert-info">Chưa có đơn hàng nào.</div>
                    </c:when>
                    <c:otherwise>
                        <div class="card shadow-sm">
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-bordered table-hover mb-0" id="ordersTable">
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
                                            <c:forEach var="o" items="${orders}">
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
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    </div>

    <%@ include file="/WEB-INF/footer.jsp" %>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.6/js/dataTables.bootstrap5.min.js"></script>
    <script>window.contextPath = '${pageContext.request.contextPath}';</script>
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
    <c:if test="${empty order}">
    <script>$('#ordersTable').DataTable({order:[[0,'desc']],language:{url:'https://cdn.datatables.net/plug-ins/1.13.6/i18n/vi.json'}});</script>
    </c:if>
</body>
</html>
