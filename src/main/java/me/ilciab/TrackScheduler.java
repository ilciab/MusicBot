package me.ilciab;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private MessageChannelUnion channel;
    private boolean repeating = false;
    private boolean paused = false;
    boolean isPlaying = false;
    private final List<AudioTrack> queue = new ArrayList<>();

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
    }


    public void setChannel(MessageChannelUnion channel) {
        this.channel = channel;
    }


    public void queue(AudioTrack track) {
        if (isPlaying) {
            queue.add(track);
        } else {
            player.startTrack(track, true);
            isPlaying = true;
        }
    }

    public void skipTrack() {
        if (queue.isEmpty()) {
            player.stopTrack();
            isPlaying = false;
        } else {
            player.startTrack(queue.removeFirst(), false);
        }
    }

    public void stopTrack() {
        player.stopTrack();
        isPlaying = false;
        queue.clear();
    }

    public String getQueue() {
        if(queue.isEmpty()) {
            channel.sendMessage("Queue is empty").queue();
            return null;
        }

        StringBuilder queueString = new StringBuilder();
        int i = 1;
        for (AudioTrack track : this.queue) {
            queueString.append(i).append(" - ").append(track.getInfo().title).append("\n");
            i++;
        }
        return queueString.toString();

    }


    @Override
    public void onPlayerPause(AudioPlayer player) {
        paused = true;
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        paused = false;
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // A track started playing
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(endReason == AudioTrackEndReason.LOAD_FAILED){
            skipTrack();
            return;
        }
        if (endReason.mayStartNext) {
            if(queue.isEmpty()) {
                return;
            }
            player.startTrack(queue.getFirst(), true);
            queue.removeFirst();
            return;
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be received separately)
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // Audio track has been unable to provide us any audio, might want to just start a new track
    }

    public Object getNowPlaying() {
        JSONObject nowPlaying = new JSONObject();
        nowPlaying.put("Title", player.getPlayingTrack().getInfo().title);
        nowPlaying.put("Author", player.getPlayingTrack().getInfo().author);
        nowPlaying.put("Paused", player.isPaused());
        nowPlaying.put("Duration", player.getPlayingTrack().getInfo().length);
        nowPlaying.put("Position", player.getPlayingTrack().getPosition());
        nowPlaying.put("Volume", player.getVolume());
        return nowPlaying;
    }
}
