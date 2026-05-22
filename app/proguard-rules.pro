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

-keep public class * extends androidx.glance.appwidget.GlanceAppWidgetReceiver
-keep public class * extends androidx.glance.appwidget.GlanceAppWidget
-keep public class com.example.rctschedule.ScheduleAppWidgetReceiver extends android.appwidget.AppWidgetProvider {
    public <init>();
    public void onUpdate(android.content.Context, android.appwidget.AppWidgetManager, int[]);
}
# Сохраняем все классы, связанные с RemoteViews
-keep class * extends android.widget.RemoteViews { *; }

# Сохраняем классы для работы с виджетами
-keep public class * extends android.appwidget.AppWidgetProvider { *; }

# Сохраняем компоненты, которые могут быть вызваны через рефлексию
-keepclassmembers class * extends android.appwidget.AppWidgetProvider {
    public void *(android.content.Context, android.appwidget.AppWidgetManager, int[]);
}


-dontwarn java.awt.**
-dontwarn javax.xml.stream.**
-dontwarn net.sf.saxon.**
-dontwarn org.apache.batik.**
-dontwarn org.osgi.framework.**


-keep class org.apache.poi.** { *; }
-dontwarn org.apache.poi.**
-dontwarn org.apache.xmlbeans.**
-keep class org.apache.xmlbeans.** { *; }
-dontwarn com.microsoft.schemas.**