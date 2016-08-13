package cn.com.bookstore.book.web.Servlet.admin;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.com.bookstore.book.domain.Book;
import cn.com.bookstore.book.service.BookService;
import cn.com.bookstore.category.domain.Category;
import cn.com.bookstore.category.service.CategoryService;
import cn.itcast.commons.CommonUtils;

public class AdminAddBookServlet extends HttpServlet {
	private BookService bookService = new BookService();
	private CategoryService categoryService = new CategoryService(); 
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		/*1 把表单数据封装到book对象中
		 * 上传三步
		 */
		//创建工厂
		DiskFileItemFactory factory = new DiskFileItemFactory(20 * 1024,new File("E:/f/temp"));
		//得到解析器
		ServletFileUpload sfu = new ServletFileUpload(factory);
		sfu.setFileSizeMax(20 * 1024);
		//使用解析器去解析request对象，得到List<FileItem>
		
		try {
			List<FileItem> fileItemList = sfu.parseRequest(request);
			/*
			 * 把fileItemList中的数据封装到Book对象中
			 * 把所有的普通表单字段数据先封装到Map中
			 * 再把map中的数据封装到Book对象中,除了image
			 */
			Map<String,String> map = new HashMap<String,String>();
			for(FileItem fileItem : fileItemList){
				if(fileItem.isFormField()){
					map.put(fileItem.getFieldName(), fileItem.getString("UTF-8"));
				}
			}
		
			Book book = CommonUtils.toBean(map, Book.class);
			//为book指定bid
			book.setBid(CommonUtils.uuid());
		
			/*
			 *需要把Map中的cid封装到Category对象中，再把Category赋给Book 
			 */
			Category category = CommonUtils.toBean(map, Category.class);
			book.setCategory(category);
		
			/*
			 * 2 保存上传的文件
			 *   保存的目录
			 *   保存的文件名称
			 */
		//得到保存的目录
			String savepath = this.getServletContext().getRealPath("/book_img");
			//得到文件名称
			String filename = CommonUtils.uuid() + "_" + fileItemList.get(1).getName();
			if(!filename.toLowerCase().endsWith("jpg")){
				request.setAttribute("msg", "文件须是jpg扩展名");
				request.setAttribute("categoryList", categoryService.findAll());
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
				.forward(request,response);
				return;
			}
			File destFile = new File(savepath,filename);
			
			fileItemList.get(1).write(destFile);

			/*
			 * 3 设置book对象的image，即把图片的路径设置给Book的image
			 */
			book.setImage("book_img/" + filename);
			
			/*
			 * 使用BookService完成保存
			 */
			bookService.add(book);
			/*
			 * 校验图片的尺寸
			 */
			Image image = new ImageIcon(destFile.getAbsolutePath()).getImage();
			if(image.getWidth(null)>200 || image.getHeight(null)>200){
				request.setAttribute("msg", "图片尺寸超出200*200");
				request.setAttribute("categoryList", categoryService.findAll());
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
				.forward(request, response);
				return;
			}
			/*
			 * 返回图书列表
			 */
			request.getRequestDispatcher("/admin/AdminBookServlet?method=findAll")
			.forward(request, response);
		
		} catch (Exception e) {
			if(e instanceof FileUploadBase.FileSizeLimitExceededException){
				request.setAttribute("msg", "您上传的文件超出了15kb");
				request.setAttribute("categoryList", categoryService.findAll());
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp").forward(request, response);
				
			}
		}
		
	}

}
