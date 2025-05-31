package model;

import java.io.Serializable;

public class Product implements Serializable {
    public enum ProductType {
        APPETIZER,
        MAIN_COURSE,
        DESSERT,
        BEVERAGE
    }
    private String name;
    private String description;
    private double price;
    private ProductType type;
    private String imagePath;
    public Product(String name, String description, double price, ProductType type, String imagePath) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.imagePath = imagePath;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public ProductType getType() {
        return type;
    }
    public void setType(ProductType type) {
        this.type = type;
    }
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    @Override
    public String toString() {
        return name + " - " + description + " ($" + price + ")";
    }
} 