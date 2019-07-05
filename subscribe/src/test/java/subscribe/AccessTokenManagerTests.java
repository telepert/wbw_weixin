package subscribe;

import org.fkjava.commons.service.TokenManager;
import org.fkjava.weixin.SubscribeApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = { SubscribeApplication.class })
public class AccessTokenManagerTests {

	@Autowired
	private TokenManager manager;

	@Test
	public void test() throws InterruptedException {
		Runnable task = () -> {
			String token = manager.getToken();
			System.out.println("令牌：" + token);
		};

		Thread t1 = new Thread(task);
		Thread t2 = new Thread(task);

		t1.start();
		t2.start();

		// 为了保证整个测试能够完整，必须等待两个子线程完成以后才能结束程序
		t1.join();
		t2.join();
	}
}
