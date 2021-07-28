package hsu.icesimon.taipeimetro

import android.app.Application
import android.content.Context
import android.os.Build
//import com.bugsee.library.Bugsee
import hsu.icesimon.taipeimetro.utils.LocalizationUtil
import hsu.icesimon.taipeimetro.utils.Log
import hsu.icesimon.taipeimetro.utils.PreferenceManagerUtil

class App : Application() {
    private val TAG = javaClass.simpleName

    override fun onCreate() {
        super.onCreate()
        context = this
        /**
         * 对于7.0以下，需要在Application创建的时候进行语言切换
         */
        val language: String = PreferenceManagerUtil.getLocale(this)
        Log.d("Language : " + language)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            getContext()?.let { LocalizationUtil.changeAppLanguage(it, language) }
        }
//        Bugsee.launch(this, "27953263-ec8f-4070-8bed-a6617a8bd6fb");

    }

    companion object {
        private var context: Context? = null
        fun getContext(): Context? {
            return context
        }
    }
}