# kotlinx.serialization: keep the generated serializers for our @Serializable models.
-keepclassmembers class com.mohithash.byokstore.**.** {
    *** Companion;
    *** serializer(...);
}
-keepclasseswithmembers class com.mohithash.byokstore.**.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.mohithash.byokstore.**.**$$serializer { *; }
-dontwarn org.slf4j.**
