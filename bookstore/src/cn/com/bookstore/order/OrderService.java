package cn.com.bookstore.order;

import java.sql.SQLException;
import java.util.List;

import cn.com.bookstore.order.dao.OrderDao;
import cn.com.bookstore.order.domain.Order;
import cn.itcast.jdbc.JdbcUtils;

public class OrderService {
	private OrderDao orderDao = new OrderDao();
	public void zhiFu(String oid){
		int state = orderDao.getStateByOid(oid);
		if(state==1){
			orderDao.updateState(oid, 2);
		}
	}
	/**
	 * 添加订单
	 * 处理事务
	 * @param order
	 */
	public void add(Order order){
		try{
			JdbcUtils.beginTransaction();//开启事务
			orderDao.addOrder(order);
			orderDao.addOrderItemList(order.getOrderItemList());
			
			JdbcUtils.commitTransaction();//提交事务
		}catch(SQLException e){
			try {
				JdbcUtils.rollbackTransaction();//回滚事务
			} catch (SQLException e1) {
				throw new RuntimeException(e);
			}
		}
	}
	/**
	 * 我的订单
	 * @param uid
	 * @return
	 */
	public List<Order> myOrders(String uid) {
		return orderDao.findByUid(uid);
	}
	/**
	 * 加载订单
	 * @param oid
	 * @return
	 */
	public Order load(String oid) {
		return orderDao.load(oid);
	}
	
	public void confirm(String oid) throws OrderException{
		int state = orderDao.getStateByOid(oid);
		if(state != 3) throw new OrderException("订单确认失败");
		orderDao.updateState(oid, 4);
	}
	
}
