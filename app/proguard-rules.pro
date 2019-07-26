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

-keep class com.kotlin.mvvm.model.** {*;}

# Retrofit rules

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

# RxJava rules

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

-dontnote rx.internal.util.PlatformDependent

# Picasso
-dontwarn com.squareup.okhttp.**

# Proguard rules that are applied to your test apk/code.
-ignorewarnings

-keepattributes *Annotation*

-dontnote junit.framework.**
-dontnote junit.runner.**

-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**
-dontwarn org.hamcrest.**
-dontwarn com.squareup.javawriter.JavaWriter
# Uncomment this if you use Mockito
-dontwarn org.mockito.**

## Android architecture components: Lifecycle

# LifecycleObserver's empty constructor is considered to be unused by proguard

-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {

    <init>(...);

}

# ViewModel's empty constructor is considered to be unused by proguard

-keepclassmembers class * extends android.arch.lifecycle.ViewModel {

    <init>(...);

}

# keep Lifecycle State and Event enums values

-keepclassmembers class android.arch.lifecycle.Lifecycle$State { *; }

-keepclassmembers class android.arch.lifecycle.Lifecycle$Event { *; }

# keep methods annotated with @OnLifecycleEvent even if they seem to be unused

# (Mostly for LiveData.LifecycleBoundObserver.onStateChange(), but who knows)

-keepclassmembers class * {

    @android.arch.lifecycle.OnLifecycleEvent *;

}



-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {

    <init>(...);

}



-keep class * implements android.arch.lifecycle.LifecycleObserver {

    <init>(...);

}

-keepclassmembers class android.arch.** { *; }

-keep class android.arch.** { *; }

-dontwarn android.arch.*




