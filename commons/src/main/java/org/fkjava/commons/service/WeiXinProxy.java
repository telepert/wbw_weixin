package org.fkjava.commons.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

import org.fkjava.commons.domain.OutMessage;
import org.fkjava.commons.domain.User;
import org.fkjava.commons.domain.text.TextOutMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
@Service
public class WeiXinProxy {

	private static final Logger LOG = LoggerFactory.getLogger(WeiXinProxy.class);
	@Autowired
	private TokenManager tokenManager;

	private HttpClient client = HttpClient.newBuilder()//
			.version(Version.HTTP_1_1)// 设置HTTP 1.1的协议版本
			.build();
	public User getWxUser(String openId) {
		// 获取一个令牌
		String accessToken = this.tokenManager.getToken();

		String url = "https://api.weixin.qq.com/cgi-bin/user/info"//
				+ "?access_token=" + accessToken//
				+ "&openid=" + openId//
				+ "&lang=zh_CN";
		HttpRequest request = HttpRequest.newBuilder(URI.create(url))//
				.GET()
				.build();
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString(Charset.forName("UTF-8")));
			String json = response.body();// 响应体

			LOG.trace("获取用户信息的返回：\n{}", json);

			if (json.indexOf("errcode") > 0) {
	
				throw new RuntimeException("获取用户信息出现问题：" + json);
			}
			ObjectMapper mapper = new ObjectMapper();
			User user = mapper.readValue(json, User.class);

			return user;
		} catch (Exception e) {

			throw new RuntimeException("获取令牌出现问题：" + e.getLocalizedMessage(), e);
		}
	}

	public void sendText(String openId, String string) {

		TextOutMessage text = new TextOutMessage(openId, string);
		
		send(text);
	}

	private void send(OutMessage msg) {
//		ObjectMapper mapper = new ObjectMapper();
//		try {
//			String json = mapper.writeValueAsString(msg);
//			LOG.trace("发送信息信息的内容：\n{}", json);
//
//			String accessToken = this.tokenManager.getToken();
//			String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken;
//			HttpRequest request = HttpRequest.newBuilder(URI.create(url))//
//					// 把消息的内容，转换为JSON字符串，然后发送给微信
//					.POST(BodyPublishers.ofString(json, Charset.forName("UTF-8")))// 以POST方式发送请求
//					.build();
////			HttpResponse<String> response = client.send(request, BodyHandlers.ofString(Charset.forName("UTF-8")));
////			LOG.trace("发送信息信息的返回：\n{}", response.body());
//
//			// 异步发送请求
//			CompletableFuture<HttpResponse<String>> future = client.sendAsync(request,
//					BodyHandlers.ofString(Charset.forName("UTF-8")));
//			// 异步处理结果
//			future.thenAcceptAsync(response -> {
//				LOG.trace("发送信息信息的返回：\n{}", response.body());
//			});
//		} catch (IOException e) {
//			throw new RuntimeException("发送消息出现问题：" + e.getLocalizedMessage(), e);
//		}

		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(msg);

			String accessToken = this.tokenManager.getToken();
			String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken;

			post(url, json);
		} catch (Exception e) {
			throw new RuntimeException("发送消息出现问题：" + e.getLocalizedMessage(), e);
		}
	}

	public void saveMenu(String json) {
		String accessToken = this.tokenManager.getToken();
		String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessToken;

		post(url, json);
	}
	private void post(String url, String json) {
		try {
			LOG.trace("POST的URL地址：\n{}", url);
			LOG.trace("POST发送到微信公众号里面的信息：\n{}", json);
			HttpRequest request = HttpRequest.newBuilder(URI.create(url))//
					// 把消息的内容，转换为JSON字符串，然后发送给微信
					.POST(BodyPublishers.ofString(json, Charset.forName("UTF-8")))// 以POST方式发送请求
					.build();
			CompletableFuture<HttpResponse<String>> future = client.sendAsync(request,
					BodyHandlers.ofString(Charset.forName("UTF-8")));
			future.thenAcceptAsync(response -> {
				LOG.trace("POST发送以后的返回：\n{}", response.body());
			});
		} catch (Exception e) {
			throw new RuntimeException("POST发送信息给微信公众号出现问题：" + e.getLocalizedMessage(), e);
		}
	}
}
