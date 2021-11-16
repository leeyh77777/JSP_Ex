package com.filter;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.snslogin.Naver;

public class CommonFilter implements Filter {
	
	public void init(FilterConfig filterConfig) throws ServletException {
		// 네이버 로그인 설정 처리
		try {
			Naver.init(filterConfig);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
		chain.doFilter(request, response);
	}
}
