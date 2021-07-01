package com.mohistmc.thor.qq;

import com.mohistmc.thor.MohistMC;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import static com.mohistmc.thor.qq.Utils.makeMessage;
import static com.mohistmc.thor.qq.Utils.translate;

/**
 * @author 	Shawiiz_z
 * @date  	01/07/2021 13:05
 * @version 0.1
 */

public class DiscordToQQ extends ListenerAdapter {

	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if(e.isWebhookMessage()
				|| e.getAuthor().isBot()
				|| e.getMessage().getContentRaw().startsWith(".no")) return;

		//Basic checks
		if(e.getChannel().getTopic() == null) return;
		long qqGroup;
		try {
			qqGroup = Long.parseLong(e.getChannel().getTopic());
		} catch (Exception ex) {
			return;
		}
		if(!MohistMC.groups.containsKey(qqGroup)) return;

		String msg = e.getMessage().getContentDisplay();
		String id = e.getAuthor().getId();
		GroupHandler group = MohistMC.groups.get(qqGroup);

		if(msg.startsWith(".displayconfirm")) {
			if(group.displayConfirm.contains(e.getAuthor().getId())) {
				group.displayConfirm.remove(id);
				e.getChannel().sendMessage("Enabled confirm message displad").queue();
			} else {
				group.displayConfirm.add(id);
				e.getChannel().sendMessage("Disabled confirm message display").queue();
			}
			return;
		}

		if(msg.startsWith(".displayauthor")) {
			if(group.authorDisplay.contains(e.getAuthor().getId())) {
				group.authorDisplay.remove(id);
				e.getChannel().sendMessage("Author name display set to disabled").queue();
			} else {
				group.authorDisplay.add(id);
				e.getChannel().sendMessage("Author name display set to enabled").queue();
			}
			return;
		}

		if(msg.startsWith(".autotranslate")) {
			if(group.autoTranslate.contains(e.getAuthor().getId())) {
				group.autoTranslate.remove(id);
				e.getChannel().sendMessage("Disabled auto translation").queue();
			} else {
				group.autoTranslate.add(id);
				e.getChannel().sendMessage("Enabled auto translation").queue();
			}
			return;
		}

		try {
			String localTranslation = translate(msg, "en");
			if(localTranslation.replaceAll(" ", "").length() > 0)
				e.getChannel().sendMessage("Local translation: " + localTranslation).queue();
		} catch (Exception ignored) {
		}

		try {
			MessageChainBuilder built = makeMessage(group, null, e.getMessage());
			group.group.sendMessage(built.build());
			if(!group.displayConfirm.contains(e.getAuthor().getId()))
				e.getChannel().sendMessage(built.build().contentToString()).queue();
		} catch (Exception exception) {
			e.getChannel().sendMessage("Failed to send this message!").queue();
			exception.printStackTrace();
		}
	}
}
