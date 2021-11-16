package com.core;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.text.SimpleDateFormat;

/** 
 * 로그 기록 
 * 
 * @author YONGGYO
 */
public class Logger  {
	
	public static final int DEBUG = 0;
	public static final int INFO = 1;
	public static final int WARN = 2;
	public static final int ERROR = 3;
	public static final int FATAL = 4;
	private static String[] errorLevels = {"DEBUG", "INFO", "WARN", "ERROR", "FATAL" };
	
	private static String channel = "general"; // 메세지 타입(기본값 general)
	private static String logDir; // 로그 디렉토리
	private static String logPath; // 로그 파일 경로
	private static Writer writer; // writer;
	private static PrintWriter out; 
	
	/**
	 * 로거 초기화
	 * 
	 * @param channel - 로거 구분 채널
	 */
	public static void init(String channel) {
		Logger.channel = channel;
	}
	
	/**
	 * 로거 초기화 
	 * 
	 * @param channel - 로거 구분 채널
	 * @param logDir - 로거 파일 저장경로
	 */
	public static void init(String channel,  String logDir) {
		Logger.channel = channel;
		Logger.logDir = logDir;
		
		Date date = Calendar.getInstance().getTime();
		String logFile = Logger.channel + "_" + new SimpleDateFormat("yyyyMMdd").format(date) + ".log";
		
		logPath = Logger.logDir + File.separator + logFile;
	}
	
	/**
	 * 로그 저장경로
	 * 
	 * @return 로그 저장 경로
	 */
	public static String getLogPath() {
		return logPath;
	}
	
	/**
	 * 로거 기록 writer 설정 
	 * 
	 * @param writer
	 */
	public static void setWriter(Writer writer) {
		if (writer != null && writer instanceof Writer) {
			Logger.writer = writer;
			out = new PrintWriter(new BufferedWriter(Logger.writer));
		}
	}
	
	/**
	 *  로거 기록 Stream 설정
	 *  Stream 형태라면 Writer로 변환하여 설정 
	 * 
	 * @param OutputStream stream 
	 */
	public static void setStream(OutputStream stream) {
		if (stream != null && stream instanceof OutputStream) {
			writer = new OutputStreamWriter(stream);
			out = new PrintWriter(new BufferedWriter(writer));
		}
	}
 	
	/**
	 * 로그 기록 
	 * 
	 * @param message
	 */
	public static void log(String message) {
		log(message, INFO);
	}
	

	public static void log(StringBuilder sb) {
		log(sb, INFO);
	}
	
	public static void log(StringBuffer sb) {
		log(sb, INFO);
	}
	
	/**
	 *  Request 정보 기록
	 *    
	 */
	public static void log(ServletRequest request) {
		HttpServletRequest req = (HttpServletRequest)request;
		StringBuilder sb = new StringBuilder();
		sb.append("IP : ");
		sb.append(req.getRemoteAddr());
		sb.append(" / URL : ");
		sb.append(req.getMethod());
		sb.append(" ");
		sb.append(req.getRequestURL());
		log(sb.toString());
		
		sb = new StringBuilder();
		sb.append("UA : ");
		sb.append(req.getHeader("user-agent"));
		
		log(sb.toString());
	}
	
	/** 
	 * 에러 로그 기록 
	 * 
	 * @param e 
	 */
	public static void log(Throwable e) {
		e.printStackTrace();
		
		StackTraceElement[] stes = e.getStackTrace();
		log("------------------------- Error Start -------------------------------------------------------------------", ERROR);
		for(StackTraceElement s : stes) {
			StringBuilder sb = new StringBuilder();
			sb.append("ClassName : ");
			sb.append(s.getClassName());
			sb.append(" / File : ");
			sb.append(s.getFileName());
			sb.append(" / Line : ");
			sb.append(s.getLineNumber());
			sb.append("/ Method : ");
			sb.append(s.getMethodName());
			sb.append(" / ClassLoader : ");
			sb.append(s.getClassLoaderName());
			log(sb.toString(), ERROR);
		}
		log("------------------------- Error End -------------------------------------------------------------------", ERROR);
	}
	
	/**
	 * 로그 기록 
	 *
	 * @param message 기록할 메세지
	 * @param level 로그 기록 레벨(DEBUG, INFO, WARN, ERROR, FATAL)
	 */
	public static void log(String message, int level) {
		
		if (message == null || message.trim().equals(""))
			return;
		
		if (level < 0 || level > 4) level = 1;
		
		try {
			if (writer == null) {
				if (logPath != null && ! logPath.trim().equals("")) {
					writer = new FileWriter(getLogPath(), true);
					out = new PrintWriter(new BufferedWriter(writer));
				} else {
					return;
				}
			}
			
			String logTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
			
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(errorLevels[level]);
			sb.append("][");
			sb.append(logTime);
			sb.append("]");
			sb.append(message);
			sb.append("\r\n");
			out.write(sb.toString());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void log(StringBuilder sb, int level) {
		log(sb.toString(), level);
	}

	public static void log(StringBuffer sb, int level) {
		log(sb.toString(), level);
	}
}
