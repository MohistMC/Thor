package com.mohistmc.thor.qq.adapter.message;

import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;

// TODO: Auto-generated Javadoc
/**
 * Class Embed.
 *
 * @author khjxiaogu
 * file: Embed.java
 * @date 2021年8月18日
 */
public class Embed implements DiscordMessage {
	
	/** The embed.<br> */
	WebhookEmbedBuilder embed;
	
	/**
	 * Get embed.
	 *
	 * @return embed<br>
	 */
	public WebhookEmbedBuilder getEmbed() {
		return embed;
	}
	
	/**
	 * set embed.
	 *
	 * @param embed value to set embed to.
	 */
	public void setEmbed(WebhookEmbedBuilder embed) {
		this.embed = embed;
	}
	
	/**
	 * Instantiates a new Embed with a WebhookEmbedBuilder object.<br>
	 *
	 * @param embed the embed<br>
	 */
	public Embed(WebhookEmbedBuilder embed) {
		this.embed = embed;
	}
	
	/**
	 * Write builder.
	 *
	 * @param builder the builder<br>
	 */
	@Override
	public void writeBuilder(WebhookMessageBuilder builder) {
		builder.addEmbeds(embed.build());
	}

}
