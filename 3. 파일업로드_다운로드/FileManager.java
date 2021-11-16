package com.core;

import java.io.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;


public class FileManager {

	private static final int MAX_UPLOAD_SIZE = 10000000;
	
	/**
	 * 파일 업로드
	 * 
	 * @param request
	 */
	public static HashMap<String, Object> upload(HttpServletRequest request) {
		HashMap<String, Object> result = new HashMap<>();
		try {
			String path = request.getServletContext().getRealPath(File.separator + "resources" + File.separator + "/upload");
			String uploadURL  = request.getServletContext().getContextPath()+ "/resources/upload";
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("UTF-8"); // 한글 파일 깨짐 방지
			upload.setSizeMax(MAX_UPLOAD_SIZE);
		
			List<FileItem> items = upload.parseRequest(request);
			Iterator<FileItem> params = items.iterator();
			while(params.hasNext()) {
				FileItem item = params.next();
				String key = item.getFieldName();
				if (item.isFormField()) { // 일반 양식 데이터 
					String value = item.getString("UTF-8");
					result.put(key, value);
				} else { //파일 데이터
					HashMap<String, String> fileInfo = new HashMap<>();
					String fNm = item.getName();
					String contentType = item.getContentType();
					
					fNm = System.currentTimeMillis() + "_" + fNm.substring(fNm.lastIndexOf(File.separator) + 1);
					long fileSize = item.getSize();
					
					String uploaded = path + File.separator + fNm;
					String uploadedURL = uploadURL + "/" + fNm;
					File file = new File(uploaded);
					item.write(file);
					fileInfo.put("fileName", fNm);
					fileInfo.put("contentType", contentType);
					fileInfo.put("fileSize", String.valueOf(fileSize));
					fileInfo.put("uploadedPath", uploaded);
					fileInfo.put("uploadedURL", uploadedURL);
					
					result.put(key, fileInfo);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 파일 다운로드 
	 * 
	 * @param request
	 * @param filePath
	 * @throws UnsupportedEncodingException 
	 */
	public static void download(HttpServletResponse response, String filePath)  {
		if (response == null || filePath == null || filePath.trim().equals("")) 
			return;
		
		String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
		File file = new File(filePath); 
		
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath))) {
			OutputStream out = response.getOutputStream();
			
			response.setHeader("Content-Description", "File Transfer");
			response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes(), "ISO8859_1"));
			response.setHeader("Content-Type", "application/octet-stream");
			response.setIntHeader("Expires", 0);
			response.setHeader("Cache-Control", "must-revalidate");
			response.setHeader("Pragma", "public");
			response.setHeader("Content-Length", String.valueOf(file.length()));
			
			int i;
			while((i = bis.read()) != -1) {
				out.write(i);
			}

			out.flush();
		} catch (IOException | IllegalStateException  e) {
			e.printStackTrace();
		}
	}
}
