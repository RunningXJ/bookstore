package cn.com.bookstore.book.cart.domain;

import java.math.BigDecimal;

import cn.com.bookstore.book.domain.Book;

public class CartItem {
	private Book book;
	private int count;
	//小计,处理二进制运算的误差问题
	public double getSubtotal(){
		BigDecimal d1 = new BigDecimal(book.getPrice() + "");
		BigDecimal d2 = new BigDecimal(count + "");
		
		return d1.multiply(d2).doubleValue();
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
