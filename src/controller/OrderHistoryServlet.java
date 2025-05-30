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
 * Servlet for displaying order history
 */
@WebServlet("/orders")
public class OrderHistoryServlet extends BaseServlet {
    
    private OrderService orderService;
    
    @Override
    public void init() throws ServletException {
        orderService = new OrderService();
    }
    
    /**
     * Handles GET requests - displays the user's order history
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Must be logged in to see orders
        if (!requireLogin(req, resp)) {
            return;
        }
        
        try {
            User user = getLoggedInUser(req);
            List<Order> orders = orderService.getOrdersByUser(user.getId());
            
            req.setAttribute("orders", orders);
            req.getRequestDispatcher("/WEB-INF/views/orders/index.jsp").forward(req, resp);
        } catch (ServiceException e) {
            getServletContext().log("Error getting order history", e);
            req.setAttribute("error", "An error occurred while retrieving your orders.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }
}