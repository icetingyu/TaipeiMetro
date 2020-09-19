package hsu.icesimon.taipeimetro.utils

/**
 * Created by Simon Hsu on 20/9/19.
 */

object Log {
    private var sMode = Mode.AppNameWithMethod
    private var sEnabled = true
    private var sAppName = "TaipeiMetro"

    /**
     * Enable the logger or not.
     *
     * @param enabled
     */
    fun setEnable(enabled: Boolean) {
        sEnabled = enabled
    }

    /**
     * Setup logger tag mode
     *
     * @param mode @see Mode
     */
    fun setMode(mode: Mode) {
        sMode = mode
    }

    /**
     * Setup application name
     *
     * @param name
     */
    fun setAppName(name: String) {
        sAppName = name
    }

    fun i(message: String?) {
        if (sEnabled) android.util.Log.i(tag, message)
    }

    fun i(message: String?, e: Throwable?) {
        if (sEnabled) android.util.Log.i(tag, message, e)
    }

    fun e(message: String?) {
        if (sEnabled) android.util.Log.e(tag, message)
    }

    fun e(message: String?, e: Throwable?) {
        if (sEnabled) android.util.Log.e(tag, message, e)
    }

    fun d(message: String?) {
        if (sEnabled) android.util.Log.d(tag, message)
    }

    fun v(message: String?) {
        if (sEnabled) android.util.Log.v(tag, message)
    }

    fun v(message: String?, e: Throwable?) {
        if (sEnabled) android.util.Log.v(tag, message, e)
    }

    fun w(message: String?) {
        if (sEnabled) android.util.Log.w(tag, message)
    }

    fun w(message: String?, e: Throwable?) {
        if (sEnabled) android.util.Log.w(tag, message, e)
    }

    private val tag: String
        private get() {
            var st: Array<StackTraceElement>? = null
            var caller: Array<String>? = null
            when (sMode) {
                Mode.AppNameWithClass -> {
                    st = Thread.currentThread().stackTrace
                    if (st != null && st.size >= 4) {
                        caller = getCallerInfo(st)
                        return sAppName + "-" + caller[0]
                    }
                }
                Mode.AppNameWithMethod -> {
                    st = Thread.currentThread().stackTrace
                    if (st != null && st.size >= 4) {
                        caller = getCallerInfo(st)
                        return sAppName + "-" + caller[0] + ":" + caller[1]
                    }
                }
            }
            return sAppName
        }

    private fun getCallerInfo(st: Array<StackTraceElement>): Array<String> {
        var findSelf = false
        val info = arrayOf("Unknown", "Unknown")
        for (e in st) {
            val name = e.className
            if (!findSelf) {
                if (name == Log::class.java.getName()) {
                    findSelf = true
                }
            } else {
                if (name != Log::class.java.getName()) {
                    val s: Array<String> = name.split("\\.").toTypedArray()
                    info[0] = s[s.size - 1]
                    info[1] = e.methodName
                    break
                }
            }
        }
        return info
    }

    /**
     * Defines 3 kinds of tag mode
     * AppName -- The tag would be only APP's name
     * AppNameWithClass -- The tag would &lt;appname&gt;-&lt;classname&gt;
     * AppNameWithMethod -- The tag would be &lt;appname&gt;-&lt;classname&gt;:&lt;methodname&gt;
     */
    enum class Mode {
        AppName, AppNameWithClass, AppNameWithMethod
    }
}