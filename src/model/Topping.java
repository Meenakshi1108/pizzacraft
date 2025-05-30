package model;

import java.math.BigDecimal;

/**
 * Represents a pizza topping option
 */
public class Topping {
    private int id;
    private String name;
    private BigDecimal price;
    private boolean isVegetarian;
    
    // Default constructor
    public Topping() {
    }
    
    // Constructor with all fields
    public Topping(int id, String name, BigDecimal price, boolean isVegetarian) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.isVegetarian = isVegetarian;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public boolean isVegetarian() {
        return isVegetarian;
    }
    
    public void setVegetarian(boolean vegetarian) {
        isVegetarian = vegetarian;
    }
    
    @Override
    public String toString() {
        return "Topping{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", isVegetarian=" + isVegetarian +
                '}';
    }
}