package cn.com.bookstore.user.service;

import cn.com.bookstore.user.dao.UserDao;
import cn.com.bookstore.user.domain.User;

public class UserService {
	private UserDao userdao = new UserDao();
	//注册功能
	public void regist(User form) throws UserException{
		//校验用户名
		User user = userdao.findByUsername(form.getUsername());
		if(user != null) throw new UserException("用户已经注册");
		//校验邮箱
		user = userdao.findByEmail(form.getEmail());
		if(user != null) throw new UserException("用户已经注册");
		
		//添加用户
		userdao.add(form);
		 		
	}
	//激活功能
	public void active(String code) throws UserException{
		User user = userdao.findByCode(code);
		if(user == null) throw new UserException("激活码无效");
		if(user.getState()) throw new UserException("您已经激活成功");
		//修改用户状态
		userdao.updateState(user.getUid(), true);
	}
	
	//登录
	public User login(User form) throws UserException{
		User user = userdao.findByUsername(form.getUsername());
		
		if(user==null) throw new UserException("用户名不存在");
		if(!user.getPassword().equals(form.getPassword())) throw new UserException("密码错误");
		//if(!user.getState()) throw new UserException("账户未激活");
		return user;
	}
}
