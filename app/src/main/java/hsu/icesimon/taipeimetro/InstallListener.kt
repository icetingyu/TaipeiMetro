package hsu.icesimon.taipeimetro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class InstallListener : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // called in the install referrer broadcast case
        val rawReferrerString = intent.getStringExtra("referrer")
        if (null != rawReferrerString) {
            Log.i("Received the following intent $rawReferrerString")
        }
    }
}