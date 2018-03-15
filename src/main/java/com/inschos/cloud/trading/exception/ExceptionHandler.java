package com.inschos.cloud.trading.exception;


import com.inschos.cloud.trading.assist.kit.L;
import org.apache.log4j.DefaultThrowableRenderer;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IceAnt on 2017/6/20.
 */

public class ExceptionHandler implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		L.log.error("错误日志 : " + request.getRequestURL() + " message:" + ex.getMessage());

		ex.printStackTrace();
		L.log.error("错误日志异常信息",ex);
		String[] rep = rep = DefaultThrowableRenderer.render(ex);

		L.log.error("错误日志异常信息Arr", Arrays.toString(rep));

		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView view = new MappingJackson2JsonView();

		Map<String, String> attributes = new HashMap<>();
		attributes.put("code", "500");
		attributes.put("message", "服务器错误");

		view.setAttributesMap(attributes);
		modelAndView.setView(view);

		return modelAndView;
	}
}
