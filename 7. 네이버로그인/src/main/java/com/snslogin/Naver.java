package com.snslogin;

import java.net.URLEncoder;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.security.SecureRandom;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.parser.*;
import org.json.simple.*;


/**
 * 네이버 로그인 API 
 * 
 * @author YONGGYO
 */
public class Naver {
	private static String clientId; // 애플리케이션 클라이언트 아이디값
	private static String clientSecret; // 애플리케이션 클라이언트 시크릿 값
	private static String callbackURL; // API설정에 등록한 콜백 URL
	
	/** 
	 * 네이버 API ClientId, Secret 설정
	 * 
	 * @param config
	 * @throws UnsupportedEncodingException 
	 */
	public static void init(FilterConfig config) throws UnsupportedEncodingException {
		init(
			config.getInitParameter("NaverClientId"),
			config.getInitParameter("NaverClientSecret"),
			config.getInitParameter("NaverCallbackURL")
		);
	}
	
	/**
	 * 네이버 API ClientId, Secret 설정
	 * 
	 * @param clientId
	 * @param clientSecret
	 * @throws UnsupportedEncodingException 
	 */
	public static void init(String clientId, String clientSecret, String callbackURL) throws UnsupportedEncodingException {
		Naver.clientId = clientId;
		Naver.clientSecret = clientSecret;
		Naver.callbackURL = URLEncoder.encode(callbackURL, "UTF-8");
	}
	
	/**
	 * 접근 Token URL 생성 
	 * 
	 * @param HttpServlertRequest request - 세션 생성을 위한 HttpServlertRequest 인스턴스  
	 * @return
	 */
	public static String getAccessCodeURL(HttpServletRequest request) {
		
		String state = getRandomState();
		StringBuilder sb = new StringBuilder();
		HttpSession session = request.getSession();
		
		sb.append("https://nid.naver.com/oauth2.0/authorize?response_type=code");
		sb.append("&client_id=");
		sb.append(clientId);
		sb.append("&redirect_uri=");
		sb.append(callbackURL);
		sb.append("&state=");
		sb.append(state);
		
		session.setAttribute("state", state);
	
		return sb.toString();
	}
	
	/**
	 * 무작위 state 난수 생성 
	 * 
	 * @return
	 */
	private static String getRandomState() {
		SecureRandom random = new SecureRandom();
		String state = new BigInteger(130, random).toString();
		return state;
	}
	
	/**
	 * AccessToken  발급
	 * 
	 * @param request - 네이버로 발급받은 code 값을 사용하여 AccessToken 발급
	 *  							- 부정 데이터 확인을 위해 세션에 저장되어 있는 값과 state값이 일치하는지 여부 체크 
	 *  
	 * @return
	 */
	public static String[] getAccessToken(HttpServletRequest request) throws Exception {
		
		String code = request.getParameter("code");
		String state = request.getParameter("state");
		if (code == null || state == null) {
			throw new Exception("잘못된 Naver API 호출입니다.");
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("https://nid.naver.com/oauth2.0/token?grant_type=authorization_code");
		sb.append("&client_id=");
		sb.append(clientId);
		sb.append("&client_secret=");
		sb.append(clientSecret);
		sb.append("&redirect_uri=");
		sb.append(callbackURL);
		sb.append("&code=");
		sb.append(code);
		sb.append("&state=");
		sb.append(state);
		
		URL url = new URL(sb.toString());
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		int responseCode = conn.getResponseCode();
		BufferedReader br;
		if (responseCode == 200) { // 정상 호출 
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else { // 에러발생
			br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
	
		String line;
		StringBuilder res = new StringBuilder();
		while((line = br.readLine()) != null) {
			res.append(line);
		}
		
		br.close();
		
		String[] result;
		JSONParser jsonParser= new JSONParser();
		JSONObject json = (JSONObject)jsonParser.parse(res.toString());
		if (responseCode == 200) {
			String accessToken = (String)json.get("access_token");
			String refreshToken = (String)json.get("refresh_token");
			result = new String[] { String.valueOf(responseCode), accessToken, refreshToken };
		} else {
			String errorDescription = (String)json.get("error_description");
			String error = (String)json.get("error");
			result = new String[] { String.valueOf(responseCode), errorDescription, error };
		}
		
		return result;
	}
	
	/**
	 * 회원 프로필 조회
	 * 
	 * @param accessToken
	 * @return
	 */
	public static HashMap<String, String> getUserProfile(String accessToken) {
		String apiURL = "https://openapi.naver.com/v1/nid/me";
		
		Map<String, String> reqHeaders = new HashMap<>();
		reqHeaders.put("Authorization", "Bearer " + accessToken);
		
		
		HashMap<String, String> userInfo = null;
		try {
			JSONObject result = httpRequest(apiURL, reqHeaders);
			String resultCode = (String)result.get("resultcode");
			if (resultCode.equals("00")) {
				userInfo = new HashMap<String, String>();
				JSONObject data = (JSONObject)result.get("response");
				Iterator<String> ir = data.keySet().iterator();
				while(ir.hasNext()) {
					String key = ir.next();
					userInfo.put(key, (String)data.get(key));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userInfo;
	}
	
	public static JSONObject httpRequest(String apiUrl, Map<String, String> reqHeaders) throws MalformedURLException, IOException, ParseException {
		URL url  = new URL(apiUrl);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		Iterator<String> ir = reqHeaders.keySet().iterator();
		while(ir.hasNext()) {
			String key = ir.next();
			conn.setRequestProperty(key, reqHeaders.get(key));
		}
		 
		InputStream in;
		int responseCode = conn.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			in = conn.getInputStream();
		}  else {
			in = conn.getErrorStream();
		}
		
		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(isr);
		StringBuilder res = new StringBuilder();
		
		String line;
		while((line = br.readLine()) != null) {
			res.append(line);
		}
		
		br.close();
		isr.close();
			
		JSONObject json = (JSONObject)new JSONParser().parse(res.toString());
		
		return json;
	}
}
