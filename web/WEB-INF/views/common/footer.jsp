</div><!-- Closing the container div opened in header.jsp -->
    
    </div><!-- Closing the content-wrapper div -->
      <footer class="site-footer mt-auto py-3 bg-dark text-white">
        <div class="container">
            <div class="row">
                <div class="col-md-4 mb-3 mb-md-0">
                    <h5>PizzaCraft</h5>
                    <p class="text-muted">Delicious pizzas, delivered fast.</p>
                </div>
                <div class="col-md-4 mb-3 mb-md-0">
                    <h5>Quick Links</h5>
                    <ul class="list-unstyled">
                        <li><a href="${pageContext.request.contextPath}/" class="text-white">Home</a></li>
                        <li><a href="${pageContext.request.contextPath}/menu" class="text-white">Menu</a></li>
                        <li><a href="${pageContext.request.contextPath}/profile" class="text-white">My Account</a></li>
                    </ul>
                </div>
                <div class="col-md-4">
                    <h5>Contact</h5>
                    <address class="text-muted">
                        <i class="fas fa-map-marker-alt me-2"></i> 123 Pizza Street<br>
                        <i class="fas fa-phone me-2"></i> (123) 456-7890<br>
                        <i class="fas fa-envelope me-2"></i> contact@pizzacraft.com
                    </address>
                </div>
            </div>
            <div class="row mt-3">
                <div class="col-12 text-center">
                    <hr class="bg-secondary">
                    <p class="mb-0">&copy; 2025 PizzaCraft. All rights reserved.</p>
                </div>
            </div>
        </div>
    </footer>    <!-- Footer styles moved to layout-fixes.css -->    <!-- Bootstrap JS Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" defer></script>

    <!-- Footer CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css">

    <!-- Custom JS -->
    <script src="${pageContext.request.contextPath}/js/script.js"></script>
    
    <!-- Performance Optimizations JS -->
    <script src="${pageContext.request.contextPath}/js/performance-optimizations.js" defer></script>
</body>
</html>