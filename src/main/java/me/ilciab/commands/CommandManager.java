package me.ilciab.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Widget;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.internal.audio.AudioConnection;
import org.jetbrains.annotations.NotNull;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        String command = event.getName();
        switch (command) {
            case "play":
                playSong(event);
                break;
            case "stop":
                event.reply("Hello!").queue();
                break;
            case "resume":
                event.reply("Hello!").queue();
                break;
            default:
                event.reply("Unknown command").queue();
                break;
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("play", "Plays the selected song")
                .addOption(OptionType.STRING, "song", "The song to play", true));
        commandData.add(Commands.slash("stop", "Stops the current song"));
        commandData.add(Commands.slash("resume", "Resumes the current song"));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }

    private void playSong(SlashCommandInteractionEvent event) {
        MessageChannelUnion channel = event.getChannel();
        String song = event.getOption("song").getAsString();
        Member member = event.getMember();
        assert member != null;

        if (member.getVoiceState() == null) {
            channel.sendMessage("You need to be in a voice channel to play a song").queue();
            return;
        }

            member.getVoiceState().getChannel().getGuild().getAudioManager().openAudioConnection(member.getVoiceState().getChannel());
            member.getVoiceState().getChannel().getGuild().getAudioManager();
    }
}
