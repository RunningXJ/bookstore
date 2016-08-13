package cn.com.bookstore.book.service;

import java.util.List;

import cn.com.bookstore.book.dao.BookDao;
import cn.com.bookstore.book.domain.Book;

public class BookService {
	private BookDao bd = new BookDao();
	public List<Book> findAll(){
		return bd.findAll();
	}
	public List<Book> findByCategory(String cid) {
		// TODO Auto-generated method stub
		return bd.findByCategory(cid);
	}
	public Book Load(String bid) {
		return bd.Load(bid);
	}
	/**
	 * 添加图书
	 * @param book
	 */
	public void add(Book book) {
		bd.add(book);
		
	}
	public void delete(String bid){
		bd.delete(bid);
	}
	public void edit(Book book) {
		bd.edit(book);
		
	}
}
