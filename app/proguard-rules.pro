# Proguard rules for ParkingEscapePuzzle
-keepattributes JavascriptInterface
-keepclassmembers class com.segadroid.parkingescapepuzzle.MainActivity$WebAppInterface {
    <methods>;
}
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
