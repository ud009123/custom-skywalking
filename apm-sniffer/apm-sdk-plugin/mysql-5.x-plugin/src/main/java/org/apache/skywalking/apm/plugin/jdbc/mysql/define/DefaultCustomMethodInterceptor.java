package org.apache.skywalking.apm.plugin.jdbc.mysql.define;

import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;

import java.lang.reflect.Method;


public class DefaultCustomMethodInterceptor implements InstanceMethodsAroundInterceptor
{
	@Override
	public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
			MethodInterceptResult result) throws Throwable {
		String operationName = generateOperationName(method);
		ContextManager.createLocalSpan(operationName);
	}

	private String generateOperationName(Method method) {
		String className = method.getDeclaringClass()!=null ? method.getDeclaringClass().getName()+"." : "";
		StringBuilder operationName = new StringBuilder(className + method.getName() + "(");
		if(method.getParameterTypes()!=null)
		{
			Class<?>[] parameterTypes = method.getParameterTypes();
			for (int i = 0; i < parameterTypes.length; i++)
			{
				operationName.append(parameterTypes[i].getName());
				if (i < (parameterTypes.length - 1))
				{
					operationName.append(",");
				}
			}
		}
		operationName.append(")");
		return operationName.toString();
	}

	@Override
		public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
			Object ret) throws Throwable {
		ContextManager.stopSpan();
		return ret;
	}

	@Override public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments,
			Class<?>[] argumentsTypes, Throwable t) {
		ContextManager.activeSpan().errorOccurred().log(t);
	}
}
