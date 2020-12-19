/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp;

import com.google.gson.Gson;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author christophe
 */
@SuppressWarnings("unchecked")
public class HttpMethodInvokerResponse {

	private byte[] bytes;
	private Exception exception;
	private int code = 500;
	private List<Map.Entry<String,String>> headers;

	HttpMethodInvokerResponse(Object methodResponse, int onSuccess) {
		this.code = onSuccess;
		if (methodResponse instanceof String) {
			setBytes(((String) methodResponse).getBytes());
			return;
		}
		if (methodResponse instanceof byte[]) {
			setBytes((byte[]) methodResponse);
			return;
		}
		if (methodResponse instanceof HttpResponseEntity) {
			HttpResponseEntity e = (HttpResponseEntity) methodResponse;
			setBytes(e.getBody());
			setHeaders(e.getHeaders());	
			this.code = e.getStatusCode() != 0 ? e.getStatusCode() : onSuccess;
			return;
		}
		setBytes(new Gson().toJson(methodResponse).getBytes());
		addJsonHeader();
	}

	public HttpMethodInvokerResponse(Exception exception) {
		this.exception = exception;
	}

	public Exception getException() {
		return exception;
	}

	public int getCode() {
		return code;
	}

	public HttpMethodInvokerResponse setCode(int code) {
		this.code = code;
		return this;
	}

	public byte[] getBytes() {
		return bytes;
	}

	private HttpMethodInvokerResponse setBytes(byte[] bytes) {
		this.bytes = bytes;
		return this;
	}

	public List<Map.Entry<String, String>> getHeaders() {
		return headers;
	}

	public void setHeaders(List<Map.Entry<String, String>> headers) {
		this.headers = headers;
	}

	private void addJsonHeader() {
		if(this.headers==null){
			this.headers=new ArrayList<>();
		}
		SimpleEntry<String,String> e = new SimpleEntry<>(HttpHeaderNames.CONTENT_TYPE.name(),HttpHeaderValues.JSON);
		this.headers.add(e);
	}

}
