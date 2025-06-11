package controller;

// Update imports: Replace Cart with ShoppingCart
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.ShoppingCart; // Changed from model.Cart
import model.ShoppingCart.CartItem; // Changed from model.CartItem
import model.Pizza;
import model.Topping;
import service.PizzaService;
import service.ToppingService;
import service.ServiceException;

/**
 * Servlet for handling the shopping cart
 */
@WebServlet({"/cart", "/cart/*"})
public class CartServlet extends BaseServlet {
    
    private PizzaService pizzaService;
    private ToppingService toppingService;
    
    @Override
    public void init() throws ServletException {
        pizzaService = new PizzaService();
        toppingService = new ToppingService();
    }
    
    /**
     * Handles GET requests - displays the cart
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            HttpSession session = req.getSession();
            ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
            
            // Debug logging
            System.out.println("Cart in session: " + (cart != null));
            if (cart != null) {
                System.out.println("Items in cart: " + cart.getItemCount());
                for (int i = 0; i < cart.getItems().size(); i++) {
                    CartItem item = cart.getItems().get(i);
                    System.out.println("Item " + i + ": " + 
                        (item.getPizza() != null ? item.getPizza().getName() : "null") + 
                        ", Quantity: " + item.getQuantity());
                }
            } else {
                // Create empty cart if null
                cart = new ShoppingCart();
                session.setAttribute("cart", cart);
            }
            
            req.setAttribute("cart", cart);
            req.getRequestDispatcher("/WEB-INF/views/cart/index.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Sorry, we couldn't load your cart: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }
    
    /**
     * Handles POST requests - adds an item to the cart
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // Check if user is logged in
        if (!isLoggedIn(req)) {
            // Set error message
            req.getSession().setAttribute("error", "Please log in to add items to your cart");
            
            // Save the intended action in the session
            saveIntendedAction(req);
            
            // Redirect to login page
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        // Process the cart action
        String action = req.getParameter("action");
        
        if ("add".equals(action)) {
            addToCart(req, resp);
        } else if ("remove".equals(action)) {
            removeFromCart(req, resp);
        } else if ("update".equals(action)) {
            updateCart(req, resp);
        } else if ("clear".equals(action)) {
            clearCart(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }
    
    /**
     * Saves the intended cart action to the session
     */
    private void saveIntendedAction(HttpServletRequest req) {
        // Get pizza ID and quantity
        String pizzaId = req.getParameter("pizzaId");
        String quantity = req.getParameter("quantity");
        
        // Store intended action in the session
        HttpSession session = req.getSession();
        session.setAttribute("intendedAction", "addToCart");
        session.setAttribute("intendedPizzaId", pizzaId);
        session.setAttribute("intendedQuantity", quantity);
        
        // Store the referring URL so we can go back after login
        String referer = req.getHeader("Referer");
        if (referer != null) {
            session.setAttribute("intendedRedirect", referer);
        }
    }
    
