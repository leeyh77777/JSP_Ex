package com.core;

import java.sql.*;
import javax.servlet.FilterConfig;

public class DB {
	
	private static Connection conn = null;
	private static String DBDriver;
	private static String DBUrl;
	private static String DBUser;
	private static String DBPass;
	
	public static void init(FilterConfig config) {	
		init(
			config.getInitParameter("DBDriver"),
			config.getInitParameter("DBUrl"),
			config.getInitParameter("DBUser"),
			config.getInitParameter("DBPass")
		);
	}
	
	public static void init(String DBDriver, String DBUrl, String DBUser, String DBPass) {
		DB.DBDriver = DBDriver;
		DB.DBUrl = DBUrl;
		DB.DBUser = DBUser;
		DB.DBPass = DBPass;
	}
	
	public static Connection getConnection() throws ClassNotFoundException, SQLException  {
		Class.forName(DBDriver);
		
		conn = DriverManager.getConnection(DBUrl, DBUser, DBPass);
		
		return conn;
	}	
}
