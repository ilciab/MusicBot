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
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.nio.ByteBuffer;

public class AudioHandler implements AudioSendHandler {

    private final AudioPlayerManager playerManager;
    private final AudioPlayer audioPlayer;
    private final TrackScheduler trackScheduler;
    private MessageChannelUnion channel;
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
        YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(true);             // YouTube audio source manager from youtube-source
        playerManager = new DefaultAudioPlayerManager();                                                // adding the default audio player manager replacing
        playerManager.registerSourceManager(youtube);                                                   // the default one with the youtube-source one
        AudioSourceManagers.registerRemoteSources(playerManager, com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class);
        audioPlayer = playerManager.createPlayer();
        audioPlayer.setVolume(50);
        trackScheduler = new TrackScheduler(audioPlayer);
        audioPlayer.addListener(trackScheduler);
    }


    public void play(String song) {
        String identifier = song.startsWith("http") ? song : "ytsearch:" + song;
        playerManager.loadItem(identifier, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Using trackloaded(), something went wrong?").queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if(playlist.isSearchResult()){
                    AudioTrack firstItem = playlist.getTracks().get(0);
                    trackScheduler.queue(firstItem);
                    channel.sendMessage("Added to queue: " + firstItem.getInfo().title).queue();
                } else {
                    int size = 0;
                    for (AudioTrack track : playlist.getTracks()) {
                        trackScheduler.queue(track);
                        size++;
                    }
                    channel.sendMessage("Playlist loaded: " + playlist.getName() + "with " + size + " elements").queue();
                }
            }

            @Override
            public void noMatches() {
                channel.sendMessage("No matches found for " + song).queue();
            }


            @Override
            public void loadFailed(FriendlyException throwable) {
                channel.sendMessage("Could not play " + song + ": " + throwable.getMessage()).queue();
            }
        });


    }

    public void skipTrack() {
        trackScheduler.skipTrack();
    }

    public void stop(SlashCommandInteractionEvent event) {
        trackScheduler.stopTrack();
    }

    public void pause() {
        audioPlayer.setPaused(!audioPlayer.isPaused());
    }

    public void setChannel(MessageChannelUnion channel) {
        this.channel = channel;
        trackScheduler.setChannel(channel);
    }

    public void showQueue() {
        trackScheduler.showQueue();
    }

    public Object getNowPlaying() {
        return trackScheduler.getNowPlaying();
    }
}
