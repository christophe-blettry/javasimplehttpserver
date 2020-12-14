/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp.main;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.microfaas.net.simplehttp.HttpServer;
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
		server.setSearchClassForAnnotations(Arrays.asList(new String[]{"net.microfaas.net.simplehttp.main"}));
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
	 * Test of getHello method, of class TestClass.
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
		assertTrue(result.startsWith(TestClass.HELLO_W));
	}

	/**
	 * Test of getHeaderHost method, of class TestClass.
	 */
	@Test
	public void testGetHeaderHost() throws IOException {
		System.out.println("getHeaderHost");
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/host")
				.build();
		int result = 0;
		try (Response response = client.newCall(request).execute()) {
			result = response.code();
		}
		assertEquals(result, 204);
	}

	/**
	 * Test of addHeader method, of class TestClass.
	 */
	@Test
	public void testAddHeader() throws IOException {
		System.out.println("addHeader");
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/addheader")
				.build();
		List<String> result = new ArrayList<>();
		try (Response response = client.newCall(request).execute()) {
			result = response.headers(TestClass.ADD_HEADER_NAME);
		}
		assertFalse(result.isEmpty());
		if (!result.isEmpty()) {
			assertTrue(result.get(0).startsWith(TestClass.ADD_HEADER_VALUE));
		}
	}

	/**
	 * Test of getHeaderUserAgent method, of class TestClass.
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
		assertEquals(result, 204);

	}

	/**
	 * Test of getNoContent method, of class TestClass.
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
		assertEquals(result, 204);
	}

	/**
	 * Test of postTest method, of class TestClass.
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
		assertEquals(result, 200);
	}

	/**
	 * Test of postTest method, of class TestClass.
	 */
	@Test
	public void testPostTest_TestDto() throws IOException {
		System.out.println("postTestDto");
		String name = "NAME" + UUID.randomUUID().toString();
		int value = random.nextInt(Integer.MAX_VALUE - 10);
		TestDto dto = new TestDto(value, name);
		System.out.println("postTestDto: dto: "+dto.toString());
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
			System.out.println("postTestDto: result: "+result);
		}
		assertEquals(resultCode, 200);
		assertTrue(result.contains(name.toUpperCase()));
		assertTrue(result.contains(Integer.toString(value + 1)));
	}

	/**
	 * Test of getTest2 method, of class TestClass.
	 */
	@Test
	public void testGetTest2() throws IOException {
		System.out.println("getTest2");
		String id = UUID.randomUUID().toString();
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/test2/"+id)
				.build();
		int resultCode = 0;
		String result = null;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
			result = response.body().string();
		}
		assertEquals(resultCode, 200);
		assertTrue(result.contains(TestClass.TEST2));
		assertTrue(result.contains(id));
	}

	/**
	 * Test of getTest3 method, of class TestClass.
	 */
	@Test
	public void testGetTest3() throws IOException {
		System.out.println("getTest3");
		String id = UUID.randomUUID().toString();
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/test3?id="+id)
				.build();
		int resultCode = 0;
		String result = null;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
			result = response.body().string();
		}
		assertEquals(resultCode, 200);
		assertTrue(result.contains(TestClass.TEST3));
		assertTrue(result.contains(id));
	}

	/**
	 * Test of getTest4 method, of class TestClass.
	 */
	@Test
	public void testGetTest4() throws IOException {
		System.out.println("getTest4 2 pathParams");
		String id = UUID.randomUUID().toString();
		String id2 = UUID.randomUUID().toString();
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/test4/"+id+"/test4/"+id2)
				.build();
		int resultCode = 0;
		String result = null;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
			result = response.body().string();
		}
		assertEquals(resultCode, 200);
		assertTrue(result.contains(TestClass.TEST4));
		assertTrue(result.contains(id));
		assertTrue(result.contains(id2));
	}

	/**
	 * Test of getTest5 method, of class TestClass.
	 */
	@Test
	public void testGetTest5() throws IOException {
		System.out.println("getTest5");
		String id = UUID.randomUUID().toString();
		String id2 = UUID.randomUUID().toString();
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/test5?id="+id+"&id2="+id2)
				.build();
		int resultCode = 0;
		String result = null;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
			result = response.body().string();
		}
		assertEquals(resultCode, 200);
		assertTrue(result.contains(TestClass.TEST5));
		assertTrue(result.contains(id));
		assertTrue(result.contains(id2));
	}

	/**
	 * Test of getTest6 method, of class TestClass.
	 */
	@Test
	public void testGetTest6() throws IOException {
		System.out.println("getTest6");
		String rp = UUID.randomUUID().toString();
		String id = UUID.randomUUID().toString();
		String id2 = UUID.randomUUID().toString();
		Request request = new Request.Builder()
				.url(server.getBaseUrl() + "/test6/"+rp+"/test6?id="+id+"&id2="+id2)
				.build();
		int resultCode = 0;
		String result = null;
		try (Response response = client.newCall(request).execute()) {
			resultCode = response.code();
			result = response.body().string();
		}
		assertEquals(resultCode, 200);
		assertTrue(result.contains(TestClass.TEST6));
		assertTrue(result.contains(rp));
		assertTrue(result.contains(id));
		assertTrue(result.contains(id2));
	}

//	/**
//	 * Test of getTest7 method, of class TestClass.
//	 */
//	@Test
//	public void testGetTest7() {
//		System.out.println("getTest7");
//		TestClass instance = new TestClass();
//		String expResult = "";
//		String result = instance.getTest7();
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getTest8 method, of class TestClass.
//	 */
//	@Test
//	public void testGetTest8() {
//		System.out.println("getTest8");
//		TestClass instance = new TestClass();
//		String expResult = "";
//		String result = instance.getTest8();
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
}
