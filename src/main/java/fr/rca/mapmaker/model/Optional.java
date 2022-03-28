package fr.rca.mapmaker.model;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @param <T>
 */
public class Optional<T> implements InvocationHandler {

	private static final EmptyOptional EMPTY = new EmptyOptional();

	public static <T> T newInstance(Class<T> clazz) {
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, new Optional<T>());
	}

	public static <T> Optional<T> ofNullable(T t) {
		return t != null
				? new Optional<>(t)
				: EMPTY;
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

	private Optional() {
		// Vide.
	}

	private Optional(T value) {
		this.value = value;
	}

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

	public Optional<T> ifPresent(Consumer<T> consumer) {
		consumer.accept(value);
		return this;
	}

	public Optional<T> orElse(Runnable runnable) {
		return this;
	}

	public T orElse(T defaultValue) {
		return value;
	}

	public <V> Optional<V> map(Function<T, V> mapper) {
		return Optional.ofNullable(mapper.apply(value));
	}

	public Optional<T> filter(Function<T, Boolean> filter) {
		return filter.apply(value) == true
				? this
				: EMPTY;
	}

	private static class EmptyOptional<T> extends Optional<T> {
		@Override
		public final Optional<T> ifPresent(Consumer<T> consumer) {
			return this;
		}

		@Override
		public final Optional<T> orElse(Runnable runnable) {
			runnable.run();
			return this;
		}

		@Override
		public final T orElse(T defaultValue) {
			return defaultValue;
		}

		@Override
		public final <V> Optional<V> map(Function<T, V> mapper) {
			return EMPTY;
		}

		@Override
		public final Optional<T> filter(Function<T, Boolean> filter) {
			return this;
		}
	}
}
