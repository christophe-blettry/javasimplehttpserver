/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp.exception;

import net.microfaas.net.simplehttp.HttpStatusEnum;
import net.microfaas.net.simplehttp.SimpleHttpException;

/**
 *
 * @author christophe
 */
public class ForbiddenException extends SimpleHttpException {


	public ForbiddenException() {
		status = HttpStatusEnum.FORBIDDEN;
	}

	public ForbiddenException(String message) {
		super(message);
		status = HttpStatusEnum.FORBIDDEN;
	}

	public ForbiddenException(String message, Throwable cause) {
		super(message, cause);
		status = HttpStatusEnum.FORBIDDEN;
	}

	public ForbiddenException(Throwable cause) {
		super(cause);
		status = HttpStatusEnum.FORBIDDEN;
	}

}
