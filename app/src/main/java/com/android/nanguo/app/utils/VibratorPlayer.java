package com.android.nanguo.app.utils;

import android.content.Context;
import android.os.Vibrator;
 
/**
 * 震动功能工具类
 *
 * @author linzhiyong
 * @time 2017-01-16 10:11:16
 */
public class VibratorPlayer {
 
    private final Vibrator vibrator;
 
    public VibratorPlayer(Context context) {
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }
 
    /**
     * 开始震动
     *
     * @param delay 延时时间
     * @param interval 震动间隔
     * @param isRepeat 是否重复
     */
    public void play(long delay, long interval, boolean isRepeat) {
        long[] pattern = {delay, interval};
        this.vibrator.vibrate(pattern, isRepeat ? 0 : -1);
    }
 
    /**
     * 停止震动
     */
    public void stop() {
        this.vibrator.cancel();
    }
 
}