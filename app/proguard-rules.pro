# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
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
-dontwarn com.amap.api.**
-keepclassmembers  public class net.tatans.coeus.launcher.bean.* {
   *;
}
-dontwarn android.content.Context
-dontwarn demo.**
-keep class demo.**
-keep class io.vov.utils.** { *; }
-keep class io.vov.vitamio.** { *; }
-keep class com.iflytek.** { *;}
-keep class com.iflytek.speech.** {*;}
-keepattributes Signature
-keepattributes *Annotation*
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*