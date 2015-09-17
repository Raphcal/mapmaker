package fr.rca.mapmaker.model;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @param <T>
 */
public class Optional<T> implements InvocationHandler {
	
	public static <T> T newInstance(Class<T> clazz) {
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, new Optional<T>());
	}
	
	public static <T> boolean set(T proxy, T value) {
		if(proxy != null && Proxy.isProxyClass(proxy.getClass())) {
			final Optional<T> optional = (Optional<T>) Proxy.getInvocationHandler(proxy);
			optional.value = value;
			return true;
		} else {
			return false;
		}
	}
	
	public static <T> T get(T proxy) {
		if(proxy != null && Proxy.isProxyClass(proxy.getClass())) {
			final Optional<T> optional = (Optional<T>) Proxy.getInvocationHandler(proxy);
			return optional.value;
		} else {
			return proxy;
		}
	}
	
	private T value;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(value != null) {
			return method.invoke(value, args);
		} else if(!method.getReturnType().isPrimitive()) {
			return null;
		} else {
			return 0;
		}
	}
}
