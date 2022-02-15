package com.mohistmc.thor;

import com.mohistmc.thor.qq.DiscordToQQ;
import com.mohistmc.thor.qq.GroupHandler;
import com.mohistmc.thor.qq.QQToDiscord;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.GlobalEventChannel;

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

	public static void main(String[] args) throws Exception {
		jda = JDABuilder.createDefault("token",
						GatewayIntent.GUILD_MESSAGES,
						GatewayIntent.GUILD_MEMBERS,
						GatewayIntent.GUILD_EMOJIS)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.setChunkingFilter(ChunkingFilter.ALL)
				.addEventListeners(
						new DiscordToQQ())
				.build().awaitReady();

		bot = BotFactory.INSTANCE.newBot(0L /*Your account ID long*/, "password");
		bot.login();
		GlobalEventChannel.INSTANCE.registerListenerHost(new QQToDiscord());

		//Register Discord channel and assign it to a group
		new GroupHandler("groupId", "discordChannelId", "webhookUrl");
	}
}
