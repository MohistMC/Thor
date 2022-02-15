package com.mohistmc.thor.qq;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.mohistmc.thor.MohistMC;
import net.dv8tion.jda.api.entities.Member;
import net.mamoe.mirai.Mirai;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Voice;

import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;

import static com.mohistmc.thor.MohistMC.groups;
import static com.mohistmc.thor.qq.Utils.*;

/**
 * @author Shawiiz_z
 * @version 0.1
 * @date 01/07/2021 13:05
 */

public class QQToDiscord extends SimpleListenerHost {

	@EventHandler
	public ListeningStatus onMessage(GroupMessageEvent e) {
		if(!groups.containsKey(e.getGroup().getId())) return ListeningStatus.LISTENING;

		GroupHandler group = groups.get(e.getGroup().getId());
		String username = e.getSenderName().length() == 0 ? e.getSender().getNick() : e.getSenderName();
		WebhookClient client = group.wbclient;
		WebhookMessageBuilder builder = new WebhookMessageBuilder();
		builder.setUsername(username);
		builder.setAvatarUrl(e.getSender().getAvatarUrl());

		String msg = e.getMessage().contentToString().replaceAll("\\[图片\\]", "").replaceAll("\\[表情\\]", "").replace("@DiscordBot", "<@" + group.lastSpeaker + ">");
		String originalMsg = msg; //Fix weird user id translation and put name instead.

		//Ping corresponding Discord users
		ArrayList<String> pinged = new ArrayList<>();
		for (String word : msg.split(" "))
			if(word.startsWith("@"))
				pinged.add(word.replaceFirst("@", ""));

		for (Member member : group.tc.getMembers()) {
			if(member.getNickname() != null && pinged.contains(member.getNickname()))
				msg = msg.replaceAll("@" + member.getNickname(), "<@" + member.getId() + ">");
			if(pinged.contains(member.getEffectiveName()))
				msg = msg.replaceAll("@" + member.getEffectiveName(), "<@" + member.getId() + ">");
		}

		//Try to convert xml sent by bots into an embed :D
		if(msg.startsWith("<?xml") || msg.startsWith("<msg serviceID=\"2\"")) {
			WebhookEmbedBuilder webhookEmbedBuilder = new WebhookEmbedBuilder();
			webhookEmbedBuilder.setThumbnailUrl(msg.split("audio cover=\"")[1].split("\"")[0]);
			webhookEmbedBuilder.setDescription("\nTitle : [" + msg.split("<title>")[1].split("</title>")[0] + "](" + msg.split("</msg>")[1] + ")" +
					"\n" + msg.split("<summary>")[1].split("</summary>")[0]);
			builder.addEmbeds(webhookEmbedBuilder.build());
			client.send(builder.build());
			builder.resetEmbeds();
			return ListeningStatus.LISTENING;
		}

		if(!e.getMessage().contentToString().equals("[图片]") && !e.getMessage().contentToString().equals("[表情]")) {
			try {
				String translation = translate(originalMsg, "en");
				if(translation.length() == 0 || translation.equalsIgnoreCase(msg)) throw new Exception();

				builder.setContent(msg + "\n<--->\n" + translation + (group.lastQQSpeaker.equals(username) ? "\n** **" : ""));
			} catch (Exception ex) { //Failed to translate or translation isn't needed, just send the original message
				builder.setContent(msg);
			}
			if(!builder.isEmpty())
				client.send(builder.build()); //Send message content
			builder.setContent("");
		}

		//Send emojis, voices and images
		for (net.mamoe.mirai.message.data.Message m : e.getMessage()) {
			if(m instanceof Face) {
				builder.setContent("https://raw.githubusercontent.com/khjxiaogu/DefaultQQEmoticon/master/emoji/" + ((Face) m).getId() + ".gif");
				client.send(builder.build());
				builder.setContent("");
			}
			if(m instanceof Voice) {
				try {
					builder.addFile(((Voice) m).getFileName(), getInput(((Voice) m).getUrl()));
					client.send(builder.build());
					builder.setContent("");
					builder.resetFiles();
				} catch (IOException ignored) {
				}
			}
			if(m instanceof Image) {
				try {
					URLConnection conn = getConn(Mirai.getInstance().queryImageUrl(MohistMC.bot, (Image) m).replace("?term=2", ""));

					if(conn.getHeaderField("Content-Type").contains("gif"))
						builder.addFile("qqimage.gif", conn.getInputStream());
					else
						builder.addFile("qqimage.png", conn.getInputStream());
				} catch (IOException ioException) {
					builder.setContent("Failed to upload an image.");
				}
				client.send(builder.build());
			}
		}

		group.lastQQSpeaker = username;

		return ListeningStatus.LISTENING;
	}
}