    /**
     * Adds an item to the cart
     */
    private void addToCart(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            // Get parameters
            String pizzaIdStr = req.getParameter("pizzaId");
            String quantityStr = req.getParameter("quantity");
            
            // Validate parameters
            if (pizzaIdStr == null || quantityStr == null) {
                resp.sendRedirect(req.getContextPath() + "/menu");
                return;
            }
            
            int pizzaId = Integer.parseInt(pizzaIdStr);
            int quantity = Integer.parseInt(quantityStr);
            
            // Validate quantity
            if (quantity < 1) {
                quantity = 1;
            } else if (quantity > 10) {
                quantity = 10;
            }
            
            // Get pizza from database
            Pizza pizza = pizzaService.getPizzaById(pizzaId);
            if (pizza == null) {
                resp.sendRedirect(req.getContextPath() + "/menu");
                return;
            }
            
            // Get selected toppings
            List<Topping> toppings = new ArrayList<>();
            String[] toppingIds = req.getParameterValues("toppings");
            BigDecimal toppingsCost = BigDecimal.ZERO;
            
            if (toppingIds != null) {
                for (String toppingIdStr : toppingIds) {
                    int toppingId = Integer.parseInt(toppingIdStr);
                    Topping topping = toppingService.getToppingById(toppingId);
                    if (topping != null) {
                        toppings.add(topping);
                        toppingsCost = toppingsCost.add(topping.getPrice());
                    }
                }
            }
            
            // Calculate item price
            BigDecimal itemPrice = pizza.getPrice().add(toppingsCost);
            
            // Get cart from session or create new one
            ShoppingCart cart = getCartFromSession(req);
            
            // Extract topping IDs from the toppings list
            List<Integer> toppingIdsList = new ArrayList<>();
            if (toppingIds != null) {
                for (String toppingIdStr : toppingIds) {
                    toppingIdsList.add(Integer.parseInt(toppingIdStr));
                }
            }
            
            // Add item to cart with proper parameters
            cart.addItem(pizza, quantity, toppingIdsList, toppings);
            
            // Save cart to session
            req.getSession().setAttribute("cart", cart);
            
            // Add success message
            req.getSession().setAttribute("success", pizza.getName() + " added to cart successfully!");
            
            // CHANGED: Redirect to cart page instead of referring page
            resp.sendRedirect(req.getContextPath() + "/cart");
            
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/menu");
        } catch (ServiceException e) {
            getServletContext().log("Error adding to cart", e);
            resp.sendRedirect(req.getContextPath() + "/menu");
        }
    }
    
    /**
     * Removes an item from the cart
     */
    private void removeFromCart(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String itemIndexStr = req.getParameter("itemIndex");
        
        if (itemIndexStr != null) {
            try {
                int itemIndex = Integer.parseInt(itemIndexStr);
                ShoppingCart cart = getCartFromSession(req);
                cart.removeItem(itemIndex);
                req.getSession().setAttribute("success", "Item removed from cart");
            } catch (NumberFormatException e) {
                // Invalid index, ignore
            }
        }
        
        resp.sendRedirect(req.getContextPath() + "/cart");
    }
    
    /**
     * Updates item quantities in the cart
     */
    private void updateCart(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            // Get parameters
            String itemIndexStr = req.getParameter("itemIndex");
            String quantityStr = req.getParameter("quantity");
            
            // Validate parameters
            if (itemIndexStr == null || quantityStr == null) {
                resp.sendRedirect(req.getContextPath() + "/cart");
                return;
            }
            
            int itemIndex = Integer.parseInt(itemIndexStr);
            int quantity = Integer.parseInt(quantityStr);
            
            // Validate quantity
            if (quantity < 1) {
                quantity = 1;
            } else if (quantity > 10) {
                quantity = 10;
            }
            
            // Get cart from session
            HttpSession session = req.getSession();
            ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
            
            if (cart != null && itemIndex >= 0 && itemIndex < cart.getItems().size()) {
                // Use the correct method name from ShoppingCart class
                cart.updateQuantity(itemIndex, quantity);
                session.setAttribute("cart", cart);
            }
            
            // Check if it's an AJAX request
            String xRequestedWith = req.getHeader("X-Requested-With");
            if (xRequestedWith != null && xRequestedWith.equals("XMLHttpRequest")) {
                // Send JSON response
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write("{\"success\": true, \"itemCount\": " + 
                                       (cart != null ? cart.getItemCount() : 0) + ", " +
                                       "\"total\": " + (cart != null ? cart.getTotal() : 0) + "}");
                return;
            }
            
            // Redirect back to cart page
            resp.sendRedirect(req.getContextPath() + "/cart");
            
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }
    
    /**
     * Clears all items from the cart
     */
    private void clearCart(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        ShoppingCart cart = getCartFromSession(req);
        cart.clear();
        req.getSession().setAttribute("success", "Cart cleared");
        resp.sendRedirect(req.getContextPath() + "/cart");
    }
    
    /**
     * Gets the cart from the session or creates a new one
     */
    private ShoppingCart getCartFromSession(HttpServletRequest req) { // Return type updated
        HttpSession session = req.getSession();
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart"); // Cast updated
        
        if (cart == null) {
            cart = new ShoppingCart(); // Constructor updated
            session.setAttribute("cart", cart);
        }
        
        return cart;
    }
}