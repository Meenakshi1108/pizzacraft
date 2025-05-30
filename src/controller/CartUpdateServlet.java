package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.ShoppingCart;

/**
 * Servlet for handling cart updates
 */
@WebServlet("/cart/update")
public class CartUpdateServlet extends BaseServlet {
    
    /**
     * Handles POST requests - updates the cart
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("cart") == null) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }
        
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        
        // Handle different types of updates
        String action = req.getParameter("action");
        
        if ("update".equals(action)) {
            // Update quantity of a specific item
            int itemIndex = Integer.parseInt(req.getParameter("itemIndex"));
            int quantity = Integer.parseInt(req.getParameter("quantity"));
            cart.updateQuantity(itemIndex, quantity);
            setFlashMessage(req, "info", "Cart updated successfully.");
        } else if ("remove".equals(action)) {
            // Remove a specific item
            int itemIndex = Integer.parseInt(req.getParameter("itemIndex"));
            cart.removeItem(itemIndex);
            setFlashMessage(req, "info", "Item removed from cart.");
        } else if ("clear".equals(action)) {
            // Clear the entire cart
            cart.clear();
            setFlashMessage(req, "info", "Cart cleared.");
        }
        
        resp.sendRedirect(req.getContextPath() + "/cart");
    }
}