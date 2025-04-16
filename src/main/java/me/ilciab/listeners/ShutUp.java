package me.ilciab.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShutUp extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        List<String> shutUps = new ArrayList<>();
        shutUps.add("573481869937606696");
        if(!shutUps.contains(event.getAuthor().getId()))
            return;
        event.getMessage().reply("Stai zitto").queue();
    }
}
