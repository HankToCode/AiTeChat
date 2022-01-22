# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#一、公共部分
#1.基本指令区
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
#-ignorewarning
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

#2.默认保留区
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
#-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}

#3.webview
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#-keepclassmembers class * extends android.webkit.webViewClient {
#    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
#    public boolean *(android.webkit.WebView, java.lang.String);
#}
#-keepclassmembers class * extends android.webkit.webViewClient {
#    public void *(android.webkit.webView, jav.lang.String);
#}

#assume no side effects:删除android.util.Log输出的日志
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

#二、需要我们不混淆的代码
#实体类
#easeui
-keep class com.hyphenate.easeui.model.** { *; }
#app
-keep class com.android.nanguo.common.model.** { *; }
-keep class com.android.nanguo.common.db.entity.** { *; }

#jar包
-libraryjars libs/mi_push_v3.6.12.jar
-libraryjars libs/vivo_push_v2.3.1.jar

#百度地图
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-keep class com.baidu.vi.** {*;}
-dontwarn com.baidu.**

#小米推送
#下面可以不需要，环信SDK混淆逻辑中已包含相关
#-keep class com.hyphenate.push.platform.mi.EMMiMsgReceiver {*;}
#可以防止一个误报的 warning 导致无法成功编译，如果编译使用的 Android 版本是 23。
-dontwarn com.xiaomi.push.**

#Vivo推送
-dontwarn com.vivo.push.**
-keep class com.vivo.push.**{*;   }
-keep class com.vivo.vms.**{*; }
#环信SDK已添加相应的规则
#-keep class com.hyphenate.push.platform.vivo.EMVivoMsgReceiver{*;}

#OPPO推送
-keep public class * extends android.app.Service
-keep class com.heytap.msp.** { *;}

#华为推送
-ignorewarnings
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}

#环信
-keep class com.hyphenate.** {*;}
-dontwarn  com.hyphenate.**
#如果使用apache库
#-keep class internal.org.apache.http.entity.** {*;}
#如果使用了实时音视频功能
-keep class com.superrtc.** {*;}
-dontwarn  com.superrtc.**

-keep class org.xmlpull.** {*;}
    -keep class com.baidu.** {*;}
    -keep public class * extends com.umeng.**
    -keep class com.umeng.** { *; }
    -keep class com.squareup.picasso.* {*;}
    -keep class com.hyphenate.* {*;}
    -keep class com.hyphenate.chat.** {*;}
    -keep class org.jivesoftware.** {*;}
    -keep class org.apache.** {*;}
    -keep class com.huawei.android.** { *; }
    #另外，demo中发送表情的时候使用到反射，需要keep SmileUtils,注意前面的包名，
    #不要SmileUtils复制到自己的项目下keep的时候还是写的demo里的包名
    -keep class com.android.nanguo.app.utils.ease.EaseSmileUtils {*;}

    #2.0.9后加入语音通话功能，如需使用此功能的api，加入以下keep
    -keep class net.java.sip.** {*;}
    -keep class org.webrtc.voiceengine.** {*;}
    -keep class org.bitlet.** {*;}
    -keep class org.slf4j.** {*;}
    #百度
#    -libraryjars libs/BaiduLBS_Android.jar
    -keep class com.baidu.** { *; }
    -keep class vi.com.gdi.bgl.android.**{*;}
    #高德
    -keep   class com.amap.api.maps.**{*;}
    -keep   class com.autonavi.amap.mapcore.*{*;}
    -keep   class com.amap.api.trace.**{*;}
    -keep   class com.amap.api.maps.**{*;}
    -keep   class com.autonavi.**{*;}
    -keep   class com.amap.api.trace.**{*;}
    -keep class com.amap.api.location.**{*;}
    -keep class com.amap.api.fence.**{*;}
    -keep class com.autonavi.aps.amapapi.model.**{*;}
    -keep   class com.amap.api.services.**{*;}
    -keep class com.amap.api.navi.**{*;}
    -keep class com.autonavi.**{*;}
    #fastjson
    -dontwarn com.alibaba.fastjson.**
    -dontskipnonpubliclibraryclassmembers
    -dontskipnonpubliclibraryclasses

    -keep class com.alibaba.fastjson.**{*;}
    -keep class * implements java.io.Serializable { *; }

    -keepattributes *Annotation
    -keepattributes Signature


-dontwarn com.alibaba.fastjson.**

-keep class com.alibaba.fastjson.** { *; }

-keepattributes Signature

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
 #阿里人脸
-keepclasseswithmembers class com.ymt.liveness.utils.UIConfig { <fields>; <methods>; }
-keep class com.ymt.liveness.utils.VersionUtil { *; }
-keep class com.detect.live.** { *; }
-keep class com.detect.facedetect.** { *; }

#南国时光源码混淆问题
-keep class com.android.nanguo.common.fcm.EMFCMTokenRefreshService {*;}
-keep class com.google.firebase.iid.** {*;}
-keep class com.android.nanguo.common.**{*;}

#event bus混淆问题
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}


#androidx混淆
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**

-keep class androidx.multidex.**{*;}


# 保护Android architecture components: Lifecycle
-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}

#--liveData
-keep class androidx.lifecycle.** { *; }
-keep class androidx.arch.core.** { *; }

# 保护 ViewModel
-keepclassmembers class * extends android.arch.lifecycle.ViewModel {
    <init>(...);
}
# 保护 Lifecycle 状态值和类
-keepclassmembers class android.arch.lifecycle.Lifecycle$State { *; }
-keepclassmembers class android.arch.lifecycle.Lifecycle$Event { *; }
# 保护 Lifecycle 事件类和注解
-keepclassmembers class * {
    @android.arch.lifecycle.OnLifecycleEvent *;
}

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.bean.** {*;}

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

##---------------End: proguard configuration for Gson  ----------


### bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}