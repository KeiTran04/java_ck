document.addEventListener('DOMContentLoaded', function () {

    // --- Dark Mode ---
    var darkToggle = document.getElementById('darkModeToggle');
    if (darkToggle) {
        var saved = localStorage.getItem('darkMode');
        if (saved === 'true') document.body.classList.add('dark-mode');

        darkToggle.addEventListener('click', function () {
            document.body.classList.toggle('dark-mode');
            localStorage.setItem('darkMode', document.body.classList.contains('dark-mode'));
            var icon = darkToggle.querySelector('i');
            if (icon) {
                icon.className = document.body.classList.contains('dark-mode') ? 'bi bi-sun-fill' : 'bi bi-moon';
            }
        });
    }

    // --- Cart AJAX ---
    function serializeForm(form) {
        var pairs = [];
        var elements = form.querySelectorAll('[name]');
        for (var i = 0; i < elements.length; i++) {
            var el = elements[i];
            if (el.name && !el.disabled) {
                pairs.push(encodeURIComponent(el.name) + '=' + encodeURIComponent(el.value));
            }
        }
        return pairs.join('&');
    }

    document.querySelectorAll('.add-to-cart-form').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            e.preventDefault();
            var btn = form.querySelector('button[type="submit"]');
            var originalText = btn.innerHTML;
            btn.disabled = true;
            btn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Đang thêm...';

            var xhr = new XMLHttpRequest();
            xhr.open('POST', form.action || window.location.href, true);
            xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
            xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
            xhr.onload = function () {
                btn.disabled = false;
                btn.innerHTML = originalText;
                if (xhr.status === 200) {
                    try {
                        var data = JSON.parse(xhr.responseText);
                        if (data.success) {
                            updateCartBadge(data.count);
                            showToast('Thành công', 'Đã thêm vào giỏ hàng!', 'success');
                        } else {
                            showToast('Lỗi', data.error || 'Không thể thêm vào giỏ hàng', 'error');
                        }
                    } catch (e) {
                        showToast('Lỗi', 'Phản hồi không hợp lệ từ máy chủ', 'error');
                    }
                } else {
                    showToast('Lỗi', 'Lỗi kết nối máy chủ', 'error');
                }
            };
            xhr.onerror = function () {
                btn.disabled = false;
                btn.innerHTML = originalText;
                showToast('Lỗi', 'Không thể kết nối đến máy chủ', 'error');
            };
            xhr.send(serializeForm(form));
        });
    });

    document.querySelectorAll('.remove-from-cart').forEach(function (link) {
        link.addEventListener('click', function (e) {
            e.preventDefault();
            var xhr = new XMLHttpRequest();
            xhr.open('GET', this.href, true);
            xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
            xhr.onload = function () {
                if (xhr.status === 200) {
                    try {
                        var data = JSON.parse(xhr.responseText);
                        if (data.success) {
                            updateCartBadge(data.count);
                            location.reload();
                        } else {
                            showToast('Lỗi', 'Không thể xóa sản phẩm', 'error');
                        }
                    } catch (e) {
                        location.reload();
                    }
                } else {
                    location.reload();
                }
            };
            xhr.onerror = function () { location.reload(); };
            xhr.send();
        });
    });

    // --- Inline cart quantity update ---
    function debounce(fn, delay) {
        var timer;
        return function () {
            var ctx = this, args = arguments;
            clearTimeout(timer);
            timer = setTimeout(function () { fn.apply(ctx, args); }, delay);
        };
    }

    function updateCartQty(productId, quantity) {
        if (quantity < 0) return;
        var xhr = new XMLHttpRequest();
        xhr.open('POST', window.contextPath + '/cart', true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
        xhr.onload = function () {
            if (xhr.status === 200) {
                try {
                    var data = JSON.parse(xhr.responseText);
                    if (data.success) {
                        updateCartBadge(data.count);
                        if (data.subtotal !== undefined) {
                            var row = document.querySelector('tr[data-product-id="' + productId + '"]');
                            if (row) {
                                var subTd = row.querySelector('.subtotal');
                                if (subTd) subTd.textContent = data.subtotal + ' VND';
                            }
                        }
                        if (data.total !== undefined) {
                            var totalEl = document.getElementById('cartTotal');
                            if (totalEl) totalEl.textContent = data.total + ' VND';
                        }
                        if (quantity === 0) {
                            var row = document.querySelector('tr[data-product-id="' + productId + '"]');
                            if (row) row.remove();
                        }
                    } else {
                        showToast('Lỗi', data.error || 'Không thể cập nhật số lượng', 'error');
                    }
                } catch (e) { location.reload(); }
            }
        };
        xhr.send('action=update&id=' + productId + '&quantity=' + quantity);
    }

    var qtyInputs = document.querySelectorAll('.cart-qty');
    if (qtyInputs.length) {
        qtyInputs.forEach(function (input) {
            var productId = input.getAttribute('data-product-id');

            input.addEventListener('change', function () {
                var qty = parseInt(this.value, 10);
                if (isNaN(qty) || qty < 0) qty = 0;
                updateCartQty(productId, qty);
            });

            var minus = input.closest('tr').querySelector('.qty-minus');
            var plus = input.closest('tr').querySelector('.qty-plus');
            if (minus) {
                minus.addEventListener('click', function () {
                    var qty = parseInt(input.value, 10) - 1;
                    if (qty < 0) qty = 0;
                    input.value = qty;
                    updateCartQty(productId, qty);
                });
            }
            if (plus) {
                plus.addEventListener('click', function () {
                    var qty = parseInt(input.value, 10) + 1;
                    var max = parseInt(input.getAttribute('max'), 10);
                    if (max && qty > max) qty = max;
                    input.value = qty;
                    updateCartQty(productId, qty);
                });
            }
        });
    }

    function updateCartBadge(count) {
        var badge = document.getElementById('cart-badge');
        if (badge) {
            badge.textContent = count;
            badge.classList.remove('d-none');
            if (count === 0) badge.classList.add('d-none');
        }
    }

    // --- Search debounce ---
    var searchInput = document.getElementById('searchInput');
    if (searchInput) {
        var searchForm = searchInput.closest('form');
        var doSearch = debounce(function () {
            if (searchForm) searchForm.submit();
        }, 400);
        searchInput.addEventListener('input', doSearch);
    }

    // --- Toast system ---
    window.showToast = function (title, message, type) {
        var toastContainer = document.getElementById('toast-container');
        if (!toastContainer) {
            toastContainer = document.createElement('div');
            toastContainer.id = 'toast-container';
            toastContainer.className = 'toast-container position-fixed bottom-0 end-0 p-3';
            toastContainer.style.zIndex = '9999';
            document.body.appendChild(toastContainer);
        }

        var bgClass = type === 'error' ? 'bg-danger text-white' : type === 'success' ? 'bg-success text-white' : 'bg-info text-white';
        var icon = type === 'error' ? 'bi-exclamation-triangle-fill' : type === 'success' ? 'bi-check-circle-fill' : 'bi-info-circle-fill';

        var toastEl = document.createElement('div');
        toastEl.className = 'toast align-items-center border-0';
        toastEl.setAttribute('role', 'alert');
        toastEl.setAttribute('aria-live', 'assertive');
        toastEl.setAttribute('aria-atomic', 'true');
        toastEl.innerHTML = '<div class="d-flex"><div class="toast-body ' + bgClass + ' rounded"><i class="bi ' + icon + ' me-2"></i><strong>' + title + ':</strong> ' + message + '</div><button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button></div>';
        toastContainer.appendChild(toastEl);

        var toast = new bootstrap.Toast(toastEl, { delay: 3000 });
        toast.show();

        toastEl.addEventListener('hidden.bs.toast', function () { toastEl.remove(); });
    };

    // --- Loading overlay ---
    window.showLoading = function () {
        var spinner = document.getElementById('loadingSpinner');
        if (spinner) spinner.style.display = 'flex';
    };
    window.hideLoading = function () {
        var spinner = document.getElementById('loadingSpinner');
        if (spinner) spinner.style.display = 'none';
    };

    // --- Client-side form validation ---
    var forms = document.querySelectorAll('.needs-validation');
    Array.prototype.slice.call(forms).forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
                var firstInvalid = form.querySelector(':invalid');
                if (firstInvalid) firstInvalid.focus();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // --- Password confirmation match ---
    var confirmPasswordInput = document.getElementById('confirmPassword');
    if (confirmPasswordInput) {
        var passwordInput = document.getElementById('password');
        confirmPasswordInput.addEventListener('input', function () {
            if (passwordInput && this.value !== passwordInput.value) {
                this.setCustomValidity('Mật khẩu xác nhận không khớp');
            } else {
                this.setCustomValidity('');
            }
        });
        if (passwordInput) {
            passwordInput.addEventListener('input', function () {
                if (confirmPasswordInput.value && this.value !== confirmPasswordInput.value) {
                    confirmPasswordInput.setCustomValidity('Mật khẩu xác nhận không khớp');
                } else {
                    confirmPasswordInput.setCustomValidity('');
                }
            });
        }
    }

    // --- Image preview ---
    document.querySelectorAll('.image-preview-input').forEach(function (input) {
        input.addEventListener('change', function () {
            var preview = document.getElementById(this.getAttribute('data-preview'));
            if (preview && this.files && this.files[0]) {
                var reader = new FileReader();
                reader.onload = function (e) { preview.src = e.target.result; preview.classList.remove('d-none'); };
                reader.readAsDataURL(this.files[0]);
            }
        });
    });

    // --- Auto-dismiss alerts ---
    document.querySelectorAll('.alert-dismissible.auto-dismiss').forEach(function (alert) {
        setTimeout(function () {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 4000);
    });

    // --- Wishlist toggle ---
    document.querySelectorAll('.wishlist-btn').forEach(function (btn) {
        btn.addEventListener('click', function (e) {
            e.preventDefault();
            var productId = this.getAttribute('data-product-id');
            var icon = this.querySelector('i');
            var xhr = new XMLHttpRequest();
            xhr.open('POST', window.contextPath + '/wishlist', true);
            xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
            xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
            xhr.onload = function () {
                if (xhr.status === 200) {
                    try {
                        var data = JSON.parse(xhr.responseText);
                        if (data.success) {
                            if (data.action === 'added') {
                                icon.className = 'bi bi-heart-fill';
                                showToast('Thành công', 'Đã thêm vào yêu thích', 'success');
                            } else {
                                icon.className = 'bi bi-heart';
                                showToast('Thành công', 'Đã xóa khỏi yêu thích', 'success');
                            }
                        } else {
                            showToast('Lỗi', data.error || 'Không thể thao tác', 'error');
                        }
                    } catch (e) {
                        showToast('Lỗi', 'Phản hồi không hợp lệ', 'error');
                    }
                } else {
                    showToast('Lỗi', 'Lỗi kết nối máy chủ', 'error');
                }
            };
            xhr.onerror = function () {
                showToast('Lỗi', 'Không thể kết nối đến máy chủ', 'error');
            };
            xhr.send('action=toggle&productId=' + productId);
        });
    });

    // --- Auto-refresh cart badge on page load ---
    var cartBadge = document.getElementById('cart-badge');
    if (cartBadge) {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', window.contextPath + '/cart?action=count', true);
        xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
        xhr.onload = function () {
            if (xhr.status === 200) {
                try {
                    var data = JSON.parse(xhr.responseText);
                    updateCartBadge(data.count);
                } catch (e) { }
            }
        };
        xhr.send();
    }
});
