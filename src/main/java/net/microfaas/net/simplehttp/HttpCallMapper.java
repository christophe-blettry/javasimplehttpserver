/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 *
 * @author christophe
 */
public class HttpCallMapper {

	private static final Map<HttpMethodEnum,List<HttpCallMapper>> MAPPER = new HashMap<>();

	private HttpMethodEnum httpMethod;
	private Pattern pattern;
	private final HttpStatusEnum onSuccess;
	private final Class classe;
	private final Method method;
	private String pat;
	static {
		Arrays.asList(HttpMethodEnum.values()).forEach(m -> MAPPER.put(m,new ArrayList<>()));
	}

	public HttpCallMapper(RequestMapping requestMapping, Class classe, Method method) {
		this.classe = classe;
		this.method = method;
		this.onSuccess=requestMapping.success();
		String path = requestMapping.value();
		this.httpMethod=requestMapping.method();
		if (path != null) {
			this.pat = computePattern(path);
			this.pattern = Pattern.compile(pat);
			MAPPER.get(this.httpMethod).add(this);
		}
	}

	static Optional<HttpCallMapper> findByPath(String path, HttpMethodEnum method) {
//		System.out.println(HttpCallMapper.class.getName()+".findByPath: "+path);
		return MAPPER.get(method).stream().filter(hcm -> hcm.pattern.matcher(path).matches()).findFirst();
	}

	public Class getClasse() {
		return classe;
	}

	public Method getMethod() {
		return method;
	}

	public HttpStatusEnum getOnSuccess() {
		return onSuccess;
	}

	public HttpMethodEnum getHttpMethod() {
		return httpMethod;
	}

	public Pattern getPattern() {
		return pattern;
	}

	private String computePattern(String path) {
		String p = path;
		p = p.replace("{{", "(?<");
		p = p.replace("}}", ">.+)");
		p = p.replace("{", "(?<");
		p = p.replace("}", ">[^/]+)");
		return "^" + p + "$";
	}

	@Override
	public String toString() {
		return "HttpCallMapper{" + "httpMethod=" + httpMethod + ", pattern=" + pattern + ", onSuccess=" + onSuccess + ", classe=" + classe + ", method=" + method + ", pat=" + pat + '}';
	}

	public static String toGlobalString() {
		StringBuilder sb = new StringBuilder();
		MAPPER.values().forEach(l -> {
			l.forEach(hcm -> sb.append(hcm.toString()).append("\n"));
		});
		return sb.toString();
	}
}
