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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.mohistmc.thor.MohistMC.groups;
import static com.mohistmc.thor.qq.Utils.getInput;
import static com.mohistmc.thor.qq.Utils.translate;

/**
 * @author 	Shawiiz_z
 * @date  	01/07/2021 13:05
 * @version 0.1
 */

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
			return ListeningStatus.LISTENING;
		}

		if(!e.getMessage().contentToString().equals("[图片]") && !e.getMessage().contentToString().equals("[表情]")) {
			try {
				String translation = translate(originalMsg, "en");
				if(translation.length() == 0 || translation.equals(msg)) throw new Exception();

				builder.setContent(msg + "\n--------\n" + translation);
			} catch (Exception ex) { //Failed to translate or translation isn't needed, just send the original message
				builder.setContent(msg);
			}
			if(!builder.isEmpty()) client.send(builder.build()); //Send message content
		}

		//Send emojis, voices and images
		for (net.mamoe.mirai.message.data.Message m : e.getMessage()) {
			if(m instanceof Face) {
				builder.setContent("https://raw.githubusercontent.com/khjxiaogu/DefaultQQEmoticon/master/emoji/" + ((Face) m).getId() + ".gif");
				client.send(builder.build());
			}
			if(m instanceof Voice) {
				try {
					builder.addFile(((Voice) m).getFileName(), getInput(((Voice) m).getUrl()));
					client.send(builder.build());
				} catch (IOException ignored) {
				}
			}
			if(m instanceof Image) {
				try {
					BufferedImage bufferedImage = ImageIO.read(new URL(e.getBot().queryImageUrl((Image) m).replace("?term=2", "")));
					ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
					ImageIO.write(bufferedImage, "png", byteArrayOut);
					byte[] resultingBytes = byteArrayOut.toByteArray();
					builder.addFile("qqimage.png", resultingBytes);
				} catch (IOException ioException) {
					builder.setContent("Failed to upload an image.");
				}
				client.send(builder.build());
			}
		}

		return ListeningStatus.LISTENING;
	}
}
