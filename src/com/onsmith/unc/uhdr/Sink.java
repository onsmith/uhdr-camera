package com.onsmith.unc.uhdr;

public interface Sink<T> {
	public void send(T value);
}
