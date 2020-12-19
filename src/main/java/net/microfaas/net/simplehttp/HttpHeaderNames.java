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
public enum HttpHeaderNames {
	NONE(""),
	AUTHORIZATION("Authorization"),
	CONTENT_TYPE("Content-Type");

	private final String name;

	private HttpHeaderNames(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
}
