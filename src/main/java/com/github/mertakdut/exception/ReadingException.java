package com.github.mertakdut.exception;

import java.io.Serial;

public class ReadingException extends Exception {

	@Serial
	private static final long serialVersionUID = -3674458503294310650L;

	public ReadingException(String message) {
		super(message);
	}

	public ReadingException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
