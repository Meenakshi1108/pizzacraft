package controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Order;
import model.User;
import service.OrderService;
import service.ServiceException;

/**
 * Servlet for displaying a user's order history
 */
@WebServlet("/orders")
public class OrderHistoryServlet extends BaseServlet {
    
    private OrderService orderService;
    
    @Override
    public void init() throws ServletException {
        orderService = new OrderService();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Ensure user is logged in
        if (!requireLogin(req, resp)) {
            return;
        }
        
        try {
            // Get logged in user
            User user = getLoggedInUser(req);
            
            // Get user's orders
            List<Order> orders = orderService.getOrdersByUser(user.getId());
            
            // Set orders as request attribute
            req.setAttribute("orders", orders);
            
            // Forward to the orders index page
            req.getRequestDispatcher("/WEB-INF/views/orders/index.jsp").forward(req, resp);
        } catch (ServiceException e) {
            getServletContext().log("Error retrieving order history", e);
            setFlashMessage(req, "error", "An error occurred while retrieving your order history.");
            resp.sendRedirect(req.getContextPath() + "/");
        }
    }
}
