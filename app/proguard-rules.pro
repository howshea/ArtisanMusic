# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Software\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#eventbus
-keepattributes *Annotation*
-keepclassmembers class ** {
   @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

#Fragment不需要在AndroidManifest.xml中注册，需要额外保护下
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment
-keep public class * extends android.view.SurfaceView

-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
  long producerIndex;
  long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
  rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef{
  rx.internal.util.atomic.LinkedQueueNode cosumerNode;
}
-dontwarn rx.Completable**
-dontwarn retrofit2.adapter.rxjava.CompletableHelper$**
-dontwarn org.codehaus.**
-dontwarn java.nio.**
-dontwarn java.lang.invoke.**
# Only required if you use AsyncExecutor
#-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
#    <init>(java.lang.Throwable);
#}
-keepnames class com.howshea.artisanmusic.utils.GlideConfiguration