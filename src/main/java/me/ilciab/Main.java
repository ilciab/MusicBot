package me.ilciab;

import io.github.cdimascio.dotenv.Dotenv;
import me.ilciab.api.BotApi;
import me.ilciab.commands.CommandManager;
import me.ilciab.listeners.ShutUp;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("DISCORD_TOKEN");
        JDA jda = JDABuilder.createDefault(token, Arrays.asList(GatewayIntent.values())).build();
        jda.addEventListener(new CommandManager());
        jda.addEventListener(new ShutUp());
        jda.updateCommands().addCommands(
            Commands.slash("play", "Plays the selected song")
                .addOption(OptionType.STRING, "song", "The song to play", true),
            Commands.slash("skip" ,  "Skips the current song"),
            Commands.slash("pause", "Pauses or resumes the current song"),
            Commands.slash("stop", "Stops the current song"),
            Commands.slash("repeat", "Toggles the repeat mode"),
            Commands.slash("volume", "Leaves the voice channel")
                    .addOption(OptionType.INTEGER, "volume", "Set the player volume"),

            Commands.slash("nowplaying", "Shows the current song"),
            Commands.slash("queue", "Shows the queue"),

            Commands.slash("join", "Joins the voice channel"),
            Commands.slash("leave", "Leaves the voice channel")
        ).queue();
        BotApi.start();
    }
}