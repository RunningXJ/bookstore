package cn.com.bookstore.order.domain;

import java.util.Date;
import java.util.List;

import cn.com.bookstore.user.domain.User;

public class Order {
	private String oid;
	private Date ordertime;//下单时间
	private double total;//合计
	private int state;//订单状态有四种1：未付款 2 付款但未发货 3 发货但未确认收货 4 确认完成交易
	private User owner;//订单所有者
	private String adress;//收货地址 
	private List<OrderItem> orderItemList;

	
	public List<OrderItem> getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(List<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public Date getOrdertime() {
		return ordertime;
	}
	public void setOrdertime(Date ordertime) {
		this.ordertime = ordertime;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}
	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
}
