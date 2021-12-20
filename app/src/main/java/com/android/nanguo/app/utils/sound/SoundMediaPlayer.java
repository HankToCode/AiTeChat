package com.android.nanguo.app.utils.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.annotation.RawRes;

import com.android.nanguo.DemoApplication;

/**
 * SoundPool 和 MediaPlayer 管理类
 */
public class SoundMediaPlayer {

    private static SoundMediaPlayer mSoundMediaPlayer;

    /**
     * 播放长时间音乐的类
     */
    private MediaPlayer mMediaPlayer;

    /**
     * 播放长时间音乐的类
     */
    private MediaPlayer mSoundEffectMediaPlayer;

    /**
     * 系统声音管理类
     */
    private AudioManager mAudioManager;

    private final Context mContext;

    /**
     * 音乐 降低至音量系数
     */
    private float isDownRatio = 0.2f;

    /**
     * 当前设置音乐音量
     */
    private float currentMusicVolume = 1.0f;

    private static final String TAG = "SoundMediaPlayer";

    /**
     * 普通语音
     */
    public static final String TYPE_NORMAL = "normal";
    /**
     * 关键语音（播放时音乐音量降低百分之80）
     */
    public static final String TYPE_DIALOG = "dialog";

    /**
     * 音乐渐入（淡入）时长
     */
    private final long musicDuration = 3000;

    /**
     * 音乐是否释放
     */
    private boolean mRelease;
    /**
     * 音效是否释放
     */
    private boolean mSoundEffectRelease;

    public static SoundMediaPlayer getInstance() {
        if (mSoundMediaPlayer == null) {
            mSoundMediaPlayer = new SoundMediaPlayer();
        }
        return mSoundMediaPlayer;
    }

