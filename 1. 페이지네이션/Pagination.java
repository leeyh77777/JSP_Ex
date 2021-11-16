package com.core;

import javax.servlet.http.HttpServletRequest;


/**
 * 페이지네이션
 * 
 * @author YONGGYO
 */
public class Pagination {
	private int page; // 페이지 번호
	private  int limit = 20; // 1페이지에 노출될 레코드 수
	private int total; // 전체 레코드 수
	private int pageLinks = 10; // 페이지 갯수(기본값 10개씩)
	private int startNo; // 페이지 구간별 시작 번호
	private int lastNo; // 페이지 구간별 마지막 번호
	private int lastPage; // 마지막 페이지 번호
	private int prevNo = 0; // 이전 페이지 시작 번호
	private int nextNo = 0; // 다음 페이지 시작 번호
	private String url; // 페이징 기본 URL
	private int num;  // 페이지 번호 생성 기준 번호
	private int lastNum; // 마지막 페이지 구간 번호
	
	/**
	 *  생성자
	 *  
	 *   @param request 
	 *   @param limit 1페이지에 노출될 레코드 갯수
	 *   @param total 전체 레코드수
	 *   @param url 쿼리스트링 포함 URL
	 */
	public Pagination(HttpServletRequest request, int limit, int total, String url) {
		try {
			page = 1;
			if (request.getParameter("page") != null) {
				page = Integer.parseInt(request.getParameter("page"));
			};
			page = (page <= 0)?1:page;
			this.limit = (limit <= 0)?20:limit;
			this.total = total;
			lastPage = (int)Math.ceil(this.total / (double)this.limit);
		
			num = (int)Math.floor((page - 1) / pageLinks); // 페이지 생성 기준번호
			startNo = pageLinks * num + 1; // 현재 page 기준의 시작번호
			lastNo = startNo + pageLinks  - 1; // 현재 page 기준의 마지막 번호
			
			// 현재 page 기준 마지막 번호가 마지막 번호보다 크다면
			if (lastNo > lastPage) {
				lastNo = lastPage;
			}
			
			/**
			 * 이전 페이지 시작 번호
			 * 
			 * 첫 페이지 구간(1~pageLinks 번호 까지)가 아닌 경우는 이전 페이지 번호 생성
			 */
			if (num > 0) {
				prevNo = pageLinks * (num - 1) + 1;
			}
			
			/**
			 * 다음 페이지 시작 번호
			 * 
			 * 마지막 페이지 구간이 아니라면 다음 페이지 번호 생성
			 */
			lastNum = (int)Math.floor((lastPage -1) / pageLinks);
			if (num < lastNum) {
				nextNo = pageLinks * (num + 1) + 1;
 			}
			
			/**
			 * url이 null 인 경우는 URL 생성 
			 */
			this.url = url;
			if (this.url == null) {
				StringBuilder sb = new StringBuilder();
				sb.append(request.getRequestURI());
				sb.append("?");
				String qs = request.getQueryString();
				if (qs != null && !qs.equals("")) {
					String[] _qs = qs.split("&");
					boolean isFirst = true;
					for(String s : _qs) {
						if (s.indexOf("page=") != -1) 
							continue;
						else  {
							if (!isFirst) 
								sb.append("&");
							
							sb.append(s);
							isFirst = false;
						} // endif 
					} // endfor 
					sb.append("&");
				} // endif 
				this.url = sb.toString();
			} else {
				this.url += "?";
			}
		} catch(ArithmeticException | NumberFormatException e) {
			// 총 레코드가 0이거나 숫자가 아닌 문자가 들어오는  경우 예외 발생
			e.printStackTrace();
		}
	}
		
	/**
	 * 페이지네이션 HTML 생성 
	 * 
	 * @return String 페이징 HTML
	 */
	public String getPages() {
		StringBuilder sb = new StringBuilder();
		sb.append("<ul class='pagination'>");
		
		
		if (num > 0) {
			// 처음 페이지
			sb.append("<li class='page first'><a href='");
			sb.append(url);
			sb.append("page=1'>First</a></li>");
			
			// 이전 페에지
			sb.append("<li class='page prev'><a href='");
			sb.append(url);
			sb.append("page=");
			sb.append(prevNo);
			sb.append("'>Prev</a></li>");
		}
		
		for (int i = startNo; i <= lastNo; i++) {
			sb.append("<li class='page");
			if (i == page) { // 현재 페이지인 경우 on 클래스 추가
				sb.append(" on");
			}
			
			sb.append("'><a href='");
			sb.append(url);
			sb.append("page=");
			sb.append(i);
			sb.append("'>");
			sb.append(i);
			sb.append("</a></li>");
		}
				
		if (num < lastNum) {
			// 다음페이지
			sb.append("<li class='page next'><a href='");
			sb.append(url);
			sb.append("page=");
			sb.append(nextNo);
			sb.append("'>Next</a></li>");
			
			// 마지막 페이지
			sb.append("<li class='page last'><a href='");
			sb.append(url);
			sb.append("page=");
			sb.append(lastPage);
			sb.append("'>Last</a></li>");
		}
		
		sb.append("</ul>");
		
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return getPages();
	}
}