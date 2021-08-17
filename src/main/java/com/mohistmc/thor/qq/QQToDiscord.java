package com.mohistmc.thor.qq;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import static com.mohistmc.thor.MohistMC.groups;

import com.mohistmc.thor.qq.adapter.QQAdapter;
import com.mohistmc.thor.qq.adapter.message.DiscordMessage;
import com.mohistmc.thor.qq.adapter.message.DiscordMessageChain;
import com.mohistmc.thor.qq.adapter.message.Link;
import com.mohistmc.thor.qq.adapter.message.Ping;
import com.mohistmc.thor.qq.adapter.message.Text;

/**
 * @author Shawiiz_z khjxiaogu
 * @version 0.2
 * @date 18/08/2021 1:34
 */

public class QQToDiscord extends SimpleListenerHost {

	@EventHandler
	public void onMessage(GroupMessageEvent e) {
		if(!groups.containsKey(e.getGroup().getId())) return;
		
		GroupHandler group = groups.get(e.getGroup().getId());
		String username = e.getSenderName().length() == 0 ? e.getSender().getNick() : e.getSenderName();
		WebhookClient client = group.wbclient;
		DiscordMessageChain dmc=QQAdapter.convert(e.getMessage(),group);
		StringBuilder msg=new StringBuilder();//text message buffer
		StringBuilder translated=new StringBuilder();//translated message buffer
		for(DiscordMessage dm:dmc) {//send every single message to get best appearance XXXD
			if(dm.getClass()==Text.class) {//translate if and only if message is Text
				msg.append(((Text) dm).getContent());
				try {
					translated.append(Utils.translate(((Text) dm).getContent(),"en"));
				} catch (Exception e1) {//no translate would be added
				}
			}else if(dm.getClass()==Ping.class) {//well, do not translate ping
				msg.append(((Text) dm).getContent());
				translated.append(((Ping) dm).getHelpText());
			}else if(dm.getClass()==Link.class){//Web Image, Send separately
				WebhookMessageBuilder builder = dm.getDiscord();
				builder.setUsername(username);
				builder.setAvatarUrl(e.getSender().getAvatarUrl());
				client.send(builder.build());
			}else{//other complex message
				WebhookMessageBuilder builder = dm.getDiscord();
				if(msg.length()>0) {//send text buffer first
					if(translated.length()>0) {// if translation present.
						builder.setContent(msg.toString() + "\n<--->\n" + translated.toString() + (group.lastQQSpeaker.equals(username) ? "\n** **" : ""));
						translated=new StringBuilder();//clear buffer
					}else {
						builder.setContent(msg.toString());
					}
					msg=new StringBuilder();//clear buffer, anyway.
				}
				
				builder.setUsername(username);
				builder.setAvatarUrl(e.getSender().getAvatarUrl());
				client.send(builder.build());
			}
		}
		/*String msg = e.getMessage().contentToString().replaceAll("\\[图片\\]", "").replaceAll("\\[表情\\]", "").replace("@DiscordBot", "<@" + group.lastSpeaker + ">");
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
			return;
		}

		if(!e.getMessage().contentToString().equals("[图片]") && !e.getMessage().contentToString().equals("[表情]")) {
			try {
				String translation = translate(originalMsg, "en");
				if(translation.length() == 0 || translation.equalsIgnoreCase(msg)) throw new Exception();

				builder.setContent(msg + "\n<--->\n" + translation + (group.lastQQSpeaker.equals(username) ? "\n** **" : ""));
			} catch (Exception ex) { //Failed to translate or translation isn't needed, just send the original message
				builder.setContent(msg);
			}
			if(!builder.isEmpty()) client.send(builder.build()); //Send message content
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
					URLConnection conn = getConn(e.getBot().queryImageUrl((Image) m).replace("?term=2", ""));
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
		 */
		group.lastQQSpeaker = username;

		return;
	}
}
