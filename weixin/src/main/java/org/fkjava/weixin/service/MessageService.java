package org.fkjava.weixin.service;

import org.fkjava.commons.domain.InMessage;
import org.fkjava.commons.domain.OutMessage;

public interface MessageService {
	OutMessage onMessage(InMessage msg);
}
