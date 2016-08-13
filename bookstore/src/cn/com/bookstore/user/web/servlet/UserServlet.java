package cn.com.bookstore.user.web.servlet;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.bookstore.book.cart.domain.Cart;
import cn.com.bookstore.user.domain.User;
import cn.com.bookstore.user.service.UserException;
import cn.com.bookstore.user.service.UserService;
import cn.itcast.commons.CommonUtils;

import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;
import cn.itcast.servlet.BaseServlet;

public class UserServlet extends BaseServlet {
	private UserService service = new UserService();

	public String quit(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		//销毁session
		request.getSession().invalidate();
		return "f:/index.jsp";
	}
	public String login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		/*
		 * 1一键封装表单数据
		 * 2 输入校验（暂时不写）
		 * 3 调用service的登录方法，如果发生异常
		 * 		保存错误信息，form到request域中，转发回login.jsp
		 * 4 保存user到session中，重定向到index.jsp中
		 * 
		 */
		User form = CommonUtils.toBean(request.getParameterMap(), User.class);
		try {
			User user = service.login(form);
			request.getSession().setAttribute("session_user", user);
			//登录之后马上保存一个购物车
			request.getSession().setAttribute("cart", new Cart());
			return "r:/index.jsp";
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
			request.setAttribute("form", form);
			return "f:/jsps/user/login.jsp";
		}
	
	}
	//邮件激活
	public String active(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		String code = request.getParameter("code");
		try {
			
			service.active(code);
			request.setAttribute("msg", "已经成功激活");//保存成功信息
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());//保存异常信息
		}
		return "f:/jps/msg.jsp";
	}
	
	
	
	public String regist(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		/*1 封装表单数据到User对象中
		 *2 补全 uid 激活码
		 *3 输入校验
		 *		保存错误信息到request域中，转发回regist.jsp
		 *4 调用service方法校验
		 *		保存错误信息到request域中,转发回regist.jsp
		 *5 发邮件
		 *6 保存成功信息到request域中，转发到msg.jsp中
		 * 
		 * 
		 * 
		 */
		//封装表单数据
		User form = CommonUtils.toBean(request.getParameterMap(), User.class);
		//补全
		form.setUid(CommonUtils.uuid());
		form.setCode(CommonUtils.uuid() + CommonUtils.uuid());
		
		//输入校验
		// 用集合来装载错误信息
		Map<String,String> errors = new HashMap<String, String>();
		String username = form.getUsername();
		if(username == null||username.trim().isEmpty()){
			errors.put("username", "用户名不能为空");
		}else if(username.length()<3 || username.length()>10){
			errors.put("username", "用户名长度须在3到10之间");
		}

		String password = form.getPassword();
		if(password == null||password.trim().isEmpty()){
			errors.put("password", "密码不能为空");
		}else if(password.length()<3 || password.length()>10){
			errors.put("password", "密码长度须在3到10之间");
		}
		
		String email = form.getEmail();
		if(email == null||email.trim().isEmpty()){
			errors.put("Email", "邮箱不能为空");
		}else if(!email.matches("\\w+@\\w+\\.\\w+")){
			errors.put("Email","邮箱格式错误");
		}
		
		//判断是否存在错误信息
		if(errors.size()>0){
			request.setAttribute("errors", errors);
			request.setAttribute("form", form);
			return "f:/jsps/user/regist.jsp";
		}
		
		//调用service的regist方法
		try{
			service.regist(form);
		}catch(UserException e){
			request.setAttribute("msg", e.getMessage());
			request.setAttribute("form", form);
			return "f:/jsps/user/regist.jsp";
		}
		
		//执行到这里说明执行成功
		
		//发邮件
		//获取配置文件内容
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		String host = props.getProperty("host");
		String uname = props.getProperty("uname");
		String pwd = props.getProperty("pwd");
		String from = props.getProperty("from");
		String to = form.getEmail();
		String subject = props.getProperty("subject");
		String content = props.getProperty("content");
		content = MessageFormat.format(content, form.getCode());//替换{0}
		Session session = MailUtils.createSession(host, uname, pwd);
		Mail mail = new Mail(from, to, subject, content);
		try {
			MailUtils.send(session, mail);
		} catch (MessagingException e) {
			
		}
		
		 
		//保存成功信息到msg.jsp
		request.setAttribute("msg", "恭喜，注册成功，请马上到邮箱激活");
		return "f:/jsps/msg.jsp";
		
		
	}
	
}
