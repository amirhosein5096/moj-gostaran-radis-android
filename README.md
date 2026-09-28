# موج گستران رادیس — Android

نسخه V1 اپ اندروید WebView برای https://radis-co.com/

## امکانات
- نمایش کامل سایت رادیس داخل اپ
- JavaScript و DOM Storage
- Cookie و ورود کاربر
- Pull to Refresh
- دکمه Back اندروید
- File Upload
- باز کردن لینک‌های خارجی در اپ مناسب
- Download link handling
- Splash Screen و لوگوی رادیس
- نام اپ: موج گستران رادیس
- Package: com.radis.app

## اجرا
پوشه پروژه را در Android Studio باز کنید و Gradle Sync را اجرا کنید.
سپس روی دستگاه/Emulator اجرا کنید.

## خروجی APK
Android Studio > Build > Build APK(s)

## خروجی AAB
Android Studio > Build > Generate Signed Bundle / APK > Android App Bundle


## Google Play
این نسخه ساختار لازم برای Build در GitHub Actions را دارد.
Workflow فایل `.github/workflows/android-build.yml` یک APK تستی و AAB release unsigned می‌سازد.

برای انتشار نهایی Google Play باید AAB با keystore دائمی امضا شود. کلید خصوصی/رمز keystore را داخل سورس GitHub commit نکنید؛ از GitHub Actions Secrets یا Android Studio استفاده کنید.

فایل‌های اولیه Store Listing و Privacy Policy در پوشه `play-store/` قرار دارند.
