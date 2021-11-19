package com.ycf.qianzhihe

import com.ycf.qianzhihe.app.platform.PlatformFactory
import com.ycf.qianzhihe.platform.PlatformQZH

/**
 * @author HankGreen.
 * @Date 2021/11/19
 * @name
 * desc:
 *
 */
class QZHApplication : DemoApplication() {


    override fun onCreate() {
        /*if (BuildConfig.DEBUG) {
            //开启InstantRun之后，一定要在ARouter.init之前调用openDebug
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)*/
        PlatformFactory.createPlatform(PlatformQZH())

        super.onCreate()
    }



}