package data;

import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 2L;

    public String productName;
    public String productType;
    public int availableAmount;
    public double price;

    public Product(String name, String type, int amount, double price) {
        this.productName = name;
        this.productType = type;
        this.availableAmount = amount;
        this.price = price;
    }

    public String toString() {
        return "\nProduct{" +
                "productName='" + productName + '\'' +
                ", productType='" + productType + '\'' +
                ", availableAmount=" + availableAmount +
                ", price=" + price +
                "}";
    }

    // Getter for productName
    public String getProductName() {
        return productName;
    }

    // // Setter for productName
    // public void setProductName(String productName) {
    //     this.productName = productName;
    // }

    // Getter for productType
    public String getProductType() {
        return productType;
    }

    // Setter for productType
    public void setProductType(String productType) {
        this.productType = productType;
    }

    // Getter for availableAmount
    public synchronized int getAvailableAmount() {
        return availableAmount;
    }

    // Setter for availableAmount
    public synchronized void setAvailableAmount(int availableAmount) {
        this.availableAmount = availableAmount;
    }

    // Getter for price
    public synchronized double getPrice() {
        return price;
    }

    // Setter for price
    public synchronized void setPrice(double price) {
        this.price = price;
    }

    // Method to purchase product
    public synchronized boolean purchase(int quantity) {
        if (availableAmount >= quantity) {
            availableAmount -= quantity;
            return true;
        }
        return false;
    }

    public synchronized String changeStock(int quantity) {
        this.availableAmount += quantity;
        if (this.availableAmount < 0) {
            this.availableAmount = 0;
            return "Stock changed";
        }
        return "Stock changed";
    }
}

