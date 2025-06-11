/**
 * Cart functionality JavaScript
 * Handles dynamic cart updates and calculations
 * With automatic cart updates (no refresh button needed)
 */

document.addEventListener('DOMContentLoaded', function() {
    // Get all quantity input fields
    const quantityInputs = document.querySelectorAll('.quantity-input');
    const incrementButtons = document.querySelectorAll('.quantity-increment');
    const decrementButtons = document.querySelectorAll('.quantity-decrement');
    
    // Add event listeners for input changes
    quantityInputs.forEach(input => {
        input.addEventListener('change', function(e) {
            updateSubtotal(e);
            autoUpdateCart(e.target);
        });
        input.addEventListener('keyup', updateSubtotal);
        
        // Ensure valid input
        input.addEventListener('blur', function() {
            const value = parseInt(this.value) || 0;
            if (value < 1) this.value = 1;
            if (value > 10) this.value = 10;
            updateSubtotal({target: this});
            autoUpdateCart(this);
        });
    });
    
    // Add event listeners for increment/decrement buttons
    incrementButtons.forEach(button => {
        button.addEventListener('click', function() {
            const input = this.parentElement.querySelector('.quantity-input');
            const currentValue = parseInt(input.value) || 1;
            if (currentValue < 10) {
                input.value = currentValue + 1;
                input.dispatchEvent(new Event('change')); // Trigger change event
                autoUpdateCart(input);
            }
        });
    });
    
    decrementButtons.forEach(button => {
        button.addEventListener('click', function() {
            const input = this.parentElement.querySelector('.quantity-input');
            const currentValue = parseInt(input.value) || 1;
            if (currentValue > 1) {
                input.value = currentValue - 1;
                input.dispatchEvent(new Event('change')); // Trigger change event
                autoUpdateCart(input);
            }
        });
    });
    
    /**
     * Function to send AJAX request to update cart
     */
    function autoUpdateCart(input) {
        const form = input.closest('form');
        if (!form) return;
        
        const formData = new FormData(form);
        
        // Create fetch request
        fetch(form.action, {
            method: 'POST',
            body: formData,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            // Update cart badge if available
            if (data.itemCount !== undefined) {
                updateCartBadge(data.itemCount);
            }
        })
        .catch(error => {
            console.error('Error updating cart:', error);
        });
    }
    
    /**
     * Updates the cart badge count
     */
    function updateCartBadge(count) {
        const badge = document.querySelector('.cart-badge');
        if (badge) {
            badge.textContent = count;
            badge.style.display = count > 0 ? 'inline-block' : 'none';
        }
    }
    
    /**
     * Updates the subtotal and total when quantity changes
     */
    function updateSubtotal(e) {
        // Get the row containing this input
        const row = e.target.closest('tr');
        
        // Get necessary elements
        const quantityInput = e.target;
        const quantity = parseInt(quantityInput.value) || 1;
        
        // Enforce min/max constraints
        if (quantity < 1) {
            quantityInput.value = 1;
        } else if (quantity > 10) {
            quantityInput.value = 10;
        }
        
        // Get unit price from data attribute
        const unitPrice = parseFloat(quantityInput.dataset.unitPrice);
        
        // Calculate and update subtotal
        const subtotal = unitPrice * quantity;
        const subtotalElement = row.querySelector('.subtotal');
        if (subtotalElement) {
            subtotalElement.textContent = '₹' + subtotal.toFixed(2);
        }
        
        // Update the total
        updateTotal();
    }
    
    /**
     * Updates the total price
     */
    function updateTotal() {
        const subtotalElements = document.querySelectorAll('.subtotal');
        let total = 0;
        
        subtotalElements.forEach(el => {
            const value = parseFloat(el.textContent.replace('₹', '')) || 0;
            total += value;
        });
        
        const totalElement = document.querySelector('.total');
        if (totalElement) {
            totalElement.textContent = '₹' + total.toFixed(2);
        }
    }
});
