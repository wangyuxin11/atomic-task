package com.wanda.base.task.utils;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * description: 调用spring方法，格式spring:beanId.method
 * 
 * @author senvon time : 2015年5月5日 下午2:21:17
 */
public class SpringCallbackUtils {
	private final static Pattern SPRING_BEAN_METHOD_PATTERN = Pattern.compile("spring:(\\w+)\\.(\\w+)");

	public static boolean isBeanAndMethodExist(ApplicationContext applicationContext, String springBeanMethod,
			Class<?> returnType, Class<?>... paramTypes) {
		Matcher matcher = SPRING_BEAN_METHOD_PATTERN.matcher(springBeanMethod);
		if (!matcher.matches()) {
			return false;
		}
		// 获得bean
		Object bean = null;
		try {
			bean = applicationContext.getBean(matcher.group(1));
		} catch (BeansException e) {
			return false;
		}
		if (bean == null) {
			return false;
		}
		// 获得方法
		Method accessibleMethod = MethodUtils.getAccessibleMethod(bean.getClass(), matcher.group(2), paramTypes);
		if (accessibleMethod == null) {
			return false;
		}
		// 检查返回类型
		if (!returnType.isAssignableFrom(accessibleMethod.getReturnType())) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokeBeanMethod(ApplicationContext applicationContext, String springBeanMethod,
			Class<T> returnType, Object[] params, Class<?>... paramTypes) throws Exception {
		Matcher matcher = SPRING_BEAN_METHOD_PATTERN.matcher(springBeanMethod);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("springBeanMethod格式必须是spring:beanId.method");
		}
		// 获得bean
		Object bean = applicationContext.getBean(matcher.group(1));
		if (bean == null) {
			throw new IllegalArgumentException("bean[" + matcher.group(1) + "] not found");
		}
		// 获得方法
		Method accessibleMethod = MethodUtils.getAccessibleMethod(bean.getClass(), matcher.group(2), paramTypes);
		if (accessibleMethod == null) {
			throw new IllegalArgumentException("bean[" + matcher.group(1) + "] not found method [" + matcher.group(2)
					+ "(" + StringUtils.join(paramTypes, ",") + ")]");
		}
		// 检查返回类型
		if (!returnType.isAssignableFrom(accessibleMethod.getReturnType())) {
			throw new IllegalArgumentException("bean[" + matcher.group(1) + "] method [" + matcher.group(2)
					+ "] not return " + returnType.getName() + " or subclass");
		}
		T execResult = (T) accessibleMethod.invoke(bean, params);
		return execResult;
	}
}
