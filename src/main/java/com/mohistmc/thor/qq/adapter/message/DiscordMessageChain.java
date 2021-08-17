package com.mohistmc.thor.qq.adapter.message;

import java.util.ArrayList;
import java.util.Collection;

import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

// TODO: Auto-generated Javadoc
/**
 * Class DiscordMessageChain.
 *
 * @author khjxiaogu
 * file: DiscordMessageChain.java
 * @date 2021年8月18日
 */
public class DiscordMessageChain extends ArrayList<DiscordMessage> {

	/**
	 * Instantiates a new DiscordMessageChain.<br>
	 */
	public DiscordMessageChain() {
		super();
	}

	/**
	 * Instantiates a new DiscordMessageChain with another chain.<br>
	 *
	 * @param c the chain<br>
	 */
	public DiscordMessageChain(Collection<? extends DiscordMessage> c) {
		super(c);
	}

	/**
	 * Instantiates a new DiscordMessageChain with a length.<br>
	 *
	 * @param initialCapacity the initial capacity<br>
	 */
	public DiscordMessageChain(int initialCapacity) {
		super(initialCapacity);
	}
	
	/**
	 * Append message.<br>
	 *
	 * @param msg the message<br>
	 * @return returns self for chain
	 */
	public DiscordMessageChain append(DiscordMessage msg) {
		this.add(msg);
		return this;
	}
	
	/**
	 * Append text.<br>
	 *
	 * @param msg text message<br>
	 * @return returns self for chain
	 */
	public DiscordMessageChain append(String msg) {
		this.add(new Text(msg));
		return this;
	}
	
	/**
	 * Append link.<br>
	 *
	 * @param msg the link message<br>
	 * @return returns self for chain
	 */
	public DiscordMessageChain appendLink(String msg) {
		this.add(new Link(msg));
		return this;
	}
	
	/**
	 * Append Embed.<br>
	 *
	 * @param msg the Ember message<br>
	 * @return returns self for chain
	 */
	public DiscordMessageChain append(WebhookEmbedBuilder msg) {
		this.add(new Embed(msg));
		return this;
	}

	/**
	 * Ping.<br>
	 *
	 * @param member the member<br>
	 * @return returns ping
	 */
	public DiscordMessageChain ping(Member member) {
		this.add(new Ping(member));
		return this;
	}
	
	/**
	 * Ping.<br>
	 *
	 * @param id the id<br>
	 * @return returns ping
	 */
	public DiscordMessageChain ping(String id,String name) {
		this.add(new Ping(id,name));
		return this;
	}
}
