# PizzaCraft CSS Architecture

This document explains the CSS structure of the PizzaCraft application to help with future maintenance.

## CSS File Organization

The CSS files are organized in a layered approach, loaded in the following order:

### 1. Base Styles
- **style.css**: Core styling for the entire application including colors, typography, buttons, and common components.

### 2. Layout and Structure
- **layout-fixes.css**: Handles page structure, including the crucial footer positioning and content wrapper.
- **responsive-fixes.css**: Responsive behavior for different screen sizes.
- **footer.css**: Dedicated footer styling.

### 3. Component Styles
- **hero.css**: Styles for the hero section on the homepage.
- **hero-fixes.css**: Fixes and enhancements for the hero section.
- **pizza-detail.css**: Styles specific to the pizza detail page.
- **cart-fixes.css**: Shopping cart styling.
- **order-details-fixes.css**: Order details page styling.

### 4. Enhancement Styles
- **extra-fixes.css**: Miscellaneous fixes and enhancements.
- **performance-optimizations.css**: Optimizations for better performance.

## CSS Responsibility Areas

Each file has a specific responsibility:

| File | Purpose |
|------|---------|
| style.css | Core styling and design system |
| layout-fixes.css | Page structure and footer positioning |
| responsive-fixes.css | Responsive design adaptations |
| footer.css | Footer-specific styling |
| hero.css | Hero section on the homepage |
| hero-fixes.css | Fixes for the hero section |
| pizza-detail.css | Pizza detail page specific styling |
| cart-fixes.css | Shopping cart styling |
| order-details-fixes.css | Order details page styling |
| extra-fixes.css | Miscellaneous fixes |
| performance-optimizations.css | Loading optimizations |

## Maintenance Guidelines

1. **Keep related styles together**: Put component-specific styles in their respective files.
2. **Avoid duplication**: Don't repeat the same styles in multiple files.
3. **Use the cascade**: Load more specific/overriding styles later in the sequence.
4. **Comment your code**: Especially when creating fixes.
5. **Keep layout separate**: Body and structural elements belong in layout-fixes.css.

Following this organization will help maintain a clean and manageable CSS codebase.
