/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 *
 * @author christophe
 */
public class HttpSimpleException extends RuntimeException {

	private String reasonPhrase;
	protected HttpResponseStatus status;

	public HttpSimpleException() {
		status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
	}

	public HttpSimpleException(String message) {
		super(message);
		status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
	}

	public HttpSimpleException(String message, Throwable cause) {
		super(message, cause);
		status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
	}

	public HttpSimpleException(Throwable cause) {
		super(cause);
		status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
	}

	public HttpResponseStatus getStatus() {
		return status;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}

	public HttpSimpleException setReasonPhrase(String reasonPhrase) {
		this.reasonPhrase = reasonPhrase;
		return this;
	}

}
