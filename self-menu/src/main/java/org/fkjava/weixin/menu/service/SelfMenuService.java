package org.fkjava.weixin.menu.service;

import org.fkjava.weixin.menu.domain.SelfMenu;

public interface SelfMenuService {
	SelfMenu findMenus();
	void save(SelfMenu menu);
}
