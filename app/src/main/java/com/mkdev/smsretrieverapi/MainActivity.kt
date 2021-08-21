package com.mkdev.smsretrieverapi

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever

class MainActivity : AppCompatActivity(), SMSReceiver.OTPReceiveListener {

    private val TAG = MainActivity::class.java.simpleName
    private var smsReceiver: SMSReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appSignatureHashHelper = AppSignatureHashHelper(this)

        // This code requires one time to get Hash keys do comment and share key
        Log.i(TAG, "HashKey: " + appSignatureHashHelper.appSignatures[0])

        startSMSListener()
    }

    /**
     * Starts SmsRetriever, which waits for ONE matching SMS message until timeout
     * (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
     * action SmsRetriever#SMS_RETRIEVED_ACTION.
     */
    private fun startSMSListener() {
        try {
            smsReceiver = SMSReceiver()
            smsReceiver?.setOTPListener(this)

            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            registerReceiver(smsReceiver, intentFilter)

            val client = SmsRetriever.getClient(this)
            val task = client.startSmsRetriever()
            task.addOnSuccessListener {
                // API successfully started
            }
            task.addOnFailureListener {
                // Fail to start API
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOTPReceived(otp: String?) {
        showToast("OTP Received: $otp");

        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
            smsReceiver = null;
        }
    }

    override fun onOTPTimeOut() {
        showToast("OTP Time out")
    }

    override fun onOTPReceivedError(error: String?) {
        showToast(error ?: "ERROR")
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver)
        }
    }
}