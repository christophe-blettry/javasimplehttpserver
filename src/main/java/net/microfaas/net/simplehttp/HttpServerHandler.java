/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author christophe
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		if (!req.decoderResult().isSuccess()) {
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(req.protocolVersion(), BAD_REQUEST, ctx.alloc().buffer(0)));
			return;
		}
		URI uri;
		try {
			uri = new URI(req.uri());
		} catch (URISyntaxException ex) {
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(req.protocolVersion(), BAD_REQUEST, ctx.alloc().buffer(0)));
			return;

		}
		String path = uri.getPath();
		HttpCallMapper call = HttpCallMapper.findByPath(path, getMethodFromRequest(req)).orElse(null);
		if (call == null) {
//			System.out.println(this.getClass().getName() + ".channelRead0: not found");
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.NOT_FOUND,
					ctx.alloc().buffer(0)));
			return;
		}

		HttpMethodInvokerResponse b = new HttpMethodInvoker().caller(uri, call, getHeaders(req), body(req.content())).orElse(null);
//		System.out.println(this.getClass().getName() + ".channelRead0: response is present " + b != null);
		if (b != null) {
			if (b.getException() == null) {
//				System.out.println(this.getClass().getName() + ".channelRead0: response length " + (b.getBytes() != null ? b.getBytes().length : 0));
//				System.out.println(this.getClass().getName() + ".channelRead0: HttpResponseStatus " + HttpResponseStatus.valueOf(b.code()));
				FullHttpResponse r = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.valueOf(b.getCode()),
						b.getBytes() != null ? Unpooled.wrappedBuffer(b.getBytes()) : ctx.alloc().buffer(0));
				if (b.getHeaders() != null && !b.getHeaders().isEmpty()) {
					b.getHeaders().forEach(e -> r.headers().add(e.getKey(), e.getValue()));
				}
				sendHttpResponse(ctx, req, r);
				return;
			}
			if (b.getException() instanceof SimpleHttpException) {
				SimpleHttpException ex = (SimpleHttpException) b.getException();
				ByteBuf reasonPhrase = ex.getReasonPhrase() != null ? Unpooled.wrappedBuffer(ex.getReasonPhrase().getBytes()) : ctx.alloc().buffer(0);
				HttpResponseStatus status = HttpResponseStatus.valueOf(ex.getStatus().code());
				sendHttpResponse(ctx, req, new DefaultFullHttpResponse(req.protocolVersion(), status, reasonPhrase));
				return;
			}
		}
		sendHttpResponse(ctx, req, new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.INTERNAL_SERVER_ERROR,
				ctx.alloc().buffer(0)));

	}

	private List<Entry<String, String>> getHeaders(FullHttpRequest req) {
		return req.headers().entries();
	}

	private String body(ByteBuf buf) {
		if (buf == null) {
			return null;
		}
		byte[] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);
		return new String(bytes);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//		System.out.println(this.getClass().getName() +".exceptionCaught: ex: " + cause.toString());
//		cause.printStackTrace();
		sendHttpResponse(ctx, null, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR,
				ctx.alloc().buffer(0)));
	}

	private HttpMethodEnum getMethodFromRequest(FullHttpRequest req) {
		HttpMethodEnum method = HttpMethodEnum.GET;
		switch (req.method().name().toUpperCase()) {
			case "GET":
				method = HttpMethodEnum.GET;
				break;
			case "POST":
				method = HttpMethodEnum.POST;
				break;
			case "PUT":
				method = HttpMethodEnum.PUT;
				break;
			case "DELETE":
				method = HttpMethodEnum.DELETE;
				break;
			case "HEAD":
				method = HttpMethodEnum.HEAD;
				break;
			case "TRACE":
				method = HttpMethodEnum.TRACE;
				break;
			case "OPTION":
				method = HttpMethodEnum.OPTION;
				break;
			case "PATCH":
				method = HttpMethodEnum.PATCH;
				break;
			case "CONNECT":
				method = HttpMethodEnum.CONNECT;
				break;
			default:
				break;
		}
		return method;
	}

	private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
		// Generate an error page if response getStatus code is not OK (200).
		HttpResponseStatus responseStatus = res.status();
		if (responseStatus.code() > 299 && res.content().writerIndex() == 0) {
			ByteBufUtil.writeUtf8(res.content(), responseStatus.toString());
		}
		HttpUtil.setContentLength(res, res.content().readableBytes());
		// Send the response and close the connection if necessary.
		boolean keepAlive = req != null && HttpUtil.isKeepAlive(req) && responseStatus.code() < 400;
		HttpUtil.setKeepAlive(res, keepAlive);
		ChannelFuture future = ctx.writeAndFlush(res);
		if (!keepAlive) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

}
