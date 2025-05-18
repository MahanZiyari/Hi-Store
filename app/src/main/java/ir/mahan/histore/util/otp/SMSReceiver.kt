package ir.mahan.histore.util.otp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import javax.inject.Inject


/**
 * Receive certain SMSs with specific hashcode and try to read it
 *
 * @constructor Create empty SMS Receiver
 */
class SMSReceiver @Inject constructor() : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action != SmsRetriever.SMS_RETRIEVED_ACTION) return
        val extras = intent.extras
        val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
        when (status.statusCode) {
            CommonStatusCodes.SUCCESS -> {
                val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                receiveMessageCallback?.let { callback ->
                    callback(message)
                }
            }
        }
    }

    private var receiveMessageCallback: ((String) -> Unit)? = null
    fun setOnMessageReceived(listener: (String) -> Unit) {
        receiveMessageCallback = listener
    }
}