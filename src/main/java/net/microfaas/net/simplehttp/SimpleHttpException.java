/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp;

/**
 *
 * @author christophe
 */
public class SimpleHttpException extends RuntimeException {

	private String reasonPhrase;
	protected HttpStatusEnum status;

	public SimpleHttpException() {
		status = HttpStatusEnum.INTERNAL_SERVER_ERROR;
	}

	public SimpleHttpException(String message) {
		super(message);
		status = HttpStatusEnum.INTERNAL_SERVER_ERROR;
	}

	public SimpleHttpException(String message, Throwable cause) {
		super(message, cause);
		status = HttpStatusEnum.INTERNAL_SERVER_ERROR;
	}

	public SimpleHttpException(Throwable cause) {
		super(cause);
		status = HttpStatusEnum.INTERNAL_SERVER_ERROR;
	}

	public HttpStatusEnum getStatus() {
		return status;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}

	public SimpleHttpException setReasonPhrase(String reasonPhrase) {
		this.reasonPhrase = reasonPhrase;
		return this;
	}

}
