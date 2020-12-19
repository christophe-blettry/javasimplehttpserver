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
public class NotFoundException extends SimpleHttpException {

	public NotFoundException() {
		status = HttpResponseStatus.NOT_FOUND;
	}

	public NotFoundException(String message) {
		super(message);
		status = HttpResponseStatus.NOT_FOUND;
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
		status = HttpResponseStatus.NOT_FOUND;
	}

	public NotFoundException(Throwable cause) {
		super(cause);
		status = HttpResponseStatus.NOT_FOUND;
	}

}
