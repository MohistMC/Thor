package com.mohistmc.thor.qq.adapter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import com.mohistmc.thor.qq.GroupHandler;
import com.mohistmc.thor.qq.adapter.message.DiscordMessageChain;
import com.mohistmc.thor.qq.adapter.message.FileMessage;

import club.minnced.discord.webhook.send.WebhookEmbed.EmbedFooter;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.Mirai;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.AtAll;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.FlashImage;
import net.mamoe.mirai.message.data.ForwardMessage;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MusicShare;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.message.data.ServiceMessage;
import net.mamoe.mirai.message.data.Voice;

// TODO: Auto-generated Javadoc
/**
 * Class QQAdapter.
 *
 * @author khjxiaogu
 * file: QQAdapter.java
 * @date 2021年8月18日
 */
public class QQAdapter {
	
	/**
	 * Interface Handler.
	 * QQ Message handler type
	 * @author khjxiaogu
	 * file: QQAdapter.java
	 * @param <T> the generic message type<br>
	 * @date 2021年8月18日
	 */
	@FunctionalInterface
	private interface Handler<T extends Message>{
		
		/**
		 * Handle QQ Message.<br>
		 *
		 * @param msg the message itself<br>
		 * @param appender the appender chain<br>
		 * @param objthis context<br>
		 */
		public void handle(T msg,DiscordMessageChain appender,GroupHandler objthis) ;
	}
	
	/**
	 * Class HandlerContext.
	 *
	 * @author khjxiaogu
	 * file: QQAdapter.java
	 * @param <T> the generic message type<br>
	 * @date 2021年8月18日
	 */
	private static class HandlerContext<T extends Message>{
		
		/** The cls. */
		Class<T> cls;
		
		/** The handler.*/
		Handler<T> handler;
		
		/**
		 * Instantiates a new HandlerContext.<br>
		 *
		 * @param cls the message class<br>
		 * @param handler the handler<br>
		 */
		public HandlerContext(Class<T> cls, Handler<T> handler) {
			this.cls = cls;
			this.handler = handler;
		}
		
		/**
		 * Handle QQ Message.<br>
		 *
		 * @param msg the message itself<br>
		 * @param appender the appender chain<br>
		 * @param objthis context<br>
		 * @return true, if handler completed<br>
		 */
		@SuppressWarnings("unchecked")
		public boolean handle(Message msg,DiscordMessageChain appender,GroupHandler objthis) {
			if(cls.isInstance(msg)) {
				handler.handle((T) msg, appender,objthis);
				return true;
			}
			return false;
		};
	}
	
	/** The handlers.*/
	private static List<HandlerContext<?>> handlers=new ArrayList<>();
	
	/**
	 * Handle QQ Message.<br>
	 *
	 * @param msg the message itself<br>
	 * @param appender the appender chain<br>
	 * @param objthis context<br>
	 */
	private static void handle(Message msg,DiscordMessageChain appender,GroupHandler objthis) {
		for(HandlerContext<?> ctx:handlers) {
			if(ctx.handle(msg, appender,objthis))
				return;
		}
		appender.append(msg.contentToString());
	}
	
	/**
	 * Convert to discord.<br>
	 *
	 * @param msg the qq message<br>
	 * @param objthis context<br>
	 * @return returns converted
	 */
	public static DiscordMessageChain convert(Message msg,GroupHandler objthis) {
		DiscordMessageChain dmc=new DiscordMessageChain();
		handle(msg,dmc,objthis);
		return dmc;
		
	}
	/**
	 * Adds the handler.<br>
	 *
	 * @param <T> the generic message type
	 * @param cls the message class to handle<br>
	 * @param handler the handler<br>
	 */
	private static <T extends Message> void addHandler(Class<T> cls, Handler<T> handler) {
		handlers.add(new HandlerContext<T>(cls,handler));
	}
	
