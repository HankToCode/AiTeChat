package com.android.nanguo.app.utils.sound;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.io.IOException;

/**
 * @author Kenn
 * @date 2021/12/27 10:38
 */
public class MessageTipsPlayer {

    private static MessageTipsPlayer messageTipsPlayer = null;
    private MediaPlayer mMediaPlayer;

    public static MessageTipsPlayer getInstance(Context context) {
        if (messageTipsPlayer == null) {
            messageTipsPlayer = new MessageTipsPlayer(context);
        }
        return messageTipsPlayer;
    }

    private MessageTipsPlayer(Context context) {
        try {
            AssetFileDescriptor fileDescriptor = context.getAssets().openFd("music_message_tips.wav");
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }
}
