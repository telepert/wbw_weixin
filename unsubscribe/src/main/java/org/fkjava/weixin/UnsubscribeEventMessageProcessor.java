package org.fkjava.weixin;

import javax.transaction.Transactional;

import org.fkjava.commons.domain.User;
import org.fkjava.commons.domain.event.EventInMessage;
import org.fkjava.commons.processors.EventMessageProcessor;
import org.fkjava.commons.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// 把对象加入Spring容器，并且根据事件处理查找Bean的规则给一个名字
@Service("unsubscribeMessageProcessor")
public class UnsubscribeEventMessageProcessor implements EventMessageProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(UnsubscribeEventMessageProcessor.class);

	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional // 要有事务支持，在事务结束后，默认自动提交数据的修改
	public void onMessage(EventInMessage event) {

		if (!event.getEvent().equals("unsubscribe")) {
			// 只处理关注事件
			return;
		}
		LOG.trace("取消关注事件处理器被调用，收到的消息:\n {} ", event);

		User user = this.userRepository.findByOpenId(event.getFromUserName());
		user.setStatus(User.Status.IS_UNSUBSCRIBED);
	}
}
