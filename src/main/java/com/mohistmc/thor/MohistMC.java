package com.mohistmc.thor;

import com.mohistmc.thor.qq.DiscordToQQ;
import com.mohistmc.thor.qq.GroupHandler;
import com.mohistmc.thor.qq.QQToDiscord;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.event.Events;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Scanner;

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
		Scanner r=new Scanner(new File("DiscordToken.txt"));
		jda = new JDABuilder().setToken(r.nextLine()).addEventListeners(new DiscordToQQ()).build();
		Scanner r2=new Scanner(new File("QQToken.txt"));
		bot = BotFactoryJvm.newBot(Long.parseLong(r2.nextLine()),r2.nextLine());
		bot.login();
		Events.registerEvents(bot, new QQToDiscord());
		jda.awaitReady(); //Wait for JDA to be ready before registering groups
		Scanner r3=new Scanner(new File("Handler.txt"));
		//Register Discord channel and assign it to a group
		new GroupHandler(r3.nextLine(),r3.nextLine(),r3.nextLine());
	}
}
