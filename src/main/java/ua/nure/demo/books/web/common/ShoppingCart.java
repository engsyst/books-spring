package ua.nure.demo.books.web.common;

import ua.nure.demo.books.entity.Book;

import java.math.BigDecimal;
import java.util.Hashtable;

/**
 * <p>Client cart. Store product as {@code key}, and count of product as {@code value}.</p>
 * <p>{@code null} key not allowed. To get total price of products in this cart it implements {@link Priceable}</p>
 * @author engsyst
 *
 * @param <T extends Product>
 */
public class ShoppingCart<T extends Book> extends Hashtable<T, Integer> implements Priceable {

	private static final long serialVersionUID = 1528960148557350187L;

	/**
	 * Adds one more product to the cart. If there are items in the cart, it
	 * increases the amount of at 1.
	 * 
	 * @param item
	 * @return Returns the <b>current</b> number of this product in the cart.
	 */
	public Integer add(T item) {
		return add(item, 1);
	}

	/**
	 * Adds more product to the cart. If there are items in the cart, it
	 * increases the amount of at {@code count}.
	 * 
	 * @param item
	 * @return Returns the <b>current</b> number of this product in the cart.
	 */
	public Integer add(T item, Integer count) {
		Integer sum = getOrDefault(item, 0) + count;
		put(item, count);
		return sum;
	}

	/**
	 * Puts product to the cart. If there are items in the cart, it
	 * <b>replace</b> the amount of at {@code count}.
	 * 
	 * @param item
	 * @param count
	 * @return Returns the <b>previous</b> number of this product in the cart.
	 */
	public Integer put(T item, Integer count) {
		return super.put(item, count);
	}

	/**
	 * <p>
	 * If real count of product less or equal {@code count} parameter removes
	 * product from cart.
	 * </p>
	 * <p>
	 * Otherwise decrease count of product in the cart.
	 * </p>
	 * 
	 * @param item
	 * @param count
	 * @return null if product removed or real count of product in the cart.
	 */
	public Integer remove(T item, Integer count) {
		if (getOrDefault(item, 0) - count <= 0) {
			return super.remove(item);
		} else {
			return add(item, -count);
		}
	}
	
	@Override
	public BigDecimal getPrice() {
		BigDecimal total = BigDecimal.ZERO;
		for(T e : keySet())
			total = total.add(e.getPrice().multiply(new BigDecimal(get(e))));
		return total;
	}
	
	public T getItem(Long id) {
		for (T e : keySet()) {
			if (e.getId().equals(id)) 
				return e;
		}
		return null;
	}

}
