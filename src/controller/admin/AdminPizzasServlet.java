package controller.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.BaseServlet;
import model.Pizza;
import model.User;
import service.CategoryService;
import service.PizzaService;
import service.ServiceException;
import service.ValidationException;

@WebServlet("/admin/pizzas")
public class AdminPizzasServlet extends BaseServlet {
    
    private PizzaService pizzaService;
    
    @Override
    public void init() throws ServletException {
        pizzaService = new PizzaService();
    }
      @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Check if logged in and has ADMIN role
        if (!requireLogin(req, resp) || !requireRole(req, resp, User.ROLE_ADMIN)) {
            return;
        }
        
        try {
            // Check for AJAX request to get a specific pizza
            String action = req.getParameter("action");
            if ("get".equals(action)) {
                int pizzaId = Integer.parseInt(req.getParameter("id"));
                Pizza pizza = pizzaService.getPizzaById(pizzaId);
                
                if (pizza == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Pizza not found\"}");
                    return;
                }
                
                // Send pizza data as JSON
                resp.setContentType("application/json");
                resp.getWriter().write(convertPizzaToJson(pizza));
                return;
            }
            
            // Regular page load
            List<Pizza> pizzas = pizzaService.getAllPizzas();
            req.setAttribute("pizzas", pizzas);
            
            // Get categories for pizza forms
            CategoryService categoryService = new CategoryService();
            req.setAttribute("categories", categoryService.getAllCategories());
            
            req.getRequestDispatcher("/WEB-INF/views/admin/pizzas.jsp").forward(req, resp);
        } catch (ServiceException e) {
            getServletContext().log("Error getting pizzas for admin", e);
            req.setAttribute("error", "An error occurred while retrieving pizzas.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid pizza ID\"}");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Check if logged in and has ADMIN role
        if (!requireLogin(req, resp) || !requireRole(req, resp, User.ROLE_ADMIN)) {
            return;
        }
        
        String action = req.getParameter("action");
        
        try {
            if ("add".equals(action)) {
                // Handle adding a new pizza
                Pizza newPizza = new Pizza();                newPizza.setName(req.getParameter("name"));
                newPizza.setDescription(req.getParameter("description"));
                newPizza.setPrice(new BigDecimal(req.getParameter("price")));
                newPizza.setCategoryId(Integer.parseInt(req.getParameter("categoryId")));
                newPizza.setAvailable(Boolean.parseBoolean(req.getParameter("available")));
                
                // Handle image (in a real application, you'd need to implement file upload)
                String imageUrl = req.getParameter("imageUrl");
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    newPizza.setImageUrl(imageUrl);
                } else {
                    // Use a default image if none provided
                    newPizza.setImageUrl("/images/pizza-placeholder.jpg");
                }
                
                pizzaService.createPizza(newPizza);
                setFlashMessage(req, "success", "Pizza added successfully!");
                
            } else if ("edit".equals(action)) {
                // Handle updating an existing pizza
                int pizzaId = Integer.parseInt(req.getParameter("id"));
                Pizza existingPizza = pizzaService.getPizzaById(pizzaId);
                
                if (existingPizza == null) {
                    setFlashMessage(req, "error", "Pizza not found.");
                    resp.sendRedirect(req.getContextPath() + "/admin/pizzas");
                    return;
                }
                  existingPizza.setName(req.getParameter("name"));
                existingPizza.setDescription(req.getParameter("description"));
                existingPizza.setPrice(new BigDecimal(req.getParameter("price")));
                existingPizza.setCategoryId(Integer.parseInt(req.getParameter("categoryId")));
                existingPizza.setAvailable(Boolean.parseBoolean(req.getParameter("available")));
                
                // Update image only if a new one is provided
                String imageUrl = req.getParameter("imageUrl");
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    existingPizza.setImageUrl(imageUrl);
                }
                
                pizzaService.updatePizza(existingPizza);
                setFlashMessage(req, "success", "Pizza updated successfully!");
                
            } else if ("delete".equals(action)) {
                // Handle deleting a pizza
                int pizzaId = Integer.parseInt(req.getParameter("id"));
                boolean deleted = pizzaService.deletePizza(pizzaId);
                
                if (deleted) {
                    setFlashMessage(req, "success", "Pizza deleted successfully!");
                } else {
                    setFlashMessage(req, "error", "Failed to delete pizza.");
                }
            } else {
                setFlashMessage(req, "error", "Invalid action specified.");
            }
        } catch (ValidationException e) {
            setFlashMessage(req, "error", "Validation error: " + e.getMessage());
        } catch (ServiceException e) {
            getServletContext().log("Error processing pizza action", e);
            setFlashMessage(req, "error", "An error occurred: " + e.getMessage());
        } catch (NumberFormatException e) {
            setFlashMessage(req, "error", "Invalid number format in form.");
        } catch (Exception e) {
            getServletContext().log("Unexpected error in AdminPizzasServlet", e);
            setFlashMessage(req, "error", "An unexpected error occurred.");
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/pizzas");
    }
    
    /**
     * Helper method to convert a Pizza object to JSON string
     */
    private String convertPizzaToJson(Pizza pizza) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(pizza.getId()).append(",");
        json.append("\"name\":\"").append(escapeJsonString(pizza.getName())).append("\",");
        json.append("\"description\":\"").append(escapeJsonString(pizza.getDescription())).append("\",");
        json.append("\"price\":").append(pizza.getPrice()).append(",");
        json.append("\"categoryId\":").append(pizza.getCategoryId()).append(",");
        json.append("\"imageUrl\":\"").append(escapeJsonString(pizza.getImageUrl())).append("\",");
        json.append("\"available\":").append(pizza.isAvailable());
        json.append("}");
        return json.toString();
    }
    
    /**
     * Escape special characters in JSON strings
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}