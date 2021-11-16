package com.controller;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

import com.snslogin.Naver;

public class MainController extends HttpServlet {
	
	private static final long serialVersionUID = -1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		String naverAccessTokenURL = Naver.getAccessCodeURL(request);
		out.println("<a href='" + naverAccessTokenURL  + "'><img height=\"50\" src=\"http://static.nid.naver.com/oauth/small_g_in.PNG\" /></a>");
	}
}
