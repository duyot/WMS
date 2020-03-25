package com.wms.sercurity;

import org.springframework.security.core.AuthenticationException;

/**
 * @author truongbx
 * @date 3/22/2020
 */
public class InvalidServerException extends AuthenticationException {
	public InvalidServerException(String msg, Throwable t) {
		super(msg, t);
	}

	public InvalidServerException(String msg) {
		super(msg);
	}
}
