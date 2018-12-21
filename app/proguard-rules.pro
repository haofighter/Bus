-optimizationpasses 5          # 指定代码的压缩级别
-dontusemixedcaseclassnames   # 是否使用大小写混合
-dontpreverify           # 混淆时是否做预校验
-verbose                # 混淆时是否记录日志

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法

-keep public class * extends android.app.Activity      # 保持哪些类不被混淆
-keep public class * extends android.app.Application   # 保持哪些类不被混淆
-keep public class * extends android.app.Service       # 保持哪些类不被混淆
-keep public class * extends android.content.BroadcastReceiver  # 保持哪些类不被混淆
-keep public class * extends android.content.ContentProvider    # 保持哪些类不被混淆
-keep public class * extends android.app.backup.BackupAgentHelper # 保持哪些类不被混淆
-keep public class * extends android.preference.Preference        # 保持哪些类不被混淆
-keep public class com.android.vending.licensing.ILicensingService    # 保持哪些类不被混淆

# 保留support下的所有类及其内部类
-keep class android.support.** {*;}
# 保留R下面的资源
-keep class **.R$* {*;}

-keepclasseswithmembernames class * {  # 保持 native 方法不被混淆
    native <methods>;
}
-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
    public void *(android.view.View);
}
-keepclassmembers enum * {     # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
    public static final android.os.Parcelable$Creator *;
}
-dontwarn android.net.**  #忽略某个包的警告
-keep class android.net.SSLCertificateSocketFactory{*;}
-keepattributes *Annotation*

#ftp
-keep class org.apache.commons.net.** { *; }

#so
-libraryjars ../app/src/main/jniLibs/armeabi/libcrypto.so
-libraryjars ../app/src/main/jniLibs/armeabi/libdriver.so
-libraryjars ../app/src/main/jniLibs/armeabi/libhalcrypto.so
-libraryjars ../app/src/main/jniLibs/armeabi/libnative-lib.so
-libraryjars ../app/src/main/jniLibs/armeabi/libssl.so
-libraryjars ../app/src/main/jniLibs/armeabi/libszxb.so
-libraryjars ../app/src/main/jniLibs/armeabi/libwlxsdkcore.so
-libraryjars ../app/src/main/jniLibs/armeabi/libymodem.so

#不混淆指定类
-keep class com.szxb.jni.libszxb {*;}
-keep class com.tencent.wlxsdk.WlxSdk {*;}
-keep class com.szxb.buspay.db.entity.bean.card.ConsumeCard {*;}
-keep class com.szxb.buspay.db.entity.bean.card.SearchCard {*;}
-keep class com.szxb.buspay.db.entity.scan.** {*;}
-keep class com.com.szxb.unionpay.config.**{*;}
-keep class com.szxb.buspay.db.entity.**{*;}


#Gson
#如果有用到Gson解析包的，直接添加下面这几行就能成功混淆，不然会报错。
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }

#nohttp
-dontwarn com.yolanda.nohttp.**
-keep class com.yolanda.nohttp.**{*;}

#nohttp-okhttp
-dontwarn com.yanzhenjie.nohttp.**
-keep class com.yanzhenjie.nohttp.**{*;}

# okhttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *;}
-dontwarn okio.**
-keep class okio.** { *;}

#Rx
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#GreenDao
-keep class org.greenrobot.greendao.**{*;}
-keep public interface org.greenrobot.greendao.**
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class net.sqlcipher.database.**{*;}
-keep public interface net.sqlcipher.database.**
-dontwarn net.sqlcipher.database.**
-dontwarn org.greenrobot.greendao.**

#fastjson
-dontwarn com.alibaba.fastjson.**
-dontskipnonpubliclibraryclassmembers
-dontskipnonpubliclibraryclasses

-keep class com.alibaba.fastjson.**{*;}
-keep class * implements java.io.Serializable { *; }

-keepattributes *Annotation
-keepattributes Signature

#第三方jar
-keep class org.apache.commons.net.**{*;}


#ali
#基线包使用，生成mapping.txt
-printmapping mapping.txt
#生成的mapping.txt在app/buidl/outputs/mapping/release路径下，移动到/app路径下
#修复后的项目使用，保证混淆结果一致
#-applymapping mapping.txt
#hotfix
-keep class com.taobao.sophix.**{*;}
-keep class com.ta.utdid2.device.**{*;}
-dontwarn com.alibaba.sdk.android.utils.**
#防止inline
-dontoptimize

#crash分析
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#防止混淆丢失行号
-keepattributes SourceFile,LineNumberTable