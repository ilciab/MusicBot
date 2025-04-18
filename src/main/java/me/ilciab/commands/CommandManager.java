package me.ilciab.commands;

import me.ilciab.AudioHandler;
import me.ilciab.api.BotApi;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    private AudioManager audioManager;
    private AudioHandler audioHandler;
    private MessageChannelUnion channel;
    private SlashCommandInteractionEvent event;

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        audioManager = event.getGuild().getAudioManager();
        audioManager.setSelfDeafened(true);
        audioHandler = new AudioHandler();
        audioManager.setSendingHandler(audioHandler);
        BotApi.setAudioHandler(audioHandler);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        this.event = event;
        channel = event.getChannel();
        String command = event.getName();
        audioHandler.setChannel(event.getChannel());
        switch (command) {
            case "play":
                event.reply(handlePlay(event)).queue();
                break;
            case "stop":
                audioHandler.stop(event);
                event.reply("Stoppata").queue();
                break;
            case "skip":
                audioHandler.skipTrack();
                event.reply("Skipped the current song!").queue();
                break;
            case "repeat":
                audioHandler.toggleRepeat();
                break;
            case "pause":
                audioHandler.pause();
                event.reply("Paused").queue();
                break;
            case "queue":
                sendQueue(event);
                break;
            case "join":
                joinVoiceChannel(event.getMember().getVoiceState().getChannel());
                event.reply("Joined the voice channel!").queue();
                break;
            case "leave":
                leaveVoiceChannel();
                event.reply("Left the voice channel!").queue();
                break;
            default:
                event.reply("Unknown command").queue();
                break;
        }
    }

    private void sendQueue(SlashCommandInteractionEvent event) {
        StringBuilder splittedString = new StringBuilder();
        List<String> messages = new ArrayList<>();
        String[] lines = audioHandler.getQueue().split("\n");
        for (String line : lines) {
            if (splittedString.length() + line.length() > 2000) {
                messages.add(splittedString.toString());
                splittedString.setLength(0);
            } else {
                splittedString.append(line).append("\n");
            }
        }
        if(!splittedString.isEmpty())
            messages.add(splittedString.toString());
        event.reply(messages.getFirst()).queue(hook -> {
            for (int i = 1; i < messages.size(); i++) {
                hook.sendMessage(messages.get(i)).queue();
            }
        });
    }

    private String handlePlay(SlashCommandInteractionEvent event) {
        if (event.getMember().getVoiceState() == null) {
            return "You need to be in a voice message";
        }
        joinVoiceChannel(event.getMember().getVoiceState().getChannel());
        play();
        return "Started playing " + event.getOption("song").getAsString();
    }

    private void play() {
        String song = event.getOption("song").getAsString();
        Member member = event.getMember();
        assert member != null;
        if (member.getVoiceState() == null) {
            channel.sendMessage("You need to be in a voice channel to play a song").queue();
            return;
        }

        if (!audioManager.isConnected()) {
            joinVoiceChannel(member.getVoiceState().getChannel());
        }
        audioHandler.play(song);
    }

    private void joinVoiceChannel(AudioChannelUnion voiceChannel) {
        audioManager.openAudioConnection(voiceChannel);
    }

    private void leaveVoiceChannel() {
        audioManager.closeAudioConnection();
    }
}