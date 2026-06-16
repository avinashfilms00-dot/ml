# Add project specific ProGuard rules here.
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson
-keepattributes *Annotation*
-keep class com.nirman.ledger.model.** { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
