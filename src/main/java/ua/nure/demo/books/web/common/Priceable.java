package ua.nure.demo.books.web.common;


import java.math.BigDecimal;

/**
 * Uses any class that need to calculate price, such as {@link ShoppingCart} etc.
 * @author engsyst
 *
 */
public interface Priceable {
	/**
	 * Calculate price of single item or collection of items.
	 *
	 * @return value of calculated price
	 */
	BigDecimal getPrice();
}
