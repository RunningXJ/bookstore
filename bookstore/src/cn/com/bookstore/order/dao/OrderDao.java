package cn.com.bookstore.order.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.com.bookstore.book.domain.Book;
import cn.com.bookstore.order.domain.Order;
import cn.com.bookstore.order.domain.OrderItem;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class OrderDao {
	private QueryRunner qr = new TxQueryRunner();
	/*
	 * 添加订单
	 */
	 public void addOrder(Order order){
		 String sql = "INSERT INTO orders values(?,?,?,?,?,?)";
		 /*
		  * 处理util的date转换成sql的Timestamp
		  */
		 Timestamp timestamp = new Timestamp(order.getOrdertime().getTime());
		 Object[] params = {order.getOid(),timestamp,order.getTotal(),
				 order.getState(),order.getOwner().getUid(),order.getAdress()};
		 try {
			qr.update(sql,params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	 }
	 
	 /**
	  * 添加条目
	  */
	 public void addOrderItemList(List<OrderItem> orderItemList){
		 try{
			 String sql = "INSERT INTO orderitem values(?,?,?,?,?)";
			 /*
			  * 把orderItemList转换成两维数组
			  * 把一个OrderItem对象转换成一个一维数组
			  */
			 Object[][] params = new Object[orderItemList.size()][];
			 //循环遍历orderItemList，使用每个orderItem对象为params中每个一维数组赋值
			 for(int i = 0;i < orderItemList.size();i++){
				 OrderItem item = orderItemList.get(i);
				 params[i] = new Object[]{item.getIid(),item.getCount(),
						 item.getSubtotal(),item.getOrder().getOid(),
						 item.getBook().getBid()
				 };
				
			 }
			 qr.batch(sql, params);//执行批处理
		 }catch(SQLException e){
			 throw new RuntimeException(e);
		 }
	 }
	 /**
	  * 通过uid查询订单
	  * @param uid
	  * @return
	  */
	public List<Order> findByUid(String uid) {
		/*
		 * 1 通过uid查询当前用户的所有List<Order>
		 * 2 循环遍历得到所有的orderItem
		 */
		String sql = "SELECT * FROM orders WHERE uid=?";
		try {
			List<Order> orderList =  qr.query(sql, new BeanListHandler<Order>(Order.class),uid);
			for(Order order : orderList){
				loadOrderItems(order);//为order加载所有订单条目
			}
			return orderList;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	/**
	 * 加载指定的订单的条目
	 * @param order
	 * @throws SQLException 
	 */
	private void loadOrderItems(Order order) throws SQLException {
		/*
		 * 查询两张表：orderitem、book
		 * 
		 */
		String sql = "SELECT * FROM orderitem i, book b WHERE i.bid=b.bid and oid=?";
		//因为一行结果不再是只对应一个javabean，所以不能再使用BeanListHandler,而是MapListHandler()
		
		
			List<Map<String,Object>> mapList  = qr.query(sql, new MapListHandler(),order.getOid());
			/*
			 * maplist是多个map，每个map对应一行结果集
			 * 我们需要一个Map生成两个对象：OrderItem、book,然后再建立两者的关系（把book设置给Order）
			 * 循环遍历每个Map,使用map生成两个对象，然后建立关系（最终结果一个OrderItem）,把orderItem保存起来
			 */
			List<OrderItem> orderItemList = toOrderItemList(mapList);
			order.setOrderItemList(orderItemList);
	
	}
	private List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
		List<OrderItem> orderItemList  = new ArrayList<OrderItem>();
		for(Map<String,Object> map: mapList){
			OrderItem item  = toOrderItem(map);
			orderItemList.add(item);
		}
		return orderItemList;
	}

	private OrderItem toOrderItem(Map<String, Object> map) {
		OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		orderItem.setBook(book);
		return orderItem;
	}

	public Order load(String oid) {
		String sql = "SELECT * FROM orders WHERE oid=?";
		try {
			Order order =  qr.query(sql, new BeanHandler<Order>(Order.class),oid);		
				loadOrderItems(order);//为order加载所有订单条目
				return order;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	/**
	 * 使用oid查询订单状态
	 * @param oid
	 * @return
	 */
	public int getStateByOid(String oid){
		String sql = "SELECT state FROM orders WHERE oid=?";
		try {
			return (Integer)qr.query(sql, new ScalarHandler(),oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 修改订单状态
	 * @param oid
	 * @param state
	 */
	public void updateState(String oid,int state){
		String sql = "UPDATE orders SET state=? WHERE oid=?";
		try {
			qr.update(sql,state,oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
