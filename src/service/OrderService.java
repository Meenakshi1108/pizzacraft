package service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import dao.OrderDAO;
import dao.OrderItemDAO;
import dao.PizzaDAO;
import dao.UserDAO;
import model.Order;
import model.OrderItem;
import model.Pizza;
import model.User;
// import service.ValidationException;
/**
 * Service for handling order-related business logic
 */
public class OrderService {
    
    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final PizzaDAO pizzaDAO;
    private final UserDAO userDAO;
    
    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.orderItemDAO = new OrderItemDAO();
        this.pizzaDAO = new PizzaDAO();
        this.userDAO = new UserDAO();
    }
    
    /**
     * Places a new order
     * 
     * @param order The order to place
     * @return The created order with ID populated
     * @throws ValidationException If validation fails
     * @throws ServiceException If a service error occurs
     */
    public Order placeOrder(Order order) throws ValidationException, ServiceException {
        try {
            // Validate the order
            validateOrder(order);
            
            // Verify the user exists
            User user = userDAO.findById(order.getUserId());
            if (user == null) {
                throw new ValidationException("User not found");
            }
            
            // Verify each pizza in order items is available and calculate total
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            for (OrderItem item : order.getOrderItems()) {
                Pizza pizza = pizzaDAO.findById(item.getPizzaId());
                
                if (pizza == null) {
                    throw new ValidationException("Pizza with ID " + item.getPizzaId() + " not found");
                }
                
                if (!pizza.isAvailable()) {
                    throw new ValidationException("Pizza '" + pizza.getName() + "' is not available");
                }
                
                // Calculate item price (pizza price * quantity)
                BigDecimal itemPrice = pizza.getPrice().multiply(new BigDecimal(item.getQuantity()));
                item.setPrice(itemPrice);
                
                // Add to total
                totalAmount = totalAmount.add(itemPrice);
            }
            
            // Set the calculated total amount
            order.setTotalAmount(totalAmount);
            
            // Set initial order status
            order.setOrderStatus("PLACED");
            
            // Create the order (this will also create order items)
            return orderDAO.createOrder(order);
        } catch (SQLException e) {
            throw new ServiceException("Error placing order", e);
        }
    }
    
    /**
     * Gets an order by its ID
     * 
     * @param id The order ID
     * @return The order if found, null otherwise
     * @throws ServiceException If a service error occurs
     */
    public Order getOrderById(int id) throws ServiceException {
        try {
            return orderDAO.findById(id);
        } catch (SQLException e) {
            throw new ServiceException("Error getting order by ID", e);
        }
    }
    
    /**
     * Gets all orders
     * 
     * @return List of all orders
     * @throws ServiceException If a service error occurs
     */
    public List<Order> getAllOrders() throws ServiceException {
        try {
            return orderDAO.findAll();
        } catch (SQLException e) {
            throw new ServiceException("Error getting all orders", e);
        }
    }
    
    /**
     * Gets all orders for a specific user
     * 
     * @param userId The user ID
     * @return List of orders for the user
     * @throws ServiceException If a service error occurs
     */
    public List<Order> getOrdersByUser(int userId) throws ServiceException {
        try {
            return orderDAO.findByUserId(userId);
        } catch (SQLException e) {
            throw new ServiceException("Error getting orders for user", e);
        }
    }
    
    /**
     * Updates the status of an order
     * 
     * @param orderId The ID of the order
     * @param status The new status
     * @return true if the update was successful, false otherwise
     * @throws ValidationException If validation fails
     * @throws ServiceException If a service error occurs
     */
    public boolean updateOrderStatus(int orderId, String status) 
            throws ValidationException, ServiceException {
        try {
            // Validate the status
            validateOrderStatus(status);
            
            // Check if the order exists
            Order order = orderDAO.findById(orderId);
            if (order == null) {
                throw new ValidationException("Order not found");
            }
            
            // Check if the status transition is valid
            validateStatusTransition(order.getOrderStatus(), status);
            
            // Update the order status
            return orderDAO.updateOrderStatus(orderId, status);
        } catch (SQLException e) {
            throw new ServiceException("Error updating order status", e);
        }
    }
    
    /**
     * Validates an order
     * 
     * @param order The order to validate
     * @throws ValidationException If validation fails
     */
    private void validateOrder(Order order) throws ValidationException {
        // Check that required fields are present
        if (order.getUserId() <= 0) {
            throw new ValidationException("User ID is required");
        }
        
        if (order.getDeliveryAddress() == null || order.getDeliveryAddress().trim().isEmpty()) {
            throw new ValidationException("Delivery address is required");
        }
        
        if (order.getContactNumber() == null || order.getContactNumber().trim().isEmpty()) {
            throw new ValidationException("Contact number is required");
        }
        
        // Check that there is at least one order item
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new ValidationException("Order must contain at least one item");
        }
        
        // Validate each order item
        for (OrderItem item : order.getOrderItems()) {
            if (item.getPizzaId() <= 0) {
                throw new ValidationException("Valid pizza ID is required for each item");
            }
            
            if (item.getQuantity() <= 0) {
                throw new ValidationException("Quantity must be greater than zero");
            }
        }
    }
    
    /**
     * Validates an order status value
     * 
     * @param status The status to validate
     * @throws ValidationException If validation fails
     */
    private void validateOrderStatus(String status) throws ValidationException {
        // Check that the status is one of the allowed values
        String[] allowedStatuses = {"PLACED", "PREPARING", "READY", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED"};
        boolean valid = false;
        
        for (String allowedStatus : allowedStatuses) {
            if (allowedStatus.equals(status)) {
                valid = true;
                break;
            }
        }
        
        if (!valid) {
            throw new ValidationException("Invalid order status");
        }
    }
    
    /**
     * Validates that a status transition is allowed
     * 
     * @param currentStatus The current status
     * @param newStatus The new status
     * @throws ValidationException If the transition is not allowed
     */
    private void validateStatusTransition(String currentStatus, String newStatus) throws ValidationException {
        // Define allowed transitions
        // This is a simplified version; a real implementation might use a state machine or a transition matrix
        
        if (currentStatus.equals("CANCELLED") || currentStatus.equals("DELIVERED")) {
            // Terminal states
            throw new ValidationException("Cannot change status of a " + currentStatus + " order");
        }
        
        if (currentStatus.equals("PLACED")) {
            // From PLACED, can go to PREPARING or CANCELLED
            if (!newStatus.equals("PREPARING") && !newStatus.equals("CANCELLED")) {
                throw new ValidationException("From PLACED status, order can only move to PREPARING or CANCELLED");
            }
        } else if (currentStatus.equals("PREPARING")) {
            // From PREPARING, can go to READY or CANCELLED
            if (!newStatus.equals("READY") && !newStatus.equals("CANCELLED")) {
                throw new ValidationException("From PREPARING status, order can only move to READY or CANCELLED");
            }
        } else if (currentStatus.equals("READY")) {
            // From READY, can go to OUT_FOR_DELIVERY or CANCELLED
            if (!newStatus.equals("OUT_FOR_DELIVERY") && !newStatus.equals("CANCELLED")) {
                throw new ValidationException("From READY status, order can only move to OUT_FOR_DELIVERY or CANCELLED");
            }
        } else if (currentStatus.equals("OUT_FOR_DELIVERY")) {
            // From OUT_FOR_DELIVERY, can go to DELIVERED or CANCELLED
            if (!newStatus.equals("DELIVERED") && !newStatus.equals("CANCELLED")) {
                throw new ValidationException("From OUT_FOR_DELIVERY status, order can only move to DELIVERED or CANCELLED");
            }
        }
    }

/**
 * Assigns an order to a delivery person
 * 
 * @param orderId The ID of the order
 * @param deliveryPersonId The ID of the delivery person
 * @return true if assignment was successful, false otherwise
 * @throws ValidationException If validation fails
 * @throws ServiceException If a service error occurs
 */
public boolean assignOrderToDeliveryPerson(int orderId, int deliveryPersonId) 
        throws ValidationException, ServiceException {
    try {
        // Check if the order exists
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            throw new ValidationException("Order not found");
        }
        
        // Check if the delivery person exists and has the correct role
        User deliveryPerson = userDAO.findById(deliveryPersonId);
        if (deliveryPerson == null) {
            throw new ValidationException("Delivery person not found");
        }
        
        if (!User.ROLE_DELIVERY.equals(deliveryPerson.getRole())) {
            throw new ValidationException("Selected user is not a delivery person");
        }
        
        // Check if the order is in a valid status for assignment
        if (!("PLACED".equals(order.getOrderStatus()) || 
              "PREPARING".equals(order.getOrderStatus()) || 
              "READY".equals(order.getOrderStatus()))) {
            throw new ValidationException("Cannot assign this order in its current status");
        }
        
        // Update the order status to READY if not already
        if (!"READY".equals(order.getOrderStatus())) {
            orderDAO.updateOrderStatus(orderId, "READY");
        }
        
        // Assign the delivery person
        return orderDAO.assignDeliveryPerson(orderId, deliveryPersonId);
    } catch (SQLException e) {
        throw new ServiceException("Error assigning delivery person to order", e);
    }
}

    /**
     * Marks an order as out for delivery
     * 
     * @param orderId The ID of the order
     * @param deliveryPersonId The ID of the delivery person (for validation)
     * @return true if update was successful, false otherwise
     * @throws ValidationException If validation fails
     * @throws ServiceException If a service error occurs
     */
    public boolean markOrderOutForDelivery(int orderId, int deliveryPersonId) 
            throws ValidationException, ServiceException {
        try {
            // Check if the order exists and is assigned to this delivery person
            Order order = orderDAO.findById(orderId);
            if (order == null) {
                throw new ValidationException("Order not found");
            }
            
            if (order.getAssignedToUserId() == null || order.getAssignedToUserId() != deliveryPersonId) {
                throw new ValidationException("This order is not assigned to you");
            }
            
            if (!"READY".equals(order.getOrderStatus())) {
                throw new ValidationException("Order is not ready for delivery");
            }
            
            // Update the order status
            return orderDAO.updateOrderStatus(orderId, "OUT_FOR_DELIVERY");
        } catch (SQLException e) {
            throw new ServiceException("Error marking order as out for delivery", e);
        }
    }

    /**
     * Marks an order as delivered
     * 
     * @param orderId The ID of the order
     * @param deliveryPersonId The ID of the delivery person (for validation)
     * @return true if update was successful, false otherwise
     * @throws ValidationException If validation fails
     * @throws ServiceException If a service error occurs
     */
    public boolean markOrderDelivered(int orderId, int deliveryPersonId) 
            throws ValidationException, ServiceException {
        try {
            // Check if the order exists and is assigned to this delivery person
            Order order = orderDAO.findById(orderId);
            if (order == null) {
                throw new ValidationException("Order not found");
            }
            
            if (order.getAssignedToUserId() == null || order.getAssignedToUserId() != deliveryPersonId) {
                throw new ValidationException("This order is not assigned to you");
            }
            
            if (!"OUT_FOR_DELIVERY".equals(order.getOrderStatus())) {
                throw new ValidationException("Order is not out for delivery");
            }
            
            // Update the order status and set delivered_at timestamp
            return orderDAO.markOrderDelivered(orderId);
        } catch (SQLException e) {
            throw new ServiceException("Error marking order as delivered", e);
        }
    }

    /**
     * Gets all orders assigned to a specific delivery person
     * 
     * @param deliveryPersonId The delivery person ID
     * @return List of orders assigned to the delivery person
     * @throws ServiceException If a service error occurs
     */
    public List<Order> getOrdersByDeliveryPerson(int deliveryPersonId) throws ServiceException {
        try {
            return orderDAO.findByDeliveryPersonId(deliveryPersonId);
        } catch (SQLException e) {
            throw new ServiceException("Error getting orders for delivery person", e);
        }
    }
}