/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.lang.reflect.Method;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;
import net.microfaas.net.simplehttp.util.ClasspathInspector;

/**
 *
 * @author christophe
 */
public class HttpServer implements Runnable {

	private final int port;
	private final String host;
	private boolean ssl = false;
	private boolean selfSigned = false;
	private final Set<String> searchClassForAnnotations = new HashSet<>();
	private Thread currentThread;
	private CompletableFuture<Boolean> threadStarted = new CompletableFuture<>();

	public HttpServer() {
		this.port = 80;
		this.host = "localhost";
	}

	public HttpServer(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public HttpServer(int port) {
		this.port = port;
		this.host = "localhost";
	}

	public void setSearchClassForAnnotations(Set<String> searchClassForAnnotations) {
		this.searchClassForAnnotations.clear();
		this.searchClassForAnnotations.addAll(searchClassForAnnotations);
	}

	public void addSearchClassForAnnotations(String searchClassForAnnotations) {
		this.searchClassForAnnotations.add(searchClassForAnnotations);
	}

	public int getPort() {
		return port;
	}

	public String getHost() {
		return host;
	}

	public boolean isSsl() {
		return ssl;
	}

	public HttpServer setSsl(boolean ssl) {
		this.ssl = ssl;
		return this;
	}

	public HttpServer setSelfSignedSsl(boolean ssl) {
		this.ssl = ssl;
		this.selfSigned = true;
		return this;
	}

	private void parseClasses() {
		List<Class<?>> allClasses = ClasspathInspector.getMatchingClasses(searchClassForAnnotations);
		allClasses.forEach(cl -> {
			Method[] methods = cl.getDeclaredMethods();
			if (methods != null) {
				Arrays.asList(methods).forEach(m -> {
					if (m.isAnnotationPresent(RequestMapping.class)) {
						new HttpCallMapper(((RequestMapping) m.getAnnotation(RequestMapping.class)), cl, m);
					}
				});
			}
		});
	}

	private void start() throws CertificateException, SSLException {
		parseClasses();
		final SslContext sslCtx;
		if (ssl && this.selfSigned) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
		} else {
			sslCtx = null;
		}

		// Configure the server.
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					//             .handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new HttpServerInitializer(sslCtx));

			Channel ch = b.bind(host, port).sync().channel();
			System.err.println("Open your web browser and navigate to "
					+ (ssl ? "https" : "http") + "://" + host + ":" + port + '/');
			this.threadStarted.complete(true);
			ch.closeFuture().sync();
		} catch (InterruptedException ex) {
			this.threadStarted.completeExceptionally(ex);
//			System.out.println("interrupted " + ex.toString());
			Thread.currentThread().interrupt();
		} finally {
			this.threadStarted.complete(true);
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public String getBaseUrl() {
		return (ssl ? "https" : "http") + "://" + host + ":" + port;
	}

	public CompletableFuture<Boolean> getThreadStarted() {
		return threadStarted;
	}

	public void shutdown() {
//		System.out.println(new Date().toInstant() + " shutdown required");
		currentThread.interrupt();
	}

	@Override
	public void run() {
		currentThread = Thread.currentThread();
		try {
			start();
		} catch (CertificateException | SSLException ex) {
			Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
