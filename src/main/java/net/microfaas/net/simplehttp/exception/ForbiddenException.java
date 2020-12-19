/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import net.microfaas.net.simplehttp.SimpleHttpException;

/**
 *
 * @author christophe
 */
public class ForbiddenException extends SimpleHttpException {


	public ForbiddenException() {
		status = HttpResponseStatus.FORBIDDEN;
	}

	public ForbiddenException(String message) {
		super(message);
		status = HttpResponseStatus.FORBIDDEN;
	}

	public ForbiddenException(String message, Throwable cause) {
		super(message, cause);
		status = HttpResponseStatus.FORBIDDEN;
	}

	public ForbiddenException(Throwable cause) {
		super(cause);
		status = HttpResponseStatus.FORBIDDEN;
	}

}
