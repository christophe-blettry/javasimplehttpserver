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
public enum HttpStatusEnum {

	OK(200),
	CREATED(201),
	ACCEPTED(202),
	NO_CONTENT(204),
	PARTIAL_CONTENT(206),
	BAD_REQUEST(400),
	UNAUTHORIZED(401),
	FORBIDDEN(403),
	NOT_FOUND(404),
	METHOD_NOT_ALLOWED(405),
	NOT_ACCEPTABLE(406),
	PROXY_AUTHENTIFICATION_REQUIRED(407),
	REQUEST_TIMEOUT(408),
	CONFLICT(409),
	X_BAD_REQUEST(490), // must have reponse body to indicate to the client the reason of the error
	INTERNAL_SERVER_ERROR(500),
	NOT_IMPLEMENTED(501);

	final int code;

	private HttpStatusEnum(int code) {
		this.code = code;
	}

	public int code() {
		return code;
	}

}
