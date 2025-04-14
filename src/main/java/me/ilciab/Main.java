package me.ilciab;

import me.ilciab.commands.CommandManager;
import me.ilciab.listeners.AntiGay;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        String BOT_TOKEN= "MTM0ODczMTcyNzkwMzcyMzU3MQ.Go4ANp.Kz7UOXvf-DZ4e7sszj5ZpTtkIMsN2BLR50Kgyo";
        JDA jda = JDABuilder.createDefault(BOT_TOKEN, Arrays.asList(GatewayIntent.values())).build();
        jda.addEventListener(new CommandManager());
        jda.addEventListener(new AntiGay());
    }
}