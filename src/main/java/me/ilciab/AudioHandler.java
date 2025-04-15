package me.ilciab;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.nio.ByteBuffer;

public class AudioHandler implements AudioSendHandler {

    private AudioPlayerManager playerManager;
    private final AudioPlayer audioPlayer;
    private TrackScheduler trackScheduler;
    private AudioFrame lastFrame;

    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }

    public AudioHandler() {
        YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(true);             //youtube audio source manager from youtube-source
        playerManager = new DefaultAudioPlayerManager();                                                //adding the default audio player manager replacing
        playerManager.registerSourceManager(youtube);                                                   // the default one with the youtube-source one
        AudioSourceManagers.registerRemoteSources(playerManager, com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class);
        audioPlayer = playerManager.createPlayer();
        trackScheduler = new TrackScheduler(audioPlayer);
        audioPlayer.addListener(trackScheduler);
    }


    public void play(String song) {
        final String[] answer = new String[1];
        playerManager.loadItem(song, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                trackScheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    //trackScheduler.queue(track);
                }
            }

            @Override
            public void noMatches() {
            }


            @Override
            public void loadFailed(FriendlyException throwable) {
            }
        });


    }

    public void stop(SlashCommandInteractionEvent event) {
        trackScheduler.stopTrack();
    }

    public void pause() {
        if (audioPlayer.isPaused()) {
            audioPlayer.setPaused(false);
        } else {
            audioPlayer.setPaused(true);
        }
    }
}
