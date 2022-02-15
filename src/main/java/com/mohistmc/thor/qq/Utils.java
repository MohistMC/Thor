package com.mohistmc.thor.qq;

import fr.shawiizz.ShaGoogleTranslate;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Shawiiz_z
 * @version 0.1
 * @date 01/07/2021 13:05
 */

public class Utils {

	public static MessageChainBuilder makeMessage(GroupHandler handler, Friend f, Message m) throws Exception {
		String msg = m.getContentDisplay();
		MessageChainBuilder qqmsg = new MessageChainBuilder();

		//TRANSLATE MESSAGE IF NEEDED
		if(!msg.startsWith(".a")) {
			if(msg.startsWith(".t"))
				msg = msg.replace(".t ", "") + "\n<--->\n" + translate(msg.replace(".t ", ""), "zh-CN");
			else if(handler.autoTranslate.contains(m.getAuthor().getId())) {
				String t = translate(msg, "zh-CN");
				msg += t.length() == 0 || t.equalsIgnoreCase(msg) ? "" : "\n<--->\n" + t;
			}

			msg = m.getAuthor().getName() + ": " + msg;
		} else msg = msg.replaceFirst(".a ", "");

		String[] words = msg.split(" ");
		String translation = null;
		if(msg.contains("\n<--->\n")) {
			String[] tSplit = msg.split("\n<--->\n");
			words = tSplit[0].split(" ");
			translation = tSplit[1];
		}

		for (String word : words) {
			if(word.startsWith("@")) {
				String potentialMember = word.split("@")[1];
				if(handler.members.containsKey(potentialMember)) {
					qqmsg.append(new At(handler.members.get(potentialMember).getId())).append(" ");
					System.out.println("found");
					continue;
				}
			}
			qqmsg.append(word).append(" ");
		}
		if(translation != null)
			qqmsg.append("\n<--->\n").append(translation);

		for (Message.Attachment a : m.getAttachments())
			if(f != null) qqmsg.append(f.uploadImage(ExternalResource.create(getInput(a.getUrl())))); //Upload friend
			else qqmsg.append(handler.group.uploadImage(ExternalResource.create(getInput(a.getUrl())))); //Upload group
		for (Emote o : m.getEmotes())
			if(f != null) qqmsg.append(f.uploadImage(ExternalResource.create(getInput(o.getImageUrl())))); //Upload friend
			else qqmsg.append(handler.group.uploadImage(ExternalResource.create(getInput(o.getImageUrl())))); //Upload group
		handler.lastSpeaker = m.getAuthor().getId();
		return qqmsg;
	}

	public static String translate(String str, String to) throws Exception {
		ShaGoogleTranslate translate = new ShaGoogleTranslate();
		String id = translate.getInfos(str).getDetectedLanguageId();
		if(!id.contains(to))
			return translate.translate(to, str).getResult();
		return "";
	}

	public static InputStream getInput(String URL) throws IOException {
		return getConn(URL).getInputStream();
	}

	public static URLConnection getConn(String URL) {
		URLConnection conn = null;
		try {
			conn = new URL(URL).openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0");
			conn.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conn;
	}
}
