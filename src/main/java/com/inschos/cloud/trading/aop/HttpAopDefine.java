package com.inschos.cloud.trading.aop;


import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseRequest;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.annotation.GetActionBeanAnnotation;
import com.inschos.cloud.trading.assist.kit.ConstantKit;
import com.inschos.cloud.trading.assist.kit.HttpKit;
import com.inschos.cloud.trading.assist.kit.JsonKit;
import com.inschos.cloud.trading.assist.kit.StringKit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Component
@Aspect
public class HttpAopDefine {

	@Around("@annotation(com.inschos.cloud.trading.annotation.GetActionBeanAnnotation)")
	public Object checkAuth(ProceedingJoinPoint joinPoint) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

		if (request != null) {
			BaseResponse response = new BaseResponse();

			String buildCode = request.getParameter(BaseRequest.FILEID_BUILDCODE);
			String platform = request.getParameter(BaseRequest.FILEID_PLATFORM);
			String apiCode = request.getParameter(BaseRequest.FILEID_APICODE);

			if (ConstantKit.IS_PRODUCT && !isValidVersion(buildCode, platform)) {
				response.code = BaseResponse.CODE_VERSION_FAILURE;
				response.message = "版本过低或无效，请安装最新版本";
				return JsonKit.bean2Json(response);
			}

			String accessToken = request.getParameter(BaseRequest.FILEID_ACCESS_TOKEN);
//			B.log.info("accessToken:{}", accessToken);

			ActionBean bean = ActionBean.parseToken(accessToken);

			// TODO: 2018/2/7
			if (false && !isAccess(joinPoint, bean)) {
				response.code = BaseResponse.CODE_ACCESS_FAILURE;
				response.message = "未登录";
				return JsonKit.bean2Json(response);
			}
			if(StringKit.isInteger(buildCode)){
				bean.buildCode = Integer.valueOf(buildCode);
			}
			if(StringKit.isInteger(apiCode)){
				bean.apiCode = Integer.valueOf(apiCode);
			}
			bean.platform = platform;
			bean.url = request.getRequestURL().toString();

			bean.body = HttpKit.readRequestBody(request);
			if (StringKit.isEmpty(bean.body)) {
				bean.body = "{}";
			}

			Object[] params = new Object[] { bean };
			Object returnValue = joinPoint.proceed(params);
			return returnValue;
		} else {
			return joinPoint.proceed();
		}
	}

	private boolean isValidVersion(String buildCode, String platform) {
		if (StringKit.isEmpty(buildCode)) {
			return false;
		}
		return true;
	}

	private boolean isAccess(ProceedingJoinPoint joinPoint, ActionBean bean) {
		boolean isAuthCheck = true;

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		if (signature != null) {
			Method method = signature.getMethod();
			if (method != null && method.isAnnotationPresent(GetActionBeanAnnotation.class)) {
				GetActionBeanAnnotation annotation = signature.getMethod().getAnnotation(GetActionBeanAnnotation.class);
				isAuthCheck = annotation.isCheckAccess();
			}
		}

		if (isAuthCheck) {
			if (!ActionBean.getSalt().equals(bean.salt)) {
				return false;
			}
		}

		return true;
	}
}
