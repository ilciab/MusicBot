package me.ilciab.commands;

import me.ilciab.AudioHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
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
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("play", "Plays the selected song")
                .addOption(OptionType.STRING, "song", "The song to play", true));
        commandData.add(Commands.slash("skip" ,  "Skips the current song"));
        commandData.add(Commands.slash("pause", "Pauses or resumes the current song"));
        commandData.add(Commands.slash("stop", "Stops the current song"));
        commandData.add(Commands.slash("repeat", "Toggles the repeat mode"));

        commandData.add(Commands.slash("nowplaying", "Shows the current song"));
        commandData.add(Commands.slash("queue", "Shows the queue"));

        commandData.add(Commands.slash("join", "Joins the voice channel"));
        commandData.add(Commands.slash("leave", "Leaves the voice channel"));
        
        event.getGuild().updateCommands().addCommands(commandData).queue();
        audioManager = event.getGuild().getAudioManager();
        audioHandler = new AudioHandler();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        this.event = event;
        channel = event.getChannel();
        String command = event.getName();
        audioHandler.setChannel(event.getChannel());
        switch (command) {
            case "play":
                play();
                break;
            case "stop":
                audioHandler.stop(event);
                break;
            case "skip":
                audioHandler.skipTrack();
                event.reply("Skipped the current song!").queue();
                break;
            case "pause":
                audioHandler.pause();
                break;
            case "queue":
                audioHandler.showQueue();
                break;
            case "join":
                System.out.println("Member: " + event.getUser().getEffectiveName());
                joinVoiceChannel(event.getMember());
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

    private void play() {
        String song = event.getOption("song").getAsString();
        Member member = event.getMember();
        assert member != null;
        if (member.getVoiceState() == null) {
            channel.sendMessage("You need to be in a voice channel to play a song").queue();
            return;
        }

        if (!audioManager.isConnected()) {
            joinVoiceChannel(member);
        }
        audioHandler.play(song);
    }

    private void joinVoiceChannel(Member member) {
        AudioChannelUnion voiceChannel = member.getVoiceState().getChannel();
        audioManager = member.getVoiceState().getChannel().getGuild().getAudioManager();
        audioManager.setSelfDeafened(true);
        audioManager.setSendingHandler(audioHandler);
        audioManager.openAudioConnection(voiceChannel);
    }

    private void leaveVoiceChannel() {
        audioManager.closeAudioConnection();
    }
}