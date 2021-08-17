package com.mohistmc.thor.qq.adapter.message;

import java.io.InputStream;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;

// TODO: Auto-generated Javadoc
/**
 * Class FileMessage.
 *
 * @author khjxiaogu
 * file: FileMessage.java
 * @date 2021年8月18日
 */
public class FileMessage implements DiscordMessage{
	
	/** The stream.<br> */
	InputStream stream;
	
	/** The name.<br> */
	String name;
	
	/**
	 * Instantiates a new FileMessage.<br>
	 *
	 * @param stream the stream<br>
	 * @param name the name<br>
	 */
	public FileMessage(InputStream stream, String name) {
		this.stream = stream;
		this.name = name;
	}
	
	/**
	 * Write builder.
	 *
	 * @param builder the builder<br>
	 */
	@Override
	public void writeBuilder(WebhookMessageBuilder builder) {
		builder.addFile(name,stream);
	}
}