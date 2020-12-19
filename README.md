# javasimplehttpserver
simple and lightweight java http server  

## How to use

### compile the project with maven  
add result to your project

```
	<dependency>
    	<groupId>net.microfaas</groupId>
    	<artifactId>net-simplehttp</artifactId>
    	<version>0.3</version>
	</dependency>
```

### developp some classes with api mapping

````java
package net.microfaas.net.simplehttp.example;

public class ApiExample {

	public static final String HELLO_W = "Hello World!   ";
    
	@RequestMapping("/hello")
	public String getHello() {
		log(ApiExample.class.getName() + ".getHello");
		return HELLO_W + new Date().toString();
	}

	@RequestMapping(value = "/host", success = 204)
	public void getHeaderHost(@RequestHeader("host") String h) {
		log(ApiExample.class.getName() + ".getHeaderHost: " + h);
	}
}
````

### start a server with api classes location
New server
````java
	HttpServer server = new HttpServer();
	Thread t = new Thread(server);
	t.start();    
````
Indicate location (befor starting thread)
````java
	server.setSearchClassForAnnotations(Collections.singleton("net.microfaas.net.simplehttp.example"));
````

[test example]
````java
public class TestClassTest {

	static HttpServer server = new HttpServer();

	public TestClassTest() {
	}

	@BeforeClass
	public static void setUpClass() throws InterruptedException, ExecutionException, TimeoutException {
		server.setSearchClassForAnnotations(Collections.singleton("net.microfaas.net.simplehttp.example"));
		Thread t = new Thread(server);
		t.start();
		server.getThreadStarted().get(10, TimeUnit.MINUTES);
	}
}
````

Example of an "api class" [here] (https://github.com/christophe-blettry/javasimplehttpserver/blob/main/src/main/java/net/microfaas/net/simplehttp/example/ApiExample.java)

[test example]: https://github.com/christophe-blettry/javasimplehttpserver/blob/main/src/test/java/net/microfaas/net/simplehttp/main/TestClassTest.java