    public SoundMediaPlayer() {
        this.mContext = DemoApplication.getInstance().getApplicationContext();

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//        mAudioManager.requestAudioFocus(mAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        //音乐MediaPlayer
        mMediaPlayer = new MediaPlayer();

        //音效MediaPlayer
        mSoundEffectMediaPlayer = new MediaPlayer();

        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                return false;
            }
        });

        mSoundEffectMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                return false;
            }
        });

    }

    /**
     * 音频焦点监听
     */
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = focusChange -> {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                break;
            default:
                break;

        }
    };

    /**
     * 获取系统音量
     * 0~1
     */
    public float getStreamVolume() {
        if (mAudioManager != null) {
            float streamVolume = (float) mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / (float) mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            if (streamVolume < 0) {
                streamVolume = 0f;
            } else if (streamVolume > 1) {
                streamVolume = 1f;
            }
            return streamVolume;
        }
        return 1f;
    }


    /**
     * 加载本地路径播放音效
     *
     * @param soundPath 音效路径
     */
    public void loadPlaySoundEffects(String soundPath) {
        playSoundEffects(soundPath, TYPE_NORMAL, null);
    }

    /**
     * 加载本地路径播放音效
     *
     * @param soundPath 音效路径
     * @param type      音效类型 {TYPE_DIALOG 关键音效 bgm减少至20%, TYPE_NORMAL 普通音效，对bgm无影响}
     */

    public void loadPlaySoundEffects(String soundPath, String type) {
        playSoundEffects(soundPath, type, null);
    }

    /**
     * 加载本地路径播放音效
     *
     * @param soundPath  音效路径
     * @param type       音效类型 {TYPE_DIALOG 关键音效 bgm减少至20%, TYPE_NORMAL 普通音效，对bgm无影响}
     * @param onListener 音效播放完成后回调
     */

    public void loadPlaySoundEffects(String soundPath, String type, OnAudioCompletionListener onListener) {
        playSoundEffects(soundPath, type, onListener);
    }

    /**
     * 加载Raw播放音效
     *
     * @param resId 本地音效raw资源
     */

    public void loadPlaySoundEffects(@RawRes int resId) {
        playSoundEffects(resId, "", null);
    }

    /**
     * 加载Raw播放音效
     *
     * @param resId      本地音效raw资源
     * @param isloop     是否循环音效（无使用）
     * @param onListener 音效播放完成
     */

    public void loadPlaySoundEffects(@RawRes int resId, boolean isloop, OnAudioCompletionListener onListener) {
        playSoundEffects(resId, "", onListener);
    }

    /**
     * 播放音效
     * (1) 题目语音播放的时候，或者玩家点击小喇叭播放题目语音或者关键信息语音时，音乐音量自动减小20%，语音播放完毕后恢复原大小
     * (2) 游戏结算或者正确反馈时音乐音量减少
     *
     * @param resId      本地音效raw资源
     * @param type       音效类型 {TYPE_DIALOG 关键音效 bgm减少至20%, TYPE_NORMAL 普通音效，对bgm无影响}
     * @param onListener 音效播放完成后回调
     */
    public void playSoundEffects(@RawRes int resId, String type, final OnAudioCompletionListener onListener) {
        if (mSoundEffectMediaPlayer != null) {
            try {
                mSoundEffectRelease = false;
                //暂停所有正在播放的音效 避免音效重叠
                stopAllSoundEffects();
                mSoundEffectMediaPlayer.reset();
//                Uri setDataSourceuri = Uri.parse("android.resource://com.ycf.qianzhihe/" + resId);
                Uri setDataSourceuri = Uri.parse("android.resource://com.android.nanguo/" + resId);
                mSoundEffectMediaPlayer.setDataSource(mContext, setDataSourceuri);
                mSoundEffectMediaPlayer.prepare();
                mSoundEffectMediaPlayer.setLooping(false);
                mSoundEffectMediaPlayer.start();
                float streamVolume = getStreamVolume();
                mSoundEffectMediaPlayer.setVolume(streamVolume, streamVolume);
                mSoundEffectMediaPlayer.setOnCompletionListener(mp -> {
                    setMusicVolume(currentMusicVolume, false);
                    if (onListener != null) {
                        onListener.finish(false);
                    }
                });

                if (TYPE_DIALOG.equals(type)) {
                    float newVolume = currentMusicVolume * isDownRatio;
                    setMusicVolume(newVolume, false);
                } else {
                    setMusicVolume(currentMusicVolume, false);
                }

            } catch (Exception e) {
            }
        }

    }

    /**
     * 播放音效
     * (1) 题目语音播放的时候，或者玩家点击小喇叭播放题目语音或者关键信息语音时，音乐音量自动减小20%，语音播放完毕后恢复原大小
     * (2) 游戏结算或者正确反馈时音乐音量减少
     *
     * @param soundPath  音效路径
     * @param type       音效类型 {TYPE_DIALOG 关键音效 bgm减少至20%, TYPE_NORMAL 普通音效，对bgm无影响}
     * @param onListener 音效播放完成后回调
     */
    public void playSoundEffects(String soundPath, String type, final OnAudioCompletionListener onListener) {
        if (mSoundEffectMediaPlayer != null) {
            try {
                mSoundEffectRelease = false;
                //暂停所有正在播放的音效 避免音效重叠
                stopAllSoundEffects();
                mSoundEffectMediaPlayer.reset();
                mSoundEffectMediaPlayer.setDataSource(soundPath);
                mSoundEffectMediaPlayer.prepare();
                mSoundEffectMediaPlayer.setLooping(false);
                mSoundEffectMediaPlayer.start();
                float streamVolume = getStreamVolume();
                mSoundEffectMediaPlayer.setVolume(streamVolume, streamVolume);
                mSoundEffectMediaPlayer.setOnCompletionListener(mp -> {
                    setMusicVolume(currentMusicVolume, false);
                    if (onListener != null) {
                        onListener.finish(false);
                    }
                });

                if (TYPE_DIALOG.equals(type)) {
                    float newVolume = currentMusicVolume * isDownRatio;
                    setMusicVolume(newVolume, false);
                } else {
                    setMusicVolume(currentMusicVolume, false);
                }

            } catch (Exception e) {
            }
        }

    }

    /**
     * 设置mediaPlayer音量
     *
     * @param volume 音量大小 0~1f
     */

    public void setMusicVolume(float volume) {
        setMusicVolume(volume, true);
    }

    /**
     * 设置mediaPlayer音量
     *
     * @param volume   音量大小 0~1f
     * @param isChange 是否改变当前音量
     */

    public void setMusicVolume(float volume, boolean isChange) {
        if (isChange) {
            this.currentMusicVolume = volume;
        }
        if (mMediaPlayer != null) {
            //设置左右声道音量
            mMediaPlayer.start();
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    /**
     * 播放音乐
     * (1) 长时间停留在一题内音乐要循环播放
     * (2) 两次循环播放之间要有1s停顿间隔或者无缝衔接，根据音乐具体的首尾情况决定，现在是无缝链接
     *
     * @param musicPath 音乐路径
     * @param volume    音量大小 0~1f
     */

    public void startPlayMusic(String musicPath, float volume) {
        this.currentMusicVolume = volume;
        if (mMediaPlayer != null) {
            try {
                mRelease = false;
                stopMusic();
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(musicPath);
                mMediaPlayer.prepare();
                mMediaPlayer.setLooping(true);
                mMediaPlayer.start();
                mMediaPlayer.setVolume(volume, volume);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 重播音乐
     */

    public void startMusic() {
        if (isMusicNoRelease()) {
            try {
                if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @return MediaPlayer是否还没被释放资源
     */
    private boolean isMusicNoRelease() {
        return mMediaPlayer != null && !mRelease;
    }

    /**
     * @return SoundEffectMediaPlayer是否还没被释放资源
     */
    private boolean isSoundEffectNoRelease() {
        return mSoundEffectMediaPlayer != null && !mSoundEffectRelease;
    }

    /**
     * 停止音乐
     */

    public void stopMusic() {
        if (isMusicNoRelease() && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    /**
     * 暂停音乐
     */

    public void pauseMusic() {
        if (isMusicNoRelease() && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    /**
     * 暂停全部音效
     */

    public void stopAllSoundEffects() {
        if (isSoundEffectNoRelease() && mSoundEffectMediaPlayer.isPlaying()) {
            mSoundEffectMediaPlayer.stop();
        }
    }

    /**
     * 音效是否播放中
     */

    public boolean isSoundPlaying() {
        if (isSoundEffectNoRelease() && mSoundEffectMediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

    /**
     * 音乐渐入
     */
    /*public void fadeIn(float volume) {
        KlzzLogWriter.i(TAG, "SoundMediaPlayer 音乐渐入");
        if (mMediaPlayer != null && musicDuration > 0) {
            new CountDownTimer(musicDuration, musicDuration / 10) {

                
                public void onTick(long millisUntilFinished) {
                    if (isMusicNoRelease()) {
                        float newVolume = volume - millisUntilFinished * volume / musicDuration;
                        mMediaPlayer.setVolume(newVolume, newVolume);
                    }
                }

                
                public void onFinish() {
                    mMediaPlayer.setVolume(volume, volume);
                }
            }.start();
        }

    }*/

    /**
     * 音乐渐出
     */
    /*public void fadeOut(float volume) {
        KlzzLogWriter.i(TAG, "SoundMediaPlayer 音乐渐出");
        if (mMediaPlayer != null && musicDuration > 0) {
            new CountDownTimer(musicDuration, musicDuration / 10) {

                
                public void onTick(long millisUntilFinished) {

                    if (isMusicNoRelease()) {
                        float newVolume = millisUntilFinished * volume / musicDuration;
                        mMediaPlayer.setVolume(newVolume, newVolume);
                    }
                }

                
                public void onFinish() {
                }
            }.start();
        }
    }*/


    /**
     * 所有音乐音效均暂停
     */

    public void allStop() {

        //暂停音乐
        stopMusic();

        //暂停音效
        stopAllSoundEffects();

    }


    /**
     * 销毁
     */

    public void destroy() {
        if (mSoundEffectMediaPlayer != null) {
            stopAllSoundEffects();
            mSoundEffectMediaPlayer.reset();
            mSoundEffectMediaPlayer.release();
            mSoundEffectMediaPlayer = null;
            mSoundEffectRelease = true;
        }

        if (mMediaPlayer != null) {
            stopMusic();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mRelease = true;
        }
        mAudioManager = null;
        mSoundMediaPlayer = null;

    }
}