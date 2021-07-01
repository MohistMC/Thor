package com.mohistmc.thor.qq;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Voice;

import java.io.IOException;
import java.util.ArrayList;

import static com.mohistmc.thor.MohistMC.groups;
import static com.mohistmc.thor.qq.Utils.getInput;
import static com.mohistmc.thor.qq.Utils.translate;

public class QQToDiscord extends SimpleListenerHost {

	@EventHandler
	public ListeningStatus onMessage(GroupMessageEvent e) {
		if(!groups.containsKey(e.getGroup().getId())) return ListeningStatus.LISTENING;

		GroupHandler group = groups.get(e.getGroup().getId());
		WebhookClient client = group.wbclient;
		WebhookMessageBuilder builder = new WebhookMessageBuilder();
		builder.setUsername(e.getSenderName().length() == 0 ? e.getSender().getNick() : e.getSenderName());
		builder.setAvatarUrl(e.getSender().getAvatarUrl());

		String msg = e.getMessage().contentToString().replaceAll("\\[图片\\]", "").replaceAll("\\[表情\\]", "").replace("@DiscordBot", "<@" + group.lastSpeaker + ">");
		if(msg.length() == 0) return ListeningStatus.LISTENING;

		//Ping corresponding Discord users
		ArrayList<String> pinged = new ArrayList<>();
		for (String word : msg.split(" "))
			if(word.startsWith("@"))
				pinged.add(word.replaceFirst("@", ""));

		for (Member member : group.tc.getMembers()) {
			String name = member.getNickname() == null ? member.getEffectiveName() : member.getNickname();
			if(pinged.contains(name))
				msg = msg.replaceAll("@" + name, "<@" + member.getId() + ">");
		}

		//Try to convert xml sent by bots into an embed :D
		if(msg.startsWith("<?xml") || msg.startsWith("<msg serviceID=\"2\"")) {
			WebhookEmbedBuilder webhookEmbedBuilder = new WebhookEmbedBuilder();
			webhookEmbedBuilder.setThumbnailUrl(msg.split("audio cover=\"")[1].split("\"")[0]);
			webhookEmbedBuilder.setDescription("\nTitle : [" + msg.split("<title>")[1].split("</title>")[0] + "](" + msg.split("</msg>")[1] + ")" +
					"\n" + msg.split("<summary>")[1].split("</summary>")[0]);
			builder.addEmbeds(webhookEmbedBuilder.build());
			client.send(builder.build());
			return ListeningStatus.LISTENING;
		}

		if(!e.getMessage().contentToString().equals("[图片]") && !e.getMessage().contentToString().equals("[表情]")) {
			try {
				String translation = translate(msg, "en");
				if(translation.length() == 0) throw new Exception();

				builder.setContent(msg + "\n--------\n" + translation);
			} catch (Exception ex) { //Failed to translate or translation isn't needed, just send the original message
				builder.setContent(msg);
			}
		}

		for (net.mamoe.mirai.message.data.Message m : e.getMessage()) {
			if(m instanceof Face)
				builder.setContent("https://raw.githubusercontent.com/khjxiaogu/DefaultQQEmoticon/master/emoji/" + ((Face) m).getId() + ".gif");
			if(m instanceof Voice)
				try {
					builder.addFile(((Voice) m).getFileName(), getInput(((Voice) m).getUrl()));
				} catch (IOException ignored) {
				}
			if(m instanceof Image)
				builder.setContent(e.getBot().queryImageUrl((Image) m).replace("?term=2", ""));
		}

		if(!builder.isEmpty()) client.send(builder.build());
		return ListeningStatus.LISTENING;
	}
}
