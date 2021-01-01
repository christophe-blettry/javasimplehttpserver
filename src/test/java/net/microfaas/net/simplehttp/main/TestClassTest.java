/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp.main;

import net.microfaas.net.simplehttp.example.ApiExample;
import net.microfaas.net.simplehttp.example.BeanExample;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.microfaas.net.simplehttp.HttpHeaderNames;
import net.microfaas.net.simplehttp.HttpServer;
import net.microfaas.net.simplehttp.HttpStatusEnum;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author christophe
 */
public class TestClassTest {

	public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
	static Random random = new Random();
	static HttpServer server = new HttpServer();
	static OkHttpClient client = new OkHttpClient();

	public TestClassTest() {
	}

	@BeforeClass
	public static void setUpClass() throws InterruptedException, ExecutionException, TimeoutException {
		server.setSearchClassForAnnotations(Collections.singleton("net.microfaas.net.simplehttp.example"));
		Thread t = new Thread(server);
		t.start();
		server.getThreadStarted().get(10, TimeUnit.MINUTES);
	}

	@AfterClass
	public static void tearDownClass() {
		server.shutdown();
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of getHello method, of class ApiExample.
	 */
	@Test
	public void testGetHello() throws IOException {
		System.out.println("getHello");
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/hello")
				.build();
		String result = null;
		try (Response response = client.newCall(request).execute()) {
			result = response.body().string();
		}
		assertTrue(result.startsWith(ApiExample.HELLO_W));
	}

	/**
	 * Test of getHeaderHost method, of class ApiExample.
	 */
	@Test
	public void testGetHeaderHost() throws IOException {
		System.out.println("getHeaderHost");
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/host")
				.build();
		int resultCode = 0;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
		}
		assertEquals(HttpStatusEnum.NO_CONTENT.code(), resultCode);
	}

