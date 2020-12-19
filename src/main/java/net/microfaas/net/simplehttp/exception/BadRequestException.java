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
public class BadRequestException extends SimpleHttpException {

	
	public BadRequestException() {
		 status=HttpResponseStatus.BAD_REQUEST;
	}

	public BadRequestException(String message) {
		super(message);
		 status=HttpResponseStatus.BAD_REQUEST;
	}

	public BadRequestException(String message, Throwable cause) {
		super(message, cause);
		 status=HttpResponseStatus.BAD_REQUEST;
	}

	public BadRequestException(Throwable cause) {
		super(cause);
		 status=HttpResponseStatus.BAD_REQUEST;
	}

}
