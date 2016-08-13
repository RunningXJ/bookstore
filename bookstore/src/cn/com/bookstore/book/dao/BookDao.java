package cn.com.bookstore.book.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.com.bookstore.book.domain.Book;
import cn.com.bookstore.category.domain.Category;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class BookDao {
	private QueryRunner qr = new TxQueryRunner();
	public List<Book> findAll(){
		try{
			String sql = "SELECT * FROM book WHERE del=FALSE";
			return qr.query(sql, new BeanListHandler<Book>(Book.class ));
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	public List<Book> findByCategory(String cid) {
		try{
			String sql = "SELECT * FROM book WHERE cid=? and del=FALSE";
			return qr.query(sql, new BeanListHandler<Book>(Book.class ),cid);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	public Book Load(String bid) {
		try{
			String sql = "SELECT * FROM book WHERE bid=?";
			/*
			 * 使用Map映射出两个对象，再给两个对象建立关系
			 */
			Map<String,Object> map =  qr.query(sql, new MapHandler(),bid);
			Category category = CommonUtils.toBean(map, Category.class);
			Book book = CommonUtils.toBean(map, Book.class);
			book.setCategory(category);
			return book;
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		
	}
	public int getCountByCid(String cid) {
		try{
			String sql = "SELECT COUNT(*) FROM book WHERE cid=?";
			Number cnt = (Number)qr.query(sql, new ScalarHandler(),cid);
			return cnt.intValue();
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	public void add(Book book) {
		try{
			String sql = "INSERT INTO book values(?,?,?,?,?,?)";
			Object[] params = {book.getBid(),book.getBname(),book.getPrice(),
					book.getAuthor(),book.getImage(),book.getCategory().getCid()};
			qr.update(sql,params);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		
	}
	/**
	 * 删除图书
	 * @param bid
	 */
	public void delete(String bid){
		String sql = "UPDATE book SET del=TRUE WHERE bid=?";
		try {
			qr.update(sql,bid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public void edit(Book book) {
		try{
			String sql = "UPDATE book SET bname=?,price=?,author=?,image=?,cid=? WHERE bid=?";
			Object[] params = {book.getBname(),book.getPrice(),
					book.getAuthor(),book.getImage(),book.getCategory().getCid(),book.getBid()};
			qr.update(sql,params);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		
	}
}
