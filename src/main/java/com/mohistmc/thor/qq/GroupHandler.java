package com.mohistmc.thor.qq;

import club.minnced.discord.webhook.WebhookClient;
import net.dv8tion.jda.api.entities.TextChannel;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.mohistmc.thor.MohistMC.*;

public class GroupHandler {
	Group group;
	String lastSpeaker = "436832490263412736";
	WebhookClient wbclient;
	TextChannel tc;
	HashMap<String, Member> members = new HashMap<>();
	List<String> autoTranslate = new ArrayList<>(Arrays.asList("436832490263412736", "361319428169662474")); //By default i add my id
	List<String> authorDisplay = new ArrayList<>();
	List<String> displayConfirm = new ArrayList<>();

	public GroupHandler(String group, String discordChannelId, String webhookurl) {
		System.out.println("Initializing group number " + group);
		this.group = bot.getGroup(Long.parseLong(group));
		this.tc = jda.getTextChannelById(discordChannelId);
		if(this.tc.getTopic() == null || !this.tc.getTopic().equals(group)) this.tc.getManager().setTopic(group);

		System.out.println("Indexing QQ group members...");
		for (Member member : this.group.getMembers()) {
			String name = member.getNameCard().length() == 0 ? member.getNick() : member.getNameCard();
			if(name.length() == 0) continue;
			this.members.put(name, member);
		}
		System.out.println("Successfully indexed QQ group members!");

		System.out.println("Creating webhook client");
		this.wbclient = WebhookClient.withUrl(webhookurl);
		groups.put(Long.parseLong(group), this);
	}
}
