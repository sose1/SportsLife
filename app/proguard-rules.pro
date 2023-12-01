-keepattributes SourceFile,LineNumberTable
-keep class com.google.android.gms.internal.** { *; }

-keepclassmembers class * {
    <init>();
    <fields>;
    <methods>;
    void set*(*);
    *** get*();
}

-keep class com.kwasowski.sportslife.ui.activeTraining.TrainingTimeService {
    *;
}

-keep class android.content.** { *; }
-keep class android.os.** { *; }
-keep class android.content.IntentFilter {
    *;
}

# Ignore warnings about unknown classes
-dontwarn com.squareup.okhttp.CipherSuite
-dontwarn com.squareup.okhttp.ConnectionSpec
-dontwarn com.squareup.okhttp.TlsVersion