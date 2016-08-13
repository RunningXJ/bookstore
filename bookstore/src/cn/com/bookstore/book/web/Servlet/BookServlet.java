package cn.com.bookstore.book.web.Servlet;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.bookstore.book.service.BookService;
import cn.itcast.servlet.BaseServlet;

public class BookServlet extends BaseServlet {
	private BookService bookService = new BookService();

	public String Load(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		request.setAttribute("book", bookService.Load(request.getParameter("bid")));
		return "f:/jsps/book/desc.jsp";
	}
	public String findAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("booklist", bookService.findAll());
	
		return "f:jsps/book/list.jsp";
	}
	public String findByCategory(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		String cid = request.getParameter("cid");
		request.setAttribute("booklist", bookService.findByCategory(cid));
		return "f:jsps/book/list.jsp";
	}
}
