package hsu.icesimon.taipeimetro.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.text.TextUtils
import android.util.DisplayMetrics
import java.util.*


object LocalizationUtil {
    @SuppressWarnings("Deprecated in Android 17")
    fun applyLanguageContext(context: Context, language: String?): Context {
        try {
            val locale = Locale(language)
            val configuration = context.resources.configuration
            val displayMetrics = context.resources.displayMetrics

            Locale.setDefault(locale)

            return if (isAtLeastSdkVersion(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
                configuration.setLocale(locale)
                context.createConfigurationContext(configuration)
            } else {
                configuration.locale = locale
                context.resources.updateConfiguration(configuration, displayMetrics)
                context
            }
        } catch (exception: Exception) {
            return context
        }
    }

    fun changeAppLanguage(context: Context, newLanguage: String?) {
        if (TextUtils.isEmpty(newLanguage)) {
            return
        }
        val resources: Resources = context.resources
        val configuration: Configuration = resources.getConfiguration()
        //获取想要切换的语言类型
        var mLocale = if (newLanguage == "zh_TW") {
            PreferenceManagerUtil.saveLocale(context, "zh_TW")
            Locale("zh_TW")
        } else {
            PreferenceManagerUtil.saveLocale(context, "en_US")
            Locale("en_US")
        }
        Log.d("mLocale : " + mLocale.toString())
        configuration.setLocale(mLocale)
        // updateConfiguration
        val dm: DisplayMetrics = resources.getDisplayMetrics()
        resources.updateConfiguration(configuration, dm)
    }

    private fun isAtLeastSdkVersion(versionCode: Int): Boolean {
        return Build.VERSION.SDK_INT >= versionCode
    }

    fun attachBaseContext(context: Context, language: String): Context? {
        Log.d("attachBaseContext: " + Build.VERSION.SDK_INT)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else {
            context
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String): Context? {
        val resources = context.resources
        var mLocale = if (language == "zh_TW") {
            PreferenceManagerUtil.saveLocale(context, "zh_TW")
            Locale("zh_TW")
        } else {
            PreferenceManagerUtil.saveLocale(context, "en_US")
            Locale("en_US")
        }
        val configuration = resources.configuration
        configuration.setLocale(mLocale)
        configuration.setLocales(LocaleList(mLocale))
        return context.createConfigurationContext(configuration)
    }
}
