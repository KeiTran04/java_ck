package com.shop.model;

public class ProductVariant {
    private int id;
    private int productId;
    private String variantType;
    private String variantName;
    private double priceAdjustment;
    private int stock;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getVariantType() { return variantType; }
    public void setVariantType(String variantType) { this.variantType = variantType; }
    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }
    public double getPriceAdjustment() { return priceAdjustment; }
    public void setPriceAdjustment(double priceAdjustment) { this.priceAdjustment = priceAdjustment; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
