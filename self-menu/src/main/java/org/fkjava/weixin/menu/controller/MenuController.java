package org.fkjava.weixin.menu.controller;

import org.fkjava.weixin.menu.domain.SelfMenu;
import org.fkjava.weixin.menu.service.SelfMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/wbw_self_menu")
public class MenuController {

	@Autowired
	private SelfMenuService selfMenuService;

	@GetMapping
	public ModelAndView index() {
		return new ModelAndView("/WEB-INF/views/self-menu/index.jsp");
	}
	@GetMapping(produces = "application/json")
	@ResponseBody
	public SelfMenu data() {
		return selfMenuService.findMenus();
	}

	@PostMapping(produces = "application/json")
	@ResponseBody
	public String save(@RequestBody SelfMenu menu) {
		this.selfMenuService.save(menu);
		return "保存成功";
	}
}
