package com.filter;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import com.core.*;

/**
 *  사이트 전역 필터
 * 
 */
public class SiteMainFilter implements Filter {
	private FilterConfig filterConfig;
	private String[] staticDirs ={"resources"}; // 정적 디렉토리 설정 (헤더, 푸터 제외)
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		
		/** 데이터 베이스 설정 초기화 */
		DB.init(filterConfig);
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {

		// 사이트 root URL 
		String siteURL = request.getServletContext().getContextPath();
		request.setAttribute("siteURL", siteURL);
		
		// 헤더 추가 
		outlineHeader(request, response);
				
		chain.doFilter(request, response);
		
		// 푸터 추가
		outlineFooter(request, response);
	}
	
	/**
	 * 공통 헤더 처리 
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void outlineHeader(ServletRequest request, ServletResponse response) throws ServletException, IOException  {
		if (isOutlineRequired(request)) {
			response.setContentType("text/html; charset=utf-8");
			String headerFile = isPopup(request)?"/outline/popup_header.jsp":"/outline/header.jsp";
			RequestDispatcher header = request.getRequestDispatcher(headerFile);
			header.include(request, response);
		}
	}
	
	/**
	 * 공통 푸터 처리
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void outlineFooter(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		if (isOutlineRequired(request)) {
			String footerFile = isPopup(request)?"/outline/popup_footer.jsp":"/outline/footer.jsp";
			RequestDispatcher footer = request.getRequestDispatcher(footerFile);
			footer.include(request, response);
		}
	}
	
	/**
	 * 헤더, 푸터가 필요한 경우인지 체크 
	 * 
	 * @param request
	 * @return
	 */
	public boolean isOutlineRequired(ServletRequest request) {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest)request;
			String method = req.getMethod().toUpperCase();
			String URI = req.getRequestURI();
			
			/** 메서드가 GET 이외는 제외 */
			if (!method.equals("GET"))
				return false;
			
			/** 정적 자원인 경우  제외 */
			for(String dir : staticDirs) {
				if (URI.indexOf("/" + dir) != -1) {
					return false;
				}
			}	
		}
		
		return true;
	}
	
	/**
	 * 팝업 페이지 여부 체크 
	 * 
	 * @param request
	 * @return 팝업인 경우 true, 아닌 경우 false
	 */
	public boolean isPopup(ServletRequest request) {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest)request;
			String URI = req.getRequestURI();
			if (URI.indexOf("/popup") != -1) { // 경로가 /popup이 포함되어 있다면 팝업 
				return true;
			}
		}
			
		return false;
	}
}
