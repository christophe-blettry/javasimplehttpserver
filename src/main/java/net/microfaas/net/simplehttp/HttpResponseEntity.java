/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author christophe
 */
public class HttpResponseEntity {
	
	private byte [] body;
	private List<Entry<String,String>> headers;
	private int statusCode;

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public List<Entry<String, String>> getHeaders() {
		return headers;
	}

	public void setHeaders(List<Entry<String, String>> headers) {
		this.headers = headers;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public void addHeader(String name, String value) {
		if(headers==null){
			headers = new ArrayList<>();
		}
		headers.add(new SimpleEntry(name,value));
	}
	
}
