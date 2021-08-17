package com.mohistmc.thor.qq;

import club.minnced.discord.webhook.WebhookClient;
import net.dv8tion.jda.api.entities.TextChannel;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.mohistmc.thor.MohistMC.*;

/**
 * @author Shawiiz_z
 * @version 0.1
 * @date 01/07/2021 13:05
 */

public class GroupHandler {
	public Group group;
	public String lastSpeaker = "436832490263412736";
	public String lastQQSpeaker;
	public WebhookClient wbclient;
	public TextChannel tc;
	public HashMap<String, Member> members = new HashMap<>();
	public HashMap<String,net.dv8tion.jda.api.entities.Member> discordMembers=new HashMap<>();
	public List<String> autoTranslate = new ArrayList<>(Arrays.asList("436832490263412736", "361319428169662474")); //By default i add my id
	public List<String> authorDisplay = new ArrayList<>();
	public List<String> displayConfirm = new ArrayList<>();
	public Bot qqbot;
	public GroupHandler(String group, String discordChannelId, String webhookurl) {
		System.out.println("Initializing group number " + group);
		this.group = bot.getGroup(Long.parseLong(group));
		qqbot=bot;
		this.tc = jda.getTextChannelById(discordChannelId);
		if(this.tc.getTopic() == null || !this.tc.getTopic().equals(group)) this.tc.getManager().setTopic(group);
		refreshDiscordIndex();
		refreshQQIndex();
		System.out.println("Creating webhook client");
		this.wbclient = WebhookClient.withUrl(webhookurl);
		groups.put(Long.parseLong(group), this);
		new Thread(() ->{
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			refreshQQIndex();
		}).start();
		new Thread(() ->{
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			refreshDiscordIndex();
		}).start();
	}
	
	public void refreshDiscordIndex() {
		System.out.println("Indexing Discord group members...");
		for(net.dv8tion.jda.api.entities.Member member:tc.getMembers()) {
			this.discordMembers.put(member.getEffectiveName(),member);
		}
		System.out.println("Successfully indexed Discord group members!");
	}
	public void refreshQQIndex() {
		System.out.println("Indexing QQ group members...");
		for (Member member : this.group.getMembers()) {
			String name = member.getNameCard().length() == 0 ? member.getNick() : member.getNameCard();
			if(name.length() == 0) continue;
			this.members.put(name, member);
		}
		System.out.println("Successfully indexed QQ group members!");
	}
}
