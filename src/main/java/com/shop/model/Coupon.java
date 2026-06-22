package com.shop.model;

import java.util.Date;

public class Coupon {
    private int id;
    private String code;
    private String discountType;
    private double discountValue;
    private double minOrderAmount;
    private int maxUsage;
    private int usedCount;
    private Date expiryDate;
    private boolean active;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }
    public double getDiscountValue() { return discountValue; }
    public void setDiscountValue(double discountValue) { this.discountValue = discountValue; }
    public double getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(double minOrderAmount) { this.minOrderAmount = minOrderAmount; }
    public int getMaxUsage() { return maxUsage; }
    public void setMaxUsage(int maxUsage) { this.maxUsage = maxUsage; }
    public int getUsedCount() { return usedCount; }
    public void setUsedCount(int usedCount) { this.usedCount = usedCount; }
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public double calculateDiscount(double orderTotal) {
        if ("PERCENTAGE".equals(discountType)) {
            return orderTotal * discountValue / 100.0;
        }
        return Math.min(discountValue, orderTotal);
    }
}
