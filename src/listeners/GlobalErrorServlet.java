package listeners;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Global error handler servlet that displays detailed error information
 * Defined in web.xml, so no @WebServlet annotation needed
 */
public class GlobalErrorServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processError(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processError(req, resp);
    }

    private void processError(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Set response content type
        resp.setContentType("text/html");
        
        // Get the error information
        Throwable throwable = (Throwable) req.getAttribute("javax.servlet.error.exception");
        Integer statusCode = (Integer) req.getAttribute("javax.servlet.error.status_code");
        String servletName = (String) req.getAttribute("javax.servlet.error.servlet_name");
        String requestUri = (String) req.getAttribute("javax.servlet.error.request_uri");
        
        // If status code is null, set to 500 (Internal Server Error)
        if (statusCode == null) {
            statusCode = 500;
        }
        
        // Write the error information to the response
        PrintWriter out = resp.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Error Details</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; padding: 20px; }");
        out.println(".error-container { background-color: #f8d7da; padding: 15px; border-radius: 5px; margin-bottom: 20px; }");
        out.println(".stack { background-color: #f8f9fa; padding: 15px; border-radius: 5px; font-family: monospace; white-space: pre-wrap; word-break: break-all; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        out.println("<h1>System Error</h1>");
        out.println("<div class='error-container'>");
        out.println("<h2>Error Details</h2>");
        out.println("<p>Status Code: " + statusCode + "</p>");
        if (requestUri != null) {
            out.println("<p>Request URI: " + requestUri + "</p>");
        }
        if (servletName != null && !servletName.isEmpty()) {
            out.println("<p>Servlet Name: " + servletName + "</p>");
        }
        out.println("</div>");
        
        if (throwable != null) {
            out.println("<h2>Exception Details</h2>");
            out.println("<p>Type: " + throwable.getClass().getName() + "</p>");
            out.println("<p>Message: " + throwable.getMessage() + "</p>");
            
            // Get the stack trace
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            
            out.println("<h3>Stack Trace</h3>");
            out.println("<div class='stack'>");
            out.println(stringWriter.toString().replace("<", "&lt;").replace(">", "&gt;"));
            out.println("</div>");
            
            // Show cause if available
            Throwable cause = throwable.getCause();
            if (cause != null) {
                out.println("<h3>Caused By</h3>");
                out.println("<p>Type: " + cause.getClass().getName() + "</p>");
                out.println("<p>Message: " + cause.getMessage() + "</p>");
                
                // Get the cause stack trace
                stringWriter = new StringWriter();
                printWriter = new PrintWriter(stringWriter);
                cause.printStackTrace(printWriter);
                
                out.println("<div class='stack'>");
                out.println(stringWriter.toString().replace("<", "&lt;").replace(">", "&gt;"));
                out.println("</div>");
            }
        }
        
        // Request information
        out.println("<h2>Request Information</h2>");
        out.println("<p>Method: " + req.getMethod() + "</p>");
        out.println("<p>Context Path: " + req.getContextPath() + "</p>");
        out.println("<p>User Agent: " + req.getHeader("User-Agent") + "</p>");
        
        // Navigation
        out.println("<div style='margin-top: 20px;'>");
        out.println("<a href='" + req.getContextPath() + "/'>Go to Homepage</a> | ");
        out.println("<a href='javascript:history.back()'>Go Back</a>");
        out.println("</div>");
        
        out.println("</body>");
        out.println("</html>");
    }
}
