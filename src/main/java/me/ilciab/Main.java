package me.ilciab;

import io.github.cdimascio.dotenv.Dotenv;
import me.ilciab.commands.CommandManager;
import me.ilciab.listeners.AntiGay;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Arrays;

public class Main {


    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("DISCORD_TOKEN");
        JDA jda = JDABuilder.createDefault(token, Arrays.asList(GatewayIntent.values())).build();
        jda.addEventListener(new CommandManager());
        jda.addEventListener(new AntiGay());
    }

}