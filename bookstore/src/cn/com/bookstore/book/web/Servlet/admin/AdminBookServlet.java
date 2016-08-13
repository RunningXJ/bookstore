package cn.com.bookstore.book.web.Servlet.admin;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.bookstore.book.domain.Book;
import cn.com.bookstore.book.service.BookService;
import cn.com.bookstore.category.domain.Category;
import cn.com.bookstore.category.service.CategoryService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class AdminBookServlet extends BaseServlet {
	private BookService bookService = new BookService();
	private CategoryService categoryService = new CategoryService();
	
	/**
	 * 添加之前
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addPre(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		//查询虽有分类，保存到request中
		request.setAttribute("categoryList",categoryService.findAll());
		return "f:/adminjsps/admin/book/add.jsp";
	}
	/**
	 * 加载图书
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		request.setAttribute("book", bookService.Load(request.getParameter("bid")));
		/*
		 * 获取所有分类保存到request域中
		 */
		request.setAttribute("categoryList",categoryService.findAll());
		return "f:/adminjsps/admin/book/desc.jsp";
	}
	/**
	 * 查看所有图书
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	
		request.setAttribute("bookList",bookService.findAll() );
		return "/adminjsps/admin/book/list.jsp";
	}
	/**
	 * 删除图书
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String delete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		String bid = request.getParameter("bid");
		bookService.delete(bid);
		return findAll(request,response);
	}
	/**
	 * 编辑图书
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String edit(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		request.setCharacterEncoding("utf-8");
		Book book = CommonUtils.toBean(request.getParameterMap(),Book.class);
		Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
		book.setCategory(category);
		bookService.edit(book);
		return findAll(request,response);
	}
}
