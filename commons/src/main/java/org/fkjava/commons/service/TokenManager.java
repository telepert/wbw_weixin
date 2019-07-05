package org.fkjava.commons.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.fkjava.commons.domain.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service // 把对象放入Spring容器里面
public class TokenManager {

	private static final Logger LOG = LoggerFactory.getLogger(TokenManager.class);

	@Autowired
	private RedisTemplate<String, AccessToken> accessTokenTemplate;

	public String getToken() {
		BoundValueOperations<String, AccessToken> ops = accessTokenTemplate.boundValueOps("weixin_access_token");
		AccessToken at = ops.get();
		if (at == null) {
			
			for (int i = 0; i < 10; i++) {
				LOG.trace("缓存中没有令牌，尝试加上分布式锁");
				Boolean result = accessTokenTemplate.boundValueOps("weixin_access_token_lock")//
						.setIfAbsent(new AccessToken());
				LOG.trace("增加分布式锁的结果：{}", result);
				if (result == true) {
				try {
				at = ops.get();
				if (at == null) {
				LOG.trace("重新获取缓存的令牌，也没有在本地获取到，尝试获取远程令牌");
				at = getRemoteToken();
				ops.set(at);
				ops.expire(at.getExpiresIn() - 60, TimeUnit.SECONDS);
				} else {
				LOG.trace("本次重试正常获得令牌: {}", at.getAccessToken());
				}
				break;
			    } finally {
				LOG.trace("删除分布式锁");
				accessTokenTemplate.delete("weixin_access_token_lock");
				synchronized (TokenManager.class) {
				TokenManager.class.notifyAll();
				}
				}
				} else {
				synchronized (TokenManager.class) {
				try {
				LOG.trace("其他线程锁定了数据，等待通知。如果没有通知则1分后重试");
				TokenManager.class.wait(1000 * 60);
				} catch (InterruptedException e) {
				LOG.error("无法等待分布式事务锁的通知：" + e.getLocalizedMessage(), e);
				}
				}
				}
			    }
		        }

		        return at.getAccessToken();
	}

	// 获取远程令牌
	public AccessToken getRemoteToken() {
		String appId = "wx375cd9c53c36te34";
		String appSecret = "ad91eb3762d1336c39a417173be85eba";
		String url = "https://api.weixin.qq.com/cgi-bin/token"//
				+ "?grant_type=client_credential"//
				+ "&appid=" + appId//
				+ "&secret=" + appSecret;
		HttpClient client = HttpClient.newBuilder()//
				.version(Version.HTTP_1_1)// 设置HTTP 1.1的协议版本
				.build();
		HttpRequest request = HttpRequest.newBuilder(URI.create(url))//
				.GET()// 以GET方式发送请求
				.build();
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString(Charset.forName("UTF-8")));
			String json = response.body();
			LOG.trace("获取令牌的返回：\n{}", json);
			if (json.indexOf("errcode") > 0) {
				throw new RuntimeException("获取令牌出现问题：" + json);
			}
			ObjectMapper mapper = new ObjectMapper();
			AccessToken at = mapper.readValue(json, AccessToken.class);
			return at;
		} catch (Exception e) {
			throw new RuntimeException("获取令牌出现问题：" + e.getLocalizedMessage(), e);
		}
	}
}