	/**
	 * Test of addHeader method, of class ApiExample.
	 */
	@Test
	public void testAddHeader() throws IOException {
		System.out.println("addHeader");
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/addheader")
				.build();
		List<String> result = new ArrayList<>();
		int resultCode = 0;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
			result = response.headers(ApiExample.ADD_HEADER_NAME);
		}
		assertEquals(HttpStatusEnum.OK.code(), resultCode);
		assertFalse(result.isEmpty());
		if (!result.isEmpty()) {
			assertTrue(result.get(0).startsWith(ApiExample.ADD_HEADER_VALUE));
		}
	}

	/**
	 * Test of getHeaderUserAgent method, of class ApiExample.
	 */
	@Test
	public void testGetHeaderUserAgent() throws IOException {
		System.out.println("getHeaderUserAgent");
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/useragent")
				.build();
		int result = 0;
		try (Response response = client.newCall(request).execute()) {
			result = response.code();
		}
		assertEquals(HttpStatusEnum.NO_CONTENT.code(), result);

	}

	/**
	 * Test of getHeaderAuth method, of class ApiExample.
	 */
	@Test
	public void testGetHeaderAuth() throws IOException {
		System.out.println("getHeaderAuth");
		String auth = "Bearer " + UUID.randomUUID().toString();
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/auth")
				.addHeader(HttpHeaderNames.AUTHORIZATION.toString(), auth)
				.build();
		int resultCode = 0;
		String result = null;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
			result = response.body().string();
		}
		assertEquals(HttpStatusEnum.OK.code(), resultCode);
		assertEquals(result, auth);
	}

	/**
	 * Test of getHeaderAuth method, of class ApiExample.
	 */
	@Test
	public void testGetHeaderAuth2() throws IOException {
		System.out.println("testGetHeaderAuth2");
		String auth = "Bearer " + UUID.randomUUID().toString();
		String name = "NAME" + UUID.randomUUID().toString();
		int value = random.nextInt(Integer.MAX_VALUE - 10);
		BeanExample dto = new BeanExample(value, name);
		System.out.println("testGetHeaderAuth2: dto: " + dto.toString());
		RequestBody reqbody = RequestBody.create(new Gson().toJson(dto), JSON);
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/auth2")
				.post(reqbody)
				.addHeader(HttpHeaderNames.AUTHORIZATION.toString(), auth)
				.build();
		int resultCode = 0;
		String result = null;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
			result = response.body().string();
		}
		assertEquals(HttpStatusEnum.OK.code(), resultCode);
		assertEquals(result, auth);
	}

	/**
	 * Test of getNoContent method, of class ApiExample.
	 */
	@Test
	public void testGetNoContent() throws IOException {
		System.out.println("getNoContent");
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/nocontent")
				.build();
		int result = 0;
		try (Response response = client.newCall(request).execute()) {
			result = response.code();
		}
		assertEquals(HttpStatusEnum.NO_CONTENT.code(), result);
	}

	/**
	 * Test of postTest method, of class ApiExample.
	 */
	@Test
	public void testPostTest_0args() throws IOException {
		System.out.println("postTest");
		RequestBody reqbody = RequestBody.create(new byte[0], null);
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/post")
				.post(reqbody)
				.build();
		int result = 0;
		try (Response response = client.newCall(request).execute()) {
			result = response.code();
		}
		assertEquals(HttpStatusEnum.OK.code(), result);
	}

	/**
	 * Test of postTest method, of class ApiExample.
	 */
	@Test
	public void testPostTest_TestDto() throws IOException {
		System.out.println("postTestDto");
		String name = "NAME" + UUID.randomUUID().toString();
		int value = random.nextInt(Integer.MAX_VALUE - 10);
		BeanExample dto = new BeanExample(value, name);
		System.out.println("postTestDto: dto: " + dto.toString());
		RequestBody reqbody = RequestBody.create(new Gson().toJson(dto), JSON);
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/post2")
				.post(reqbody)
				.build();
		int resultCode = 0;
		String result = null;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
			result = response.body().string();
			System.out.println("postTestDto: result: " + result);
		}
		assertEquals(HttpStatusEnum.OK.code(), resultCode);
		assertTrue(result.contains(name.toUpperCase()));
		assertTrue(result.contains(Integer.toString(value + 1)));
	}

	/**
	 * Test of getTest2 method, of class ApiExample.
	 */
	@Test
	public void testGetTest2() throws IOException {
		System.out.println("getTest2");
		String id = UUID.randomUUID().toString();
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/test2/" + id)
				.build();
		int resultCode = 0;
		String result = null;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
			result = response.body().string();
		}
		assertEquals(HttpStatusEnum.OK.code(), resultCode);
		assertTrue(result.contains(ApiExample.TEST2));
		assertTrue(result.contains(id));
	}

	/**
	 * Test of getTest3 method, of class ApiExample.
	 */
	@Test
	public void testGetTest3() throws IOException {
		System.out.println("getTest3");
		String id = UUID.randomUUID().toString();
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/test3?id=" + id)
				.build();
		int resultCode = 0;
		String result = null;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
			result = response.body().string();
		}
		assertEquals(HttpStatusEnum.OK.code(), resultCode);
		assertTrue(result.contains(ApiExample.TEST3));
		assertTrue(result.contains(id));
	}

	/**
	 * Test of getTest4 method, of class ApiExample.
	 */
	@Test
	public void testGetTest4() throws IOException {
		System.out.println("getTest4 2 pathParams");
		String id = UUID.randomUUID().toString();
		String id2 = UUID.randomUUID().toString();
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/test4/" + id + "/test4/" + id2)
				.build();
		int resultCode = 0;
		String result = null;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
			result = response.body().string();
		}
		assertEquals(HttpStatusEnum.OK.code(), resultCode);
		assertTrue(result.contains(ApiExample.TEST4));
		assertTrue(result.contains(id));
		assertTrue(result.contains(id2));
	}

	/**
	 * Test of getTest5 method, of class ApiExample.
	 */
	@Test
	public void testGetTest5() throws IOException {
		System.out.println("getTest5");
		String id = UUID.randomUUID().toString();
		String id2 = UUID.randomUUID().toString();
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/test5?id=" + id + "&id2=" + id2)
				.build();
		int resultCode = 0;
		String result = null;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
			result = response.body().string();
		}
		assertEquals(HttpStatusEnum.OK.code(), resultCode);
		assertTrue(result.contains(ApiExample.TEST5));
		assertTrue(result.contains(id));
		assertTrue(result.contains(id2));
	}

	/**
	 * Test of getTest6 method, of class ApiExample.
	 */
	@Test
	public void testGetTest6() throws IOException {
		System.out.println("getTest6");
		String rp = UUID.randomUUID().toString();
		String id = UUID.randomUUID().toString();
		String id2 = UUID.randomUUID().toString();
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/test6/" + rp + "/test6?id=" + id + "&id2=" + id2)
				.build();
		int resultCode = 0;
		String result = null;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
			result = response.body().string();
		}
		assertEquals(HttpStatusEnum.OK.code(), resultCode);
		assertTrue(result.contains(ApiExample.TEST6));
		assertTrue(result.contains(rp));
		assertTrue(result.contains(id));
		assertTrue(result.contains(id2));
	}

	/**
	 * Test of getTest7 method, of class ApiExample.
	 */
	@Test
	public void testGetTest7() throws IOException {
		System.out.println("getTest7");
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/test7")
				.build();
		int result = 0;
		try (Response response = client.newCall(request).execute()) {
			result = response.code();
		}
		assertEquals(HttpStatusEnum.CONFLICT.code(), result);
	}

	/**
	 * Test of getTest8 method, of class ApiExample.
	 */
	@Test
	public void testGetTest8() throws IOException {
		System.out.println("getTest8");
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/test8")
				.build();
		int result = 0;
		try (Response response = client.newCall(request).execute()) {
			result = response.code();
		}
		assertEquals(HttpStatusEnum.INTERNAL_SERVER_ERROR.code(), result);
	}

	/**
	 * Test of getTest8 method, of class ApiExample.
	 */
	@Test
	public void testGetTest9() throws IOException {
		System.out.println("getTest9");
		String toFind ="js/mon.js";
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/test9/"+toFind)
				.build();
		int resultCode = 0;
		String result = null;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
			result = response.body().string();
			System.out.println("getTest9: result: "+result);
		}
		assertEquals(HttpStatusEnum.OK.code(), resultCode);
		assertEquals(toFind,result);
	}
	/**
	 * Test of getTest8 method, of class ApiExample.
	 */
	@Test
	public void testGetTest10() throws IOException {
		System.out.println("getTest10");
		String oth ="other";
		String toFind ="js/mon/mon.js";
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/test10/"+oth+"/"+toFind)
				.build();
		int resultCode = 0;
		String result = null;
		String [] ar=null;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
			result = response.body().string();
			System.out.println("getTest10: result: "+result);
			if(result != null){
				ar=result.split("::");
			}
		}
		assertEquals(HttpStatusEnum.OK.code(), resultCode);
		assertTrue(ar!=null);
		if(ar!=null){
			assertTrue(ar.length==2);
			assertEquals(oth,ar[0]);
			assertEquals(toFind,ar[1]);
		}
	}
	
	/**
	 * Test of getTest6 method, of class ApiExample.
	 */
	@Test
	public void testBean() throws IOException {
		System.out.println("testBean");
		String name = UUID.randomUUID().toString();
		int value = new Random().nextInt(Integer.MAX_VALUE);
		BeanExample bean = new BeanExample(value, name);
		System.out.println("testBean: bean:       " + bean);
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/beanexample?value=" + value + "&name=" + name)
				.build();
		int resultCode = 0;
		String result = null;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
			result = response.body().string();
			BeanExample beanResult = new Gson().fromJson(result, BeanExample.class);
			System.out.println("testBean: beanResult: " + beanResult);
			assertTrue(bean.equals(beanResult));
		}
		assertEquals(HttpStatusEnum.OK.code(), resultCode);
	}
}
