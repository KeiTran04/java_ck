package com.shop.task;

import com.shop.dao.OrderDAO;
import com.shop.model.Order;
import com.shop.model.OrderDetail;
import com.shop.util.EmailUtil;

import java.util.List;

public class OrderProcessingTask implements Runnable {

    private Order order;
    private List<OrderDetail> details;
    private String userEmail;
    private String invoiceDir;

    public OrderProcessingTask(Order order, List<OrderDetail> details, String userEmail, String invoiceDir) {
        this.order = order;
        this.details = details;
        this.userEmail = userEmail;
        this.invoiceDir = invoiceDir;
    }

    @Override
    public void run() {
        try {
            String invoicePath = XmlGenerator.generateInvoice(order, details, invoiceDir);

            if (invoicePath != null) {
                OrderDAO orderDAO = new OrderDAO();
                orderDAO.updateInvoicePath(order.getId(), invoicePath);
            }

            StringBuilder orderInfo = new StringBuilder();
            orderInfo.append("Mã đơn hàng: ").append(order.getId()).append("\n");
            orderInfo.append("Tổng tiền: ").append(String.format("%,.0f", order.getTotalAmount())).append(" VND\n");
            orderInfo.append("Số mặt hàng: ").append(details.size());

            EmailUtil.sendOrderConfirmation(userEmail, "Xác nhận đơn hàng - Shop", orderInfo.toString(), invoicePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
