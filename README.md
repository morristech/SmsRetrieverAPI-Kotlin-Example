# Automatic SMS Verification with the SMS Retriever API
This sample code is in Kotlin, As per Google's new policy with the SMS Retriever API, you can perform SMS-based user verification in your Android app automatically, without requiring the user to manually type verification codes, and without requiring any extra app permissions.  

Google restricts which Android apps can request Call Log and SMS permissions, Only apps selected as the device's default app for making calls or sending text messages will be able to access call logs and SMS data from now on.

# Solution
<img src="./screenshots/sms_retriever_api.png" width=“400”/>


# Screenshot
<img src="./screenshots/sreenshot_01.png" width="300"/>


# Work flow
- Enter or pick mobile number from smart lock hint selector and initiate SMS verification call to your server.
- App request your server to verify the entered mobile number.
- Your app calls the SMS Retriever API at the same time and listening for an SMS from your server.
- Your server sends an SMS message that includes a verification code and a hash to identify your app.
- When user's device receives the SMS message, SMS Retriever API reads the SMS in your app.
- App extract verification code from SMS and sends to your server for code verification.
- Your server receives the verification code and after validating it can return success response to proceed.

# Prerequisites
The SMS Retriever API is available only on Android devices with Play services version 10.2 and newer.

# Important
The standard SMS format is given blow.

    <#> Your ExampleApp code is: 123ABC78 
    FA+9qCX9VSu

SMS alwayse starts with <#> sign and have a hash key FA+9qCX9VSu to identify your app it is generated with your app's package id. You just need to get this has key from app and share with your server. 
In next few steps you will see how to create hash keys.

# Dependencies used
    // Add at app level gradle file
       implementation 'com.google.android.gms:play-services-base:17.6.0'
       implementation 'com.google.android.gms:play-services-identity:17.0.1'
       implementation 'com.google.android.gms:play-services-auth:19.2.0'
       implementation 'com.google.android.gms:play-services-auth-api-phone:17.5.1'

     // Note: If you have migrated your code to AndroidX than add this for LocalBroadCastManager
         implementation 'androidx.localbroadcastmanager:localbroadcastmanager:1.0.0'

    
# Integration steps
1. AppSignatureHashHelper class is responsible to get Hash key associated with your app as per your packege id. This is only one time required to get your app's hash key it would always be same unless you are changing app's package id.
       
       // Inside Main Activity 
       Log.d(TAG, "HashKey: " + appSignatureHashHelper.getAppSignatures().get(0));
       // Inside  log cat Apps Hash Key: qzwS5M4KQ5H
        
                
2. Declare this SMSReceiver in your app's manifest file in side application tag.

        <receiver
            android:name=".SMSReceiver"
            android:exported="true">
            <intent-filter>
                <action android:name="com.google.android.gms.auth.api.phone.SMS_RETRIEVED" />
            </intent-filter>
        </receiver> 
3. Create SMSReceiver class that will listen SMS and extract code and create OTPReceiveListener that will communicate with Activities/Fragments.
      
       interface OTPReceiveListener {
          fun onOTPReceived(otp: String?)
          fun onOTPTimeOut()
          fun onOTPReceivedError(error: String?)
        }
4.  Create SMSReceiver listener and Initiate SmsRetrieverClient.

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
5. You will receive OTP in call back methods implemented in you  Activity/Fragment.
    
       override fun onOTPReceived(otp: String?) {
        
    }

    override fun onOTPTimeOut() {

}

    override fun onOTPReceivedError(error: String?) {

}


# Server Side Implementation / SMS Guide
[Google's official doc](https://developers.google.com/identity/sms-retriever/verify)


# Credits
[Google's official doc](https://developers.google.com/identity/sms-retriever/overview) ,
