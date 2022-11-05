package com.example.rqchallenge.logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

public class RequestResponseLogger extends OncePerRequestFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestResponseLogger.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

		long startTime = System.currentTimeMillis();
		filterChain.doFilter(requestWrapper, responseWrapper);
		long timeTakenByAPI = System.currentTimeMillis() - startTime;

		String requestBody = getStringValue(requestWrapper.getContentAsByteArray(),	request.getCharacterEncoding());
		String responseBody = getStringValue(responseWrapper.getContentAsByteArray(), response.getCharacterEncoding());

		LOGGER.info("Processed : HTTP method={}; Url={}; Request Payload={}; Response Code={}; Response={}; Total Time Taken={}",
				request.getMethod(), request.getRequestURI(), requestBody, response.getStatus(), responseBody, timeTakenByAPI);
		responseWrapper.copyBodyToResponse();
	}

	private String getStringValue(byte[] contentAsByteArray, String characterEncoding) {
		try {
			return new String(contentAsByteArray, 0, contentAsByteArray.length, characterEncoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
}
