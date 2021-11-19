package com.ycf.qianzhihe.app.platform;

import com.ycf.qianzhihe.BuildConfig;

public class PlatformFactory {

    private static Platform platform;

    public static Platform getPlatform() {
        return platform == null ? new DefaultPlatform() : platform;
    }


    public static void createPlatform(Platform platform) {
        PlatformFactory.platform = platform;
    }


}
