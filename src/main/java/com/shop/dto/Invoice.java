package com.shop.dto;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "Invoice")
@XmlAccessorType(XmlAccessType.FIELD)
public class Invoice {

    @XmlElement(name = "Customer")
    private int customerId;

    @XmlElement(name = "OrderDate")
    private String orderDate;

    @XmlElementWrapper(name = "LineItems")
    @XmlElement(name = "Item")
    private List<LineItem> items;

    @XmlElement(name = "TotalAmount")
    private double totalAmount;

    public Invoice() {}

    public Invoice(int customerId, String orderDate, List<LineItem> items, double totalAmount) {
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.items = items;
        this.totalAmount = totalAmount;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class LineItem {
        @XmlElement(name = "ProductId")
        private int productId;
        @XmlElement(name = "Quantity")
        private int quantity;
        @XmlElement(name = "Price")
        private double price;

        public LineItem() {}

        public LineItem(int productId, int quantity, double price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }
    }
}
