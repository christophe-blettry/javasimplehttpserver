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
		Constructor<?> c;
		Object methodResponse;
		try {
			c = call.getClasse().getConstructor();
			Object o = c.newInstance();
			Matcher m = call.getPattern().matcher(uri.getPath());
			m.matches();
			Object[] args = buildArgs(call, m, uri, headers, body);
//			System.out.println(this.getClass().getName() + ".caller: args.length: " + (args != null ? args.length : 0));
			if (args != null) {
				methodResponse = call.getMethod().invoke(o, args);
			} else {
				methodResponse = call.getMethod().invoke(o);
			}
			return Optional.ofNullable(new HttpMethodInvokerResponse(methodResponse, call.getOnSuccess()));
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
			return Optional.ofNullable(new HttpMethodInvokerResponse(ex));
		} catch (InvocationTargetException ex) {
			if (ex.getCause() instanceof SimpleHttpException) {
				return Optional.ofNullable(new HttpMethodInvokerResponse((SimpleHttpException) ex.getCause()));
			}
			return Optional.ofNullable(new HttpMethodInvokerResponse(ex));
		}
	}

	private Object[] buildArgs(HttpCallMapper call, Matcher m, URI uri, List<Entry<String, String>> headers, String body) throws BadRequestException {

		Parameter[] parameters = call.getMethod().getParameters();
		Object[] args = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			Parameter p = parameters[i];
			args[i] = buildArgsFromParameters(p, body, m, uri, headers);
		}
		return args;
	}

	Object buildArgsFromParameters(Parameter p, String body, Matcher m, URI uri, List<Entry<String, String>> headers) {
		try {
			if (p.isAnnotationPresent(RequestBody.class) && body != null) {
				return new Gson().fromJson(body, p.getParameterizedType());
			}
			if (p.isAnnotationPresent(PathVariable.class)) {
				PathVariable pv = (PathVariable) p.getAnnotation(PathVariable.class);
				String name = pv.value();
				return m.group(name);
			}
			if (p.isAnnotationPresent(RequestParam.class)) {
				RequestParam rp = (RequestParam) p.getAnnotation(RequestParam.class);
				String name = rp.value();
				QueryStringDecoder decoder = new QueryStringDecoder(uri.toString());
				if (decoder.parameters().containsKey(name)) {
					return decoder.parameters().get(name).get(0);
				}
			}
			if (p.isAnnotationPresent(RequestHeader.class)) {
				return getArgFromHeader(p, headers);
			}
		} catch (JsonParseException | IllegalStateException | IllegalArgumentException ex) {
			throw new BadRequestException();
		}
		return null;
	}

	Object getArgFromHeader(Parameter p, List<Entry<String, String>> headers) {
		RequestHeader rh = (RequestHeader) p.getAnnotation(RequestHeader.class);
		String name;
		if (rh.name().equals(HttpHeaderNames.NONE)) {
			name = rh.value().toLowerCase();
		} else {
			name = rh.name().toString();
		}
		String h = null;
		if (headers != null) {
			h = headers.stream().filter(e -> e.getKey().equalsIgnoreCase(name)).map(Entry::getValue).findFirst().orElse(null);
		}
		return h;
	}

}
