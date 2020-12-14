/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp.main;

import com.google.gson.Gson;
import java.util.Date;
import net.microfaas.net.simplehttp.HttpMethodEnum;
import net.microfaas.net.simplehttp.HttpResponseEntity;
import net.microfaas.net.simplehttp.PathVariable;
import net.microfaas.net.simplehttp.RequestBody;
import net.microfaas.net.simplehttp.RequestHeader;
import net.microfaas.net.simplehttp.RequestMapping;
import net.microfaas.net.simplehttp.RequestParam;
import net.microfaas.net.simplehttp.exception.BadRequestException;
import net.microfaas.net.simplehttp.exception.ConflictException;

/**
 *
 * @author christophe
 */
public class TestClass {

	public static final String HELLO_W = "Hello World!   ";
	public static final String ADD_HEADER_NAME = "X-my-header";
	public static final String ADD_HEADER_VALUE = "my-header-value";
	public static final String TEST2 = "test2";
	public static final String TEST3 = "test3";
	public static final String TEST4 = "test4";
	public static final String TEST5 = "test5";
	public static final String TEST6 = "test6";

	@RequestMapping("/hello")
	public String getHello() {
		System.out.println(TestClass.class.getName() + ".getHello");
		return HELLO_W + new Date().toString();
	}

	@RequestMapping(value = "/host", success = 204)
	public void getHeaderHost(@RequestHeader("host") String h) {
		System.out.println(TestClass.class.getName() + ".getHeaderHost: " + h);
	}

	@RequestMapping(value = "/addheader")
	public HttpResponseEntity addHeader() {
		System.out.println(TestClass.class.getName() + ".addHeader");
		HttpResponseEntity entity = new HttpResponseEntity();
		entity.setBody(("Hello Headers World!   " + new Date().toString()).getBytes());
		entity.addHeader(ADD_HEADER_NAME, ADD_HEADER_VALUE + " " + new Date().toString());
		return entity;
	}

	@RequestMapping(value = "/useragent", success = 204)
	public void getHeaderUserAgent(@RequestHeader("User-Agent") String h) {
		System.out.println(TestClass.class.getName() + ".getHeaderUserAgent: " + h);
		if (h == null) {
			throw new BadRequestException().setReasonPhrase("header 'User-Agent' not found");
		}
	}

	@RequestMapping(value = "/nocontent", success = 204)
	public void getNoContent() {
		System.out.println(TestClass.class.getName() + ".getNoContent");
	}

	@RequestMapping(value = "/post", method = HttpMethodEnum.POST)
	public String postTest() {
		System.out.println(TestClass.class.getName() + ".postTest");
		return "POST: " + new Date().toString();
	}

	@RequestMapping(value = "/post2", method = HttpMethodEnum.POST)
	public String postTest(@RequestBody TestDto dto) {
		System.out.println(TestClass.class.getName() + ".postTest");
		if (dto.getName() != null) {
			dto.setName(dto.getName().toUpperCase());
		}
		dto.setValue(dto.getValue() + 1);
		return new Gson().toJson(dto);
	}

	@RequestMapping("/test2/{id}")
	public String getTest2(@PathVariable("id") String id) {
		System.out.println(TestClass.class.getName() + ".getTest2");
		return TEST2 + ": " + id + " " + new Date().toString();
	}

	@RequestMapping("/test3")
	public String getTest3(@RequestParam("id") String id) {
		System.out.println(TestClass.class.getName() + ".getTest3");
		return TEST3+": " + id + " " + new Date().toString();
	}

	@RequestMapping("/test4/{id}/test4/{id2}")
	public String getTest4(@PathVariable("id") String id, @PathVariable("id2") String id2) {
		System.out.println(TestClass.class.getName() + ".getTest4");
		return TEST4+": " + id + " " + id2 + " " + new Date().toString();
	}

	@RequestMapping("/test5")
	public String getTest5(@RequestParam("id") String id, @RequestParam("id2") String id2) {
		System.out.println(TestClass.class.getName() + ".getTest5");
		return TEST5+": " + id + " " + id2 + " " + new Date().toString();
	}

	@RequestMapping("/test6/{rp}/test6")
	public String getTest6(@PathVariable("rp") String rp, @RequestParam("id") String id, @RequestParam("id2") String id2) {
		System.out.println(TestClass.class.getName() + ".getTest6");
		return TEST6+": " + rp + " " + id + " " + id2 + " " + new Date().toString();
	}

	@RequestMapping("/test7")
	public String getTest7() {
		throw new ConflictException().setReasonPhrase("ceci est un test de conflit " + new Date().toString());
	}

	@RequestMapping("/test8")
	public String getTest8() {
		throw new RuntimeException();
	}
}
