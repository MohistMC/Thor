package com.mohistmc.thor.qq.adapter.message;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;

// TODO: Auto-generated Javadoc
/**
 * Class Text.
 * Represent normal text message.
 * @author khjxiaogu
 * file: Text.java
 * @date 2021年8月18日
 */
public class Text implements DiscordMessage {
	
	/** The content.<br> */
	private String content;

	/**
	 * Get content.
	 *
	 * @return content<br>
	 */
	public String getContent() {
		return content;
	}

	/**
	 * set content.
	 *
	 * @param content value to set content to.
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Instantiates a new Text with a String object.<br>
	 *
	 * @param content the content<br>
	 */
	public Text(String content) {
		this.content = content;
	}

	/**
	 * Write to builder.
	 *
	 * @param builder the builder<br>
	 */
	@Override
	public void writeBuilder(WebhookMessageBuilder builder) {
		builder.append(content);
	}
}
