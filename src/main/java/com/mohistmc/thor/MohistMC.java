package com.mohistmc.thor;

import com.mohistmc.thor.qq.DiscordToQQ;
import com.mohistmc.thor.qq.GroupHandler;
import com.mohistmc.thor.qq.QQToDiscord;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.event.Events;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

/**
 * @author 	Shawiiz_z
 * @date  	01/07/2021 13:05
 * @version 0.1
 */

public class MohistMC {
	public static JDA jda;
	public static Bot bot;
	public static HashMap<Long, GroupHandler> groups = new HashMap<>();

	public static void main(String[] args) throws LoginException, InterruptedException {
		jda = new JDABuilder().setToken("token").addEventListeners(new DiscordToQQ()).build();

		bot = BotFactoryJvm.newBot(0L /*Your account ID long*/, "password");
		bot.login();
		Events.registerEvents(bot, new QQToDiscord());
		//Register Discord channel and assign it to a group
		new GroupHandler("QQgroupId", "DiscordChannelId", "WebhookURL");
		bot.join();
		Thread.currentThread().join();
	}
}
