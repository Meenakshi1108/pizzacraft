package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a shopping cart
 */
public class ShoppingCart {
    
    private List<CartItem> items;
    private BigDecimal total;
    
    public ShoppingCart() {
        this.items = new ArrayList<>();
        this.total = BigDecimal.ZERO;
    }
    
    /**
     * Adds a pizza to the cart
     * 
     * @param pizza The pizza to add
     * @param quantity The quantity to add
     * @param selectedToppingIds IDs of selected toppings
     * @param toppings List of all available toppings (to look up by ID)
     */
    public void addItem(Pizza pizza, int quantity, List<Integer> selectedToppingIds, List<Topping> toppings) {
        // Create a list of selected toppings
        List<Topping> selectedToppings = new ArrayList<>();
        if (selectedToppingIds != null) {
            for (Integer toppingId : selectedToppingIds) {
                for (Topping topping : toppings) {
                    if (topping.getId() == toppingId) {
                        selectedToppings.add(topping);
                        break;
                    }
                }
            }
        }
        
        // Check if the same pizza with the same toppings already exists in the cart
        for (CartItem item : items) {
            if (item.getPizza().getId() == pizza.getId() && hasSameToppings(item.getToppings(), selectedToppings)) {
                // If it exists, just increase the quantity
                item.setQuantity(item.getQuantity() + quantity);
                calculateTotal();
                return;
            }
        }
        
        // Otherwise, add a new cart item
        CartItem newItem = new CartItem(pizza, quantity, selectedToppings);
        items.add(newItem);
        calculateTotal();
    }
    
    /**
     * Updates the quantity of an item in the cart
     * 
     * @param itemIndex The index of the item to update
     * @param quantity The new quantity
     */
    public void updateQuantity(int itemIndex, int quantity) {
        if (itemIndex >= 0 && itemIndex < items.size()) {
            if (quantity <= 0) {
                items.remove(itemIndex);
            } else {
                items.get(itemIndex).setQuantity(quantity);
            }
            calculateTotal();
        }
    }
    
    /**
     * Removes an item from the cart
     * 
     * @param itemIndex The index of the item to remove
     */
    public void removeItem(int itemIndex) {
        if (itemIndex >= 0 && itemIndex < items.size()) {
            items.remove(itemIndex);
            calculateTotal();
        }
    }
    
    /**
     * Clears all items from the cart
     */
    public void clear() {
        items.clear();
        total = BigDecimal.ZERO;
    }
    
    /**
     * Gets the number of items in the cart
     * 
     * @return The number of items
     */
    public int getItemCount() {
        return items.size();
    }
    
    /**
     * Gets the total quantity of all items in the cart
     * 
     * @return The total quantity
     */
    public int getTotalQuantity() {
        int totalQty = 0;
        for (CartItem item : items) {
            totalQty += item.getQuantity();
        }
        return totalQty;
    }
    
    /**
     * Gets the items in the cart
     * 
     * @return The cart items
     */
    public List<CartItem> getItems() {
        return items;
    }
    
    /**
     * Gets the total price of all items in the cart
     * 
     * @return The total price
     */
    public BigDecimal getTotal() {
        return total;
    }
    
    /**
     * Recalculates the total price of all items in the cart
     */
    private void calculateTotal() {
        total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getSubtotal());
        }
    }
    
    /**
     * Checks if two lists of toppings are the same
     * 
     * @param list1 First list of toppings
     * @param list2 Second list of toppings
     * @return true if the lists contain the same toppings, false otherwise
     */
    private boolean hasSameToppings(List<Topping> list1, List<Topping> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        
        for (Topping topping : list1) {
            boolean found = false;
            for (Topping other : list2) {
                if (topping.getId() == other.getId()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Represents an item in the shopping cart
     */
    public static class CartItem {
        private Pizza pizza;
        private int quantity;
        private List<Topping> toppings;
        
        public CartItem(Pizza pizza, int quantity, List<Topping> toppings) {
            this.pizza = pizza;
            this.quantity = quantity;
            this.toppings = toppings != null ? toppings : new ArrayList<>();
        }
        
        public Pizza getPizza() {
            return pizza;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
        
        public List<Topping> getToppings() {
            return toppings;
        }
        
        public BigDecimal getToppingsCost() {
            BigDecimal cost = BigDecimal.ZERO;
            for (Topping topping : toppings) {
                cost = cost.add(topping.getPrice());
            }
            return cost;
        }
        
        public BigDecimal getItemPrice() {
            return pizza.getPrice().add(getToppingsCost());
        }
        
        public BigDecimal getSubtotal() {
            return getItemPrice().multiply(new BigDecimal(quantity));
        }
    }
}