package me.ilciab.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShutUp extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        List<String> gays = new ArrayList<>();
        gays.add("573481869937606696");
        if(!gays.contains(event.getAuthor().getId()))
            return;
        event.getChannel().sendMessage("Stai zitto").queue();
    }
}
