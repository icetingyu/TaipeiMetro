package hsu.icesimon.taipeimetro.utils

import android.content.Context
import android.util.Log

object PreferenceManagerUtil {

    const val FILE_NAME: String = "info"
    private const val KEY_LOCALE: String = "locale"

    const val KEY_DEVICE_UUID = "device_uuid"

    // =================================== save ==========================================

    /**
     *
     * save base type data to SP
     */
    fun save(context: Context?, fileName: String, key: String, value: Any?): Boolean {

        context ?: return false
        value ?: return false

        try {
            val sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
            val editor = sp.edit()
            when (value) {
                is String -> editor.putString(key, value.toString())
                is Int -> editor.putInt(key, value.toInt())
                is Long -> editor.putLong(key, value.toLong())
                is Float -> editor.putFloat(key, value.toFloat())
                is Boolean -> editor.putBoolean(key, value)
            }
            return editor.commit()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("PreferenceManager", "ITError storing object", e)
        }
        return false
    }

    /**
     *
     * save base type data to SP
     */
    fun save(context: Context?, key: String, value: Any?): Boolean {
        return context?.let {
            save(context, FILE_NAME, key, value)
        } ?: false

    }

    fun saveLocale(context: Context, locale: String) {
        save(context, KEY_LOCALE, locale)
    }

    @JvmStatic
    fun getLocale(context: Context?): String {
        return context?.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)?.getString(KEY_LOCALE, "") ?: ""
    }
}
