package hsu.icesimon.taipeimetro.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by Simon Hsu on 20/9/19.
 */

class InstallListener : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // called in the install referrer broadcast case
        val rawReferrerString = intent.getStringExtra("referrer")
        if (null != rawReferrerString) {
            Log.i("Received the following intent $rawReferrerString")
        }
    }
}