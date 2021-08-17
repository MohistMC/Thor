package com.mohistmc.thor.qq.adapter.message;

import net.dv8tion.jda.api.entities.Member;

// TODO: Auto-generated Javadoc
/**
 * Class Ping.
 * Repersent ping
 * @author khjxiaogu
 * file: Ping.java
 * @date 2021年8月18日
 */
public class Ping extends Text {
	
	/** The human readable name.<br> */
	String HelpText;
	
	public String getHelpText() {
		return HelpText;
	}
	public void setHelpText(String helpText) {
		HelpText = helpText;
	}
	/**
	 * Instantiates a new Ping with a ping id.<br>
	 *
	 * @param id the id<br>
	 * @param name human readable name<br>
	 */
	public Ping(String id,String name) {
		super("<@"+id+">");
		HelpText="@"+name+" ";
	}
	/**
	 * Instantiates a new Ping with member.<br>
	 *
	 * @param member the member<br>
	 */
	public Ping(Member member) {
		super("<@"+member.getId()+">");
		HelpText="@"+member.getEffectiveName()+" ";
	}
}
