<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    Integer statusCode = (Integer) pageContext.getAttribute("javax.servlet.error.status_code", PageContext.REQUEST_SCOPE);
    if (statusCode == null) statusCode = response.getStatus();
    pageContext.setAttribute("sc", statusCode);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${sc == 404 ? 'Không tìm thấy' : sc == 500 ? 'Lỗi máy chủ' : 'Lỗi'}"/> - Shop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="d-flex flex-column min-vh-100 bg-light">
    <c:set var="user" value="${sessionScope.user}" />
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/home"><i class="bi bi-shop"></i> Shop</a>
        </div>
    </nav>
    <div class="container d-flex flex-column justify-content-center flex-grow-1">
        <div class="row justify-content-center">
            <div class="col-md-6 text-center">
                <c:choose>
                    <c:when test="${sc == 404}">
                        <i class="bi bi-emoji-frown text-warning" style="font-size:5rem;"></i>
                        <h1 class="display-1 fw-bold text-muted">404</h1>
                        <h3 class="mb-3">Trang không tìm thấy</h3>
                        <p class="text-muted">Trang bạn đang tìm không tồn tại hoặc đã bị di chuyển.</p>
                    </c:when>
                    <c:when test="${sc == 500}">
                        <i class="bi bi-gear text-danger" style="font-size:5rem;"></i>
                        <h1 class="display-1 fw-bold text-muted">500</h1>
                        <h3 class="mb-3">Lỗi máy chủ</h3>
                        <p class="text-muted">Đã có lỗi xảy ra. Vui lòng thử lại sau.</p>
                    </c:when>
                    <c:when test="${sc == 403}">
                        <i class="bi bi-shield-exclamation text-danger" style="font-size:5rem;"></i>
                        <h1 class="display-1 fw-bold text-muted">403</h1>
                        <h3 class="mb-3">Truy cập bị từ chối</h3>
                        <p class="text-muted">Bạn không có quyền truy cập trang này.</p>
                    </c:when>
                    <c:otherwise>
                        <i class="bi bi-exclamation-triangle-fill text-danger" style="font-size:5rem;"></i>
                        <h1 class="display-1 fw-bold text-muted">${sc}</h1>
                        <h3 class="mb-3">Đã xảy ra lỗi</h3>
                        <c:if test="${not empty message}">
                            <p class="text-muted">${message}</p>
                        </c:if>
                        <p class="text-muted">Vui lòng thử lại sau.</p>
                    </c:otherwise>
                </c:choose>
                <a href="${pageContext.request.contextPath}/home" class="btn btn-primary btn-lg mt-3"><i class="bi bi-house"></i> Về trang chủ</a>
            </div>
        </div>
    </div>
    <%@ include file="/WEB-INF/footer.jsp" %>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
