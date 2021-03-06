package com.example.demo.interceptor;

import com.example.demo.entity.UserInf;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



/**
 * 登录拦截
 */
public class LoginInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler)
			throws Exception {

		//测试
		//if(true) return true;

		// 获取请求的URL
		String url = request.getRequestURI();
		// URL:除了登录请求外，其他的URL都进行拦截控
		if (url.indexOf("/tologin") >= 0 || url.indexOf("/toregister") >= 0) {
			System.out.println(request.getRequestURI()+"允许通过！");

			return true;
		}
		// 获取Session
		HttpSession session = request.getSession();
		UserInf user = (UserInf) session.getAttribute("USER_SESSION");
		// 判断Session中是否有用户数据，如果有，则返回true,继续向下执行
		if (user != null) {
			return true;
		}
		// 不符合条件的给出提示信息，并转发到登录页
		request.setAttribute("msg", "您还没有登录，请先登录！");
		System.out.println(request.getRequestURI()+"不许通过，跳转到登录！");
		response.sendRedirect("/tologin");
		return false;
	}
	@Override
	public void postHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}
	@Override
	public void afterCompletion(HttpServletRequest request, 
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}
