package org.fkjava.commons.processors;

import org.fkjava.commons.domain.event.EventInMessage;

public interface EventMessageProcessor {

	void onMessage(EventInMessage event);

}
