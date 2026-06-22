<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${not empty coupon ? 'Sửa' : 'Thêm'} mã giảm giá - Admin</title>
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
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/coupons">Mã giảm giá</a></li>
                <li class="breadcrumb-item active">${not empty coupon ? 'Sửa' : 'Thêm'} mã</li>
            </ol>
        </nav>

        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card shadow-sm">
                    <div class="card-header"><h5 class="mb-0">${not empty coupon ? 'Sửa mã giảm giá' : 'Thêm mã giảm giá mới'}</h5></div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/admin/coupons" method="post">
                            <c:if test="${not empty coupon}">
                                <input type="hidden" name="id" value="${coupon.id}">
                            </c:if>
                            <div class="mb-3">
                                <label class="form-label">Mã giảm giá <span class="text-danger">*</span></label>
                                <input type="text" name="code" class="form-control text-uppercase" value="${coupon.code}" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Loại giảm giá <span class="text-danger">*</span></label>
                                <select name="discountType" class="form-select" required>
                                    <option value="PERCENTAGE" ${coupon.discountType == 'PERCENTAGE' ? 'selected' : ''}>Phần trăm (%)</option>
                                    <option value="FIXED" ${coupon.discountType == 'FIXED' ? 'selected' : ''}>Số tiền (VNĐ)</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Giá trị <span class="text-danger">*</span></label>
                                <input type="number" name="discountValue" class="form-control" value="${coupon.discountValue}" min="1" required>
                                <small class="text-muted">Nhập % (vd: 10) hoặc số tiền (vd: 50000)</small>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Đơn hàng tối thiểu</label>
                                <input type="number" name="minOrderAmount" class="form-control" value="${coupon.minOrderAmount}" min="0">
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Giới hạn lượt dùng (0 = không giới hạn)</label>
                                <input type="number" name="maxUsage" class="form-control" value="${coupon.maxUsage}" min="0">
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Ngày hết hạn</label>
                                <input type="date" name="expiryDate" class="form-control" value="${not empty coupon.expiryDate ? coupon.expiryDate.toString().substring(0,10) : ''}">
                            </div>
                            <button type="submit" class="btn btn-primary w-100"><i class="bi bi-save"></i> Lưu</button>
                            <a href="${pageContext.request.contextPath}/admin/coupons" class="btn btn-secondary w-100 mt-2"><i class="bi bi-arrow-left"></i> Quay lại</a>
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