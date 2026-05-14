# Room: generated *_Impl classes are loaded reflectively by Room.getGeneratedImplementation
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-keep class **_Impl { <init>(...); *; }
-keepclassmembers class androidx.room.RoomDatabase { ** getOpenHelper(); }
-dontwarn androidx.room.paging.**

# WorkManager (pulled in transitively by play-services-ads/games, auto-inits via androidx.startup)
-keep class androidx.work.impl.** { *; }
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker { <init>(...); }

# kotlinx.serialization — keep generated serializers for @Serializable classes
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keep,includedescriptorclasses class com.inspiredandroid.braincup.**$$serializer { *; }
-keepclassmembers class com.inspiredandroid.braincup.** {
    *** Companion;
}
-keepclasseswithmembers class com.inspiredandroid.braincup.** {
    kotlinx.serialization.KSerializer serializer(...);
}
