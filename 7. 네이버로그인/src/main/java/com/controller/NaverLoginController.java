package com.controller;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.snslogin.Naver;

/**
 * 네이버 로그인 콜백 URL 처리 및 회원 프로필 조회  
 * 
 * @author YONGGYO
 *
 */
public class NaverLoginController extends HttpServlet {
	
	private static final long serialVersionUID = -1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String[] result = Naver.getAccessToken(request);
			if (result[1] != null) {
				HashMap<String, String> userInfo = Naver.getUserProfile(result[1]);
				Iterator<String> ir = userInfo.keySet().iterator();
				while(ir.hasNext()) {
					String key = ir.next();
					System.out.println(key + "= " + userInfo.get(key));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