	/**
	 * Get the image file message.<br>
	 *
	 * @param img the image message<br>
	 * @param bot the bot<br>
	 * @return image file message<br>
	 */
	private static FileMessage getImage(Image img,Bot bot) {
		try {
			String iid=img.getImageId().replaceAll("[\\{\\}]","");
			String url = Mirai.getInstance().queryImageUrl(bot,img);
			URL uri=new URL(url);
			HttpURLConnection huc=(HttpURLConnection) uri.openConnection();
			huc.setDoInput(true);
			huc.setRequestMethod("GET");
			huc.connect();
			return new FileMessage(huc.getInputStream(),iid);
		} catch (IOException e) {
			// this only happens when network connection failed
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Get the voice file message.<br>
	 *
	 * @param vc the voice message<br>
	 * @return voice file message<br>
	 */
	private static FileMessage getVoice(Voice vc) {
		try {
			String iid=vc.getFileName();
			String url = vc.getUrl();
			URL uri=new URL(url);
			HttpURLConnection huc=(HttpURLConnection) uri.openConnection();
			huc.setDoInput(true);
			huc.setRequestMethod("GET");
			huc.connect();
			return new FileMessage(huc.getInputStream(),iid);
		} catch (IOException e) {
			// this only happens when network connection failed
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Get the name of any member.<br>
	 * Always valid
	 * @param m the member<br>
	 * @return name<br>
	 */
	private static String getName(NormalMember m) {
		String name=m.getNameCard();
		if(name==null)
			name=m.getNick();
		return name;
	}
	static {//add Handler(Adapter) for all messages
	   	addHandler(MessageChain.class,(msgs,sb,bot)->{
	   		for(Message msg:msgs) {
	   			handle(msg,sb,bot);
	   			}
	   		});
		addHandler(AtAll.class,(msg,sb,bot)->{sb.append("@everyone");});//ping everyone XD
		addHandler(At.class,(msg,sb,bot)->{
			long target=msg.getTarget();
			if(target!=bot.qqbot.getId())
				sb.ping(bot.discordMembers.get(getName(bot.group.get(msg.getTarget()))));
			else
				sb.ping(bot.lastSpeaker,"bot");
		});//process ping.
		addHandler(Face.class,(msg,sb,bot)->{
			sb.appendLink("https://raw.githubusercontent.com/khjxiaogu/DefaultQQEmoticon/master/emoji/" + msg.getId() + ".gif");
		});
		addHandler(ForwardMessage.class,(msg,sb,bot)->{});//large quote reply, reserved
	
		addHandler(FlashImage.class,(msg,sb,bot)->{
			FileMessage fm=getImage(msg.getImage(),bot.qqbot);
			if(fm!=null)
				sb.append(fm);
			});//treat as Image
		addHandler(Image.class,(msg,sb,bot)->{
			FileMessage fm=getImage(msg,bot.qqbot);
			if(fm!=null)
				sb.append(fm);
			});
		addHandler(PlainText.class,(msgx,sb,bot)->{
			String msg=msgx.getContent();
			String[] words=msg.split(" ");
			int lastplace=0;
			for (int i=0;i<words.length;i++) {
				//process all words
				if(words[i].contains("@")) {//process ping at different location
					StringBuilder wsv=new StringBuilder();
					if(lastplace!=i) {
						for(int j=lastplace;j<i;j++) {
							wsv.append(words[j]).append(" ");
						}
					}
					lastplace=i+1;
					String[] all=words[i].split("@");
					wsv.append(all[0]);
					sb.append(wsv.toString());
					sb.ping(bot.discordMembers.get(all[1]));
				}
			}
			if(lastplace<words.length)
				sb.append(String.join(" ",Arrays.copyOfRange(words,lastplace,words.length)));//rejoin
		});
		addHandler(QuoteReply.class,(msg,sb,bot)->{/*(msg.getSource().getOriginalMessage());*/});//quote, reserved
		addHandler(Voice.class,(msg,sb,bot)->{
			FileMessage fm=getVoice(msg);
			if(fm!=null)
			sb.append(fm);
		});
		addHandler(MusicShare.class,(msg,sb,bot)->{
			WebhookEmbedBuilder webhookEmbedBuilder = new WebhookEmbedBuilder();
			webhookEmbedBuilder.setThumbnailUrl(msg.getPictureUrl());
			webhookEmbedBuilder.setDescription(msg.getSummary()+"\nAudio:" + msg.getMusicUrl() + "");
			webhookEmbedBuilder.setFooter(new EmbedFooter(msg.getKind().name(),null));
			webhookEmbedBuilder.setTitle(new EmbedTitle(msg.getTitle(),msg.getJumpUrl()));
			sb.append(webhookEmbedBuilder);
		});
		addHandler(ServiceMessage.class,(msgx,sb,bot)->{//Xml to Embed hack?
			String msg=msgx.getContent();
			WebhookEmbedBuilder webhookEmbedBuilder = new WebhookEmbedBuilder();
			webhookEmbedBuilder.setThumbnailUrl(msg.split("audio cover=\"")[1].split("\"")[0]);
			webhookEmbedBuilder.setTitle(new EmbedTitle(msg.split("<title>")[1].split("</title>")[0],msg.split("url=\"")[1].split("\"")[0]));
			webhookEmbedBuilder.setDescription(msg.split("<summary>")[1].split("</summary>")[0]+"\nAudio:" + msg.split("src=\"")[1].split("\"")[0] + "");
			/*webhookEmbedBuilder.setDescription("\nTitle : [" + msg.split("<title>")[1].split("</title>")[0] + "](" + msg.split("</msg>")[1] + ")" +
				"\n" + msg.split("<summary>")[1].split("</summary>")[0]);*/
			sb.append(webhookEmbedBuilder);
		});
		
	}
}
