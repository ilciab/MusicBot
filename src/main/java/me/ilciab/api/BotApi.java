package me.ilciab.api;

import static spark.Spark.*;
import me.ilciab.AudioHandler;

public class BotApi {

    private static AudioHandler audioHandler;


    public static void start() {
        port(8080); // imposta la porta

        post("/play", (req, res) -> {
            String body = req.body(); // riceve la canzone come testo semplice
            audioHandler.play(body);  // passa al bot
            return "🎵 In riproduzione: " + body;
        });

        post("/pause", (req, res) -> {
            audioHandler.pause();
            return "⏸️ Riproduzione messa in pausa/ripresa.";
        });


        post("/skip", (req, res) -> {
            audioHandler.skipTrack();
            return "⏭️ Brano saltato.";
        });

        get("/nowplaying", (req, res) -> {
            return audioHandler.getNowPlaying();
        });
    }

    public static void setAudioHandler(AudioHandler handler) {
        audioHandler = handler;
    }


}