package cn.com.bookstore.order.web.servlet;



import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.bookstore.book.cart.domain.Cart;
import cn.com.bookstore.book.cart.domain.CartItem;
import cn.com.bookstore.order.OrderException;
import cn.com.bookstore.order.OrderService;
import cn.com.bookstore.order.domain.Order;
import cn.com.bookstore.order.domain.OrderItem;
import cn.com.bookstore.user.domain.User;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class OrderServlet extends BaseServlet {
	private OrderService orderService = new OrderService();
	/**
	 * 支付
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String zhiFu(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		Properties pro = new Properties();
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("merchantInfo.properties");
		pro.load(input);
		String p0_Cmd = "Buy";
		String p1_MerId = pro.getProperty("p1_MerId");
		String p2_Order = request.getParameter("oid");
		String p3_Amt = "0.01";
		String p4_Cur = "CNY";
		String p5_Pid = "";
		String p6_Pcat = "";
		String p7_Pdesc = "";
		String p8_Url = pro.getProperty("p8_Url");
		String p9_SAF = "";
		String pa_MP = "";
		String pd_FrpId = request.getParameter("pd_FrpId");
		String pr_NeedResponse = "1";
		/*
		 * 计算 hmac
		 */
		String keyValue = pro.getProperty("keyValue");
		String hmac = PaymentUtils.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt, p4_Cur, p5_Pid,
				p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
				pd_FrpId, pr_NeedResponse, keyValue);
		/*
		 * 连接易宝支付的网址和13+1个参数
		 */
		StringBuilder sb = new StringBuilder(pro.getProperty("url"));
		sb.append("?p0_Cmd=").append(p0_Cmd);
		sb.append("&p1_MerId=").append(p1_MerId);
		sb.append("&p2_Order=").append(p2_Order);
		sb.append("&p3_Amt=").append(p3_Amt);
		sb.append("&p4_Cur=").append(p4_Cur);
		sb.append("&p6_Pcat=").append(p6_Pcat);
		sb.append("&p7_Pdesc=").append(p7_Pdesc);
		sb.append("&p8_Url=").append(p8_Url);
		sb.append("&p9_SAF=").append(p9_SAF);
		sb.append("&pa_MP=").append(pa_MP);
		sb.append("&pd_FrpId=").append(pd_FrpId);
		sb.append("&pr_NeedResponse=").append(pr_NeedResponse);
		sb.append("&hmac=").append(hmac);
		response.sendRedirect(sb.toString());
	
		return null;
	}
	public String back(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		String p1_MerId = request.getParameter("p1_MerId");
		String r0_Cmd = request.getParameter("r0_Cmd");
		String r1_Code = request.getParameter("r1_Code");
		String r2_TrxId = request.getParameter("r2_TrxId");
		String r3_Amt = request.getParameter("r3_Amt");
		String r4_Cur = request.getParameter("r4_Cur");
		String r5_Pid = request.getParameter("r5_Pid");
		String r6_Order = request.getParameter("r6_Order");
		String r7_Uid = request.getParameter("r7_Uid");
		String r8_MP = request.getParameter("r8_MP");
		String r9_BType = request.getParameter("r9_BType");
		String hmac = request.getParameter("hmac");
		/*
		 * 校验访问者是否为易宝
		 */
		Properties pro = new Properties();
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("merchantInfo.properties");
		pro.load(input);
		String keyValue = pro.getProperty("keyValue");
		boolean bool = PaymentUtils.verifyCallback(hmac, p1_MerId, r0_Cmd, r1_Code, r2_TrxId, r3_Amt, 
				r4_Cur, r5_Pid, r6_Order, 
				r7_Uid, r8_MP, r9_BType, keyValue);
		if(!bool) {
			request.setAttribute("msg", "操作错误!");
			return "f:/jsps/msg.jsp";
		}
		/*
		 * 获取订单状态
		 */
		orderService.zhiFu(r6_Order);
		/*
		 * 判断回调方式，如果为2
		 * 需要回馈以success开头的字符串
		 */
		if(r9_BType.equals("2")){
			response.getWriter().print("success");
		}
		request.setAttribute("msg", "支付成功，等待买家发货");
		return "f:/jsps/msg.jsp";
	}
	/**
	 * 确认订单
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String confirm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		String oid = request.getParameter("oid");
		try {
			orderService.confirm(oid);
			request.setAttribute("msg", "交易完成");
		} catch (OrderException e) {
			request.setAttribute("msg", e.getMessage());
		}
		return "f:/jsps/msg.jsp";
	}
	
	
	
	/**
	 * 记载订单
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		/*
		 * 1 得到oid参数
		 * 2 使用oid调用service方法得到Order对象
		 * 3 保存到request域
		 */
		request.setAttribute("order", orderService.load(request.getParameter("oid")));
		
		return "f:/jsps/order/desc.jsp";
	}
	
	public String myOrders(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		/*
		 * 1 从session中获取用户，在获取器uid
		 * 2 使用uid调用orderService#myOrders()方法，得到List<Order>
		 * 3 把订单保存到request域中，转发到/jsps/order/list.jsp
		 */
		User user = (User)request.getSession().getAttribute("session_user");
		List<Order> orderList = orderService.myOrders(user.getUid());
		request.setAttribute("orderList", orderList);
		
		return "f:/jsps/order/list.jsp";
	}
	
	
	/**
	 * 添加订单
	 * 使用session中的车生成订单
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String add(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 1 从session中获取cart
		 * 2 使用cart获得order对象
		 * 3 调用service方法完成添加订单
		 * 4 保存order到request域中，转发到/jsps/order/desc.jsp
		 */
		Cart cart = (Cart)request.getSession().getAttribute("cart");
		Order order = new Order();
		order.setOid(CommonUtils.uuid());//设置编号
		order.setOrdertime(new Date());//设置下单时间
		order.setState(1);//设置订单状态为1，表示未付款
		User user = (User)request.getSession().getAttribute("session_user");
		order.setOwner(user);//设置订单所有者
		order.setTotal(cart.getTotal());//设置订单的合计，从cart中获取合计
		/*
		 * 创建订单条目
		 */
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		//循环遍历Cart中的所有CartItem,使用每一个CartItem对象创建OrderItem对象，并添加到集合中
		for(CartItem cartItem : cart.getCartItems()){
			OrderItem oi = new OrderItem();
			oi.setIid(CommonUtils.uuid());
			oi.setCount(cartItem.getCount());
			oi.setBook(cartItem.getBook());
			oi.setSubtotal(cartItem.getSubtotal());
			oi.setOrder(order);
			orderItemList.add(oi);
		}
		//把所有的订单条目添加到订单中
		order.setOrderItemList(orderItemList);
		cart.clear();
		/*
		 * 调用orderService添加订单
		 */
		orderService.add(order);
		request.setAttribute("order", order);
		return "f:/jsps/order/desc.jsp";
	}
}
