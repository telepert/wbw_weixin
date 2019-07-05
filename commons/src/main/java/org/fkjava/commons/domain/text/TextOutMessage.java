package org.fkjava.commons.domain.text;

import org.fkjava.commons.domain.OutMessage;

public class TextOutMessage extends OutMessage {

	public static class TextContent {
		private String content;

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}

	private TextContent text;

	public TextOutMessage(String toUser, String text) {
		super();
		super.setToUser(toUser);
		super.setMessageType("text");
		this.text = new TextContent();
		this.text.content = text;
	}

	public TextContent getText() {
		return text;
	}

	public void setText(TextContent text) {
		this.text = text;
	}
}
