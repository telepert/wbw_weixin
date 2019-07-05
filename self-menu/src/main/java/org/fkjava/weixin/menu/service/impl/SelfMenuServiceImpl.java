package org.fkjava.weixin.menu.service.impl;

import java.util.List;

import org.fkjava.commons.service.WeiXinProxy;
import org.fkjava.weixin.menu.domain.Menu;
import org.fkjava.weixin.menu.domain.SelfMenu;
import org.fkjava.weixin.menu.repository.SelfMenuRepository;
import org.fkjava.weixin.menu.service.SelfMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class SelfMenuServiceImpl implements SelfMenuService {

	private static final Logger LOG = LoggerFactory.getLogger(SelfMenuServiceImpl.class);
	@Autowired
	private SelfMenuRepository selfMenuRepository;
	@Autowired
	private WeiXinProxy weiXinProxy;

	@Override
	public SelfMenu findMenus() {
		List<SelfMenu> menus = selfMenuRepository.findAll();
		if (menus.isEmpty()) {
			return new SelfMenu();
		}
		return menus.get(0);
	}

	@Override
	public void save(SelfMenu menu) {
		this.selfMenuRepository.deleteAll();
		this.selfMenuRepository.save(menu);

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode selfMenuNode = mapper.createObjectNode();
		ArrayNode buttonNode = mapper.createArrayNode();
		selfMenuNode.set("button", buttonNode);

		menu.getSubMenus().forEach(m -> {
	    ObjectNode menuNode = mapper.createObjectNode();
		buttonNode.add(menuNode);
		menuNode.put("name", m.getName());
		if (m.getSubMenus() == null || m.getSubMenus().isEmpty()) {
				this.setVauels(menuNode, m);
			} else {
			ArrayNode subButtons = mapper.createArrayNode();
			menuNode.set("sub_button", subButtons);
			m.getSubMenus().forEach(sm -> {
			ObjectNode subMenuNode = mapper.createObjectNode();
		    this.setVauels(subMenuNode, sm);
			subButtons.add(subMenuNode);
			});
			}
		});

		try {
			String json = mapper.writeValueAsString(selfMenuNode);
			this.weiXinProxy.saveMenu(json);
		} catch (JsonProcessingException e) {
			LOG.error("转换菜单的时候出现问题：" + e.getLocalizedMessage(), e);
		}
	}

	private void setVauels(ObjectNode menuNode, Menu m) {
		menuNode.put("name", m.getName());
		if (!m.getSubMenus().isEmpty()) {
			return;
		}
		if ((m.getType().equals("click")
				|| m.getType().equals("scancode_waitmsg") 
				|| m.getType().equals("scancode_push")
				|| m.getType().equals("pic_sysphoto") 
				|| m.getType().equals("pic_photo_or_album") 
				|| m.getType().equals("pic_weixin")
				|| m.getType().equals("location_select")
		) 
				&& !StringUtils.isEmpty(m.getKey())) {
			menuNode.put("key", m.getKey());
		}
		if (!StringUtils.isEmpty(m.getAppId())) {
			menuNode.put("appid", m.getAppId());
		}
		if (!StringUtils.isEmpty(m.getMediaId())) {
			menuNode.put("media_id", m.getMediaId());
		}
		if (!StringUtils.isEmpty(m.getPagePath())) {
			menuNode.put("pagepath", m.getPagePath());
		}
		if (!StringUtils.isEmpty(m.getType())) {
			menuNode.put("type", m.getType());
		}
		if (!StringUtils.isEmpty(m.getUrl())) {
			menuNode.put("url", m.getUrl());
		}
	}
}
