/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import net.microfaas.net.simplehttp.HttpSimpleException;

/**
 *
 * @author christophe
 */
public class UnauthorizedException extends HttpSimpleException {

	public UnauthorizedException() {
		status = HttpResponseStatus.UNAUTHORIZED;
	}

	public UnauthorizedException(String message) {
		super(message);
		status = HttpResponseStatus.UNAUTHORIZED;
	}

	public UnauthorizedException(String message, Throwable cause) {
		super(message, cause);
		status = HttpResponseStatus.UNAUTHORIZED;
	}

	public UnauthorizedException(Throwable cause) {
		super(cause);
		status = HttpResponseStatus.UNAUTHORIZED;
	}

}
