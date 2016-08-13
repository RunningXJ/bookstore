package cn.com.bookstore.book.cart.web.servlet;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.bookstore.book.cart.domain.Cart;
import cn.com.bookstore.book.cart.domain.CartItem;
import cn.com.bookstore.book.domain.Book;
import cn.com.bookstore.book.service.BookService;
import cn.itcast.servlet.BaseServlet;

public class CartServlet extends BaseServlet {
	
	//添加条目
	public String add(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*1 得到车
		 *2 得到条目
		 *3 添加到车中 
		 * */
		Cart cart = (Cart)request.getSession().getAttribute("cart");
		String bid = request.getParameter("bid");
		Book book = new BookService().Load(bid);
		int count = Integer.parseInt(request.getParameter("count"));
		CartItem cartItem = new CartItem();
		cartItem.setBook(book);
		cartItem.setCount(count);
		cart.add(cartItem);
		return "f:/jsps/cart/list.jsp";
	}
	//清空条目
	public String clear(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Cart cart = (Cart)request.getSession().getAttribute("cart");
		cart.clear();
		return "f:/jsps/cart/list.jsp";
	}
	//删除条目
	public  String delete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Cart cart = (Cart)request.getSession().getAttribute("cart");
		String bid = request.getParameter("bid");
		cart.delete(bid);
		return "f:/jsps/cart/list.jsp";
	}
	
	public  String getCartItems(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		return null;
	}
}
