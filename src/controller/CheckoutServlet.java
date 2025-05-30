package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.ShoppingCart;
import model.User;
import model.Order;
import model.OrderItem;
import service.OrderService;
import service.ServiceException;
import service.ValidationException;

/**
 * Servlet for handling the checkout process
 */
@WebServlet("/checkout")
public class CheckoutServlet extends BaseServlet {
    
    private OrderService orderService;
    
    @Override
    public void init() throws ServletException {
        orderService = new OrderService();
    }
    
    /**
     * Handles GET requests - displays the checkout form
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Must be logged in to checkout
        if (!requireLogin(req, resp)) {
            return;
        }
        
        // Get the cart from the session
        HttpSession session = req.getSession();
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        
        // Check if cart is empty
        if (cart == null || cart.getItemCount() == 0) {
            setFlashMessage(req, "warning", "Your cart is empty. Please add some items before checking out.");
            resp.sendRedirect(req.getContextPath() + "/menu");
            return;
        }
        
        // Get user for pre-filling the form
        User user = getLoggedInUser(req);
        req.setAttribute("user", user);
        
        // Forward to the checkout page
        req.getRequestDispatcher("/WEB-INF/views/checkout/index.jsp").forward(req, resp);
    }
    
    /**
     * Handles POST requests - processes the checkout form submission
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Must be logged in to checkout
        if (!requireLogin(req, resp)) {
            return;
        }
        
        // Get the cart from the session
        HttpSession session = req.getSession();
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        
        // Check if cart is empty
        if (cart == null || cart.getItemCount() == 0) {
            setFlashMessage(req, "warning", "Your cart is empty. Please add some items before checking out.");
            resp.sendRedirect(req.getContextPath() + "/menu");
            return;
        }
        
        try {
            // Get form parameters
            String deliveryAddress = req.getParameter("deliveryAddress");
            String contactNumber = req.getParameter("contactNumber");
            
            // Get the logged in user
            User user = getLoggedInUser(req);
            
            // Create an order
            Order order = new Order();
            order.setUserId(user.getId());
            order.setDeliveryAddress(deliveryAddress);
            order.setContactNumber(contactNumber);
            
            // Add items from the cart to the order
            for (ShoppingCart.CartItem cartItem : cart.getItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setPizzaId(cartItem.getPizza().getId());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getSubtotal());
                orderItem.setPizza(cartItem.getPizza());
                orderItem.setToppings(cartItem.getToppings());
                
                order.addOrderItem(orderItem);
            }
            
            // Place the order
            order = orderService.placeOrder(order);
            
            // Clear the cart
            cart.clear();
            
            // Store the order ID for the confirmation page
            session.setAttribute("lastOrderId", order.getId());
            
            // Redirect to the confirmation page
            resp.sendRedirect(req.getContextPath() + "/checkout/confirmation");
        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("deliveryAddress", req.getParameter("deliveryAddress"));
            req.setAttribute("contactNumber", req.getParameter("contactNumber"));
            req.getRequestDispatcher("/WEB-INF/views/checkout/index.jsp").forward(req, resp);
        } catch (ServiceException e) {
            getServletContext().log("Error during checkout", e);
            req.setAttribute("error", "An error occurred while processing your order. Please try again later.");
            req.getRequestDispatcher("/WEB-INF/views/checkout/index.jsp").forward(req, resp);
        }
    }
}