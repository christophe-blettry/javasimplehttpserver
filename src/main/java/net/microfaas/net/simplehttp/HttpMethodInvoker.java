/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp;

import net.microfaas.net.simplehttp.exception.BadRequestException;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;

/**
 *
 * @author christophe
 */
@SuppressWarnings("unchecked")
public class HttpMethodInvoker {

	public Optional<HttpMethodInvokerResponse> caller(URI uri, HttpCallMapper call, List<Map.Entry<String, String>> headers, String body) {
		Constructor c;
		Object methodResponse;
		try {
			c = call.getClasse().getConstructor();
			Object o = c.newInstance();
			Matcher m = call.getPattern().matcher(uri.getPath());
			m.matches();
			Object args[] = buildArgs(call, m, uri, headers, body);
//			System.out.println(this.getClass().getName() + ".caller: args.length: " + (args != null ? args.length : 0));
			if (args != null) {
				methodResponse = call.getMethod().invoke(o, args);
			} else {
				methodResponse = call.getMethod().invoke(o);
			}
			return Optional.ofNullable(new HttpMethodInvokerResponse(methodResponse, call.getOnSuccess()));
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			if (ex instanceof InvocationTargetException) {
				InvocationTargetException ite = InvocationTargetException.class.cast(ex);
				if (ite.getCause() instanceof HttpSimpleException) {
					return Optional.ofNullable(new HttpMethodInvokerResponse((HttpSimpleException) ite.getCause()));
				}
			}
			return Optional.ofNullable(new HttpMethodInvokerResponse(ex));
		}
//		return Optional.empty();
	}

	private Object[] buildArgs(HttpCallMapper call, Matcher m, URI uri, List<Entry<String, String>> headers, String body) throws BadRequestException {
		QueryStringDecoder decoder = new QueryStringDecoder(uri.toString());
		Parameter parameters[] = call.getMethod().getParameters();
		Object args[] = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			Parameter p = parameters[i];
//			System.out.println(this.getClass().getName() + ".buildArgs: parameter: name: " + p.getName());
			if (p.isAnnotationPresent(RequestBody.class)) {
				if (body != null) {
					try {
						args[i] = new Gson().fromJson(body, p.getParameterizedType());
					} catch (JsonParseException ex) {
						throw new BadRequestException();
					}
				}
			}
			if (p.isAnnotationPresent(PathVariable.class)) {
				PathVariable pv = (PathVariable) p.getAnnotation(PathVariable.class);
				String name = pv.value();
//				System.out.println(this.getClass().getName() + ".buildArgs: PathVariable: name: " + name);
				try {
					args[i] = m.group(name);
				} catch (IllegalStateException | IllegalArgumentException ex) {
					//TODO WARN
				}
			}
			if (p.isAnnotationPresent(RequestParam.class)) {
				RequestParam rp = (RequestParam) p.getAnnotation(RequestParam.class);
				String name = rp.value();
//				System.out.println(this.getClass().getName() + ".buildArgs: RequestParam: name: " + name);
				if (decoder.parameters().containsKey(name)) {
					args[i] = decoder.parameters().get(name).get(0);
				}
			}
			if (p.isAnnotationPresent(RequestHeader.class)) {
				RequestHeader rh = (RequestHeader) p.getAnnotation(RequestHeader.class);
				String name = rh.value().toLowerCase();
//				System.out.println(this.getClass().getName() + ".buildArgs: RequestParam: name: " + name);
				String h = null;
				if (headers != null) {
					h = headers.stream().filter(e -> e.getKey().equalsIgnoreCase(name)).map(e -> e.getValue()).findFirst().orElse(null);
				}
				args[i] = h;
			}
		}
		return args;
	}

}
