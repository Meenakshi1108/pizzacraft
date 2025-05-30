package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single item in an order (a pizza with its quantity and possible toppings)
 */
public class OrderItem {
    private int id;
    private int orderId;
    private int pizzaId;
    private int quantity;
    private BigDecimal price;
    private Pizza pizza; // To hold the related pizza object
    private List<Topping> toppings; // To hold the toppings for this order item
    
    // Default constructor
    public OrderItem() {
        this.toppings = new ArrayList<>();
    }
    
    // Constructor with all fields
    public OrderItem(int id, int orderId, int pizzaId, int quantity, BigDecimal price) {
        this.id = id;
        this.orderId = orderId;
        this.pizzaId = pizzaId;
        this.quantity = quantity;
        this.price = price;
        this.toppings = new ArrayList<>();
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public int getPizzaId() {
        return pizzaId;
    }
    
    public void setPizzaId(int pizzaId) {
        this.pizzaId = pizzaId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Pizza getPizza() {
        return pizza;
    }
    
    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }
    
    public List<Topping> getToppings() {
        return toppings;
    }
    
    public void setToppings(List<Topping> toppings) {
        this.toppings = toppings;
    }
    
    // Add a single topping to the list
    public void addTopping(Topping topping) {
        this.toppings.add(topping);
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", pizzaId=" + pizzaId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", toppings count=" + (toppings != null ? toppings.size() : 0) +
                '}';
    }
}