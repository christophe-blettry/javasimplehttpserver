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
public class ConflictException extends SimpleHttpException {


	public ConflictException() {
		status = HttpStatusEnum.CONFLICT;
	}

	public ConflictException(String message) {
		super(message);
		status = HttpStatusEnum.CONFLICT;
	}

	public ConflictException(String message, Throwable cause) {
		super(message, cause);
		status = HttpStatusEnum.CONFLICT;
	}

	public ConflictException(Throwable cause) {
		super(cause);
		status = HttpStatusEnum.CONFLICT;
	}

}
