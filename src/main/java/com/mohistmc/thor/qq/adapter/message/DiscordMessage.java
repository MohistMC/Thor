package com.mohistmc.thor.qq.adapter.message;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;

// TODO: Auto-generated Javadoc
/**
 * Interface DiscordMessage.
 *
 * @author khjxiaogu
 * file: DiscordMessage.java
 * @date 2021年8月18日
 */
public interface DiscordMessage {
	
	/**
	 * Write to builder.
	 *
	 * @param builder the builder<br>
	 */
	void writeBuilder(WebhookMessageBuilder builder);
	
	/**
	 * Get discord message.
	 *
	 * @return discord message builder<br>
	 */
	default WebhookMessageBuilder getDiscord() {
		WebhookMessageBuilder builder=new WebhookMessageBuilder();
		writeBuilder(builder);
		return builder;
	}
	
}
