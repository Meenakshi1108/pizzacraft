// Pizza Delivery System JavaScript

// Wait for the document to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    
    // Auto-dismiss flash messages after 5 seconds
    setTimeout(function() {
        const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
        alerts.forEach(function(alert) {
            const closeButton = alert.querySelector('button.close');
            if (closeButton) {
                closeButton.click();
            }
        });
    }, 5000);
    
    // Quantity incrementing/decrementing in product detail page
    const quantityInput = document.getElementById('quantity');
    if (quantityInput) {
        document.querySelector('.quantity-increment')?.addEventListener('click', function() {
            const max = parseInt(quantityInput.getAttribute('max'));
            const currentValue = parseInt(quantityInput.value);
            if (currentValue < max) {
                quantityInput.value = currentValue + 1;
            }
        });
        
        document.querySelector('.quantity-decrement')?.addEventListener('click', function() {
            const min = parseInt(quantityInput.getAttribute('min'));
            const currentValue = parseInt(quantityInput.value);
            if (currentValue > min) {
                quantityInput.value = currentValue - 1;
            }
        });
    }
    
    // Form validation
    const forms = document.querySelectorAll('.needs-validation');
    if (forms) {
        Array.prototype.slice.call(forms).forEach(function(form) {
            form.addEventListener('submit', function(event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    }
    
    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-toggle="tooltip"]'));
    if (tooltipTriggerList.length > 0 && typeof bootstrap !== 'undefined') {
        tooltipTriggerList.map(function(tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }
});

function formatCurrency(amount) {
    return "₹" + parseFloat(amount).toFixed(2);
}