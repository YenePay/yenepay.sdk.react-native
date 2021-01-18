package com.yenepaysdkreactnative

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.yenepaySDK.PaymentOrderManager
import com.yenepaySDK.PaymentResponse
import com.yenepaySDK.model.YenePayConfiguration
import java.lang.Exception

private const val TAG = "YenepaySdkReactNativeMo"
class YenepaySdkReactNativeModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
  private val activityEventListener: ActivityEventListener = object : BaseActivityEventListener() {
    override fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, intent: Intent?) {
      if (requestCode == PAYMENT_REQ_CODE) {
        if (resultCode == Activity.RESULT_CANCELED) {
          rejectPromise(NativePaymentActivity.extractErrorMessage(intent))
        } else if (resultCode == Activity.RESULT_OK) {
          NativePaymentActivity.extractPaymentResponse(intent)?.let {
            resolvePromise(it)
          }?: rejectPromise("Invalid Payment Response Data")
        }
      }
    }
  }

  private val broadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if(intent?.action == PAYMENT_BROADCAST_ACTION) {
        processIntent(intent)
      }
    }
  }

  private val lifecycleEventListener = object : LifecycleEventListener {
    override fun onHostResume() {

    }

    override fun onHostPause() {
    }

    override fun onHostDestroy() {
//      try {
//        reactApplicationContext.unregisterReceiver(broadcastReceiver)
//      } catch (ex: Exception){
//        Log.e(TAG, "onHostDestroy: ", ex)
//      }

    }

  }
  init {
      reactContext.addActivityEventListener(activityEventListener)
      reactContext.addLifecycleEventListener(lifecycleEventListener)
    reactApplicationContext.registerReceiver(broadcastReceiver, IntentFilter(PAYMENT_BROADCAST_ACTION))
    val completionIntent = PendingIntent.getActivity(reactContext.applicationContext,
      PaymentOrderManager.YENEPAY_CHECKOUT_REQ_CODE,
      Intent(reactContext.applicationContext, NativePaymentResponseActivity::class.java), 0)
    val cancelationIntent = PendingIntent.getActivity(reactContext.applicationContext,
      PaymentOrderManager.YENEPAY_CHECKOUT_REQ_CODE,
      Intent(reactContext.applicationContext, NativePaymentResponseActivity::class.java), 0)
    YenePayConfiguration.setDefaultInstance(YenePayConfiguration.Builder(reactContext)
      .setGlobalCompletionIntent(completionIntent)
      .setGlobalCancelIntent(cancelationIntent)
      .build())
  }

  private fun processIntent(intent: Intent?){
    intent?.let {
      PaymentOrderManager.parseResponse(it)?.let { response ->
        resolvePromise(response)
      }
    }
  }
  private fun sendEvent(reactContext: ReactContext,
                        eventName: String,
                        params: WritableMap?) {
    reactContext
      .getJSModule(RCTDeviceEventEmitter::class.java)
      .emit(eventName, params)
  }
    override fun getName(): String {
        return "YenepaySdkReactNative"
    }

    // Example method
    // See https://reactnative.dev/docs/native-modules-android
    @ReactMethod
    fun multiply(a: Int, b: Int, promise: Promise) {
      promise.resolve(a * b)

    }

  @ReactMethod
  fun requestPayment(payment: ReadableMap?, promise: Promise) {
//    waitingPromise = promise
    payment.generatePaymentManager()?.let { order ->
      startPaymentActivity(order, promise)
    }?: promise.reject("PaymentError","Invalid Payment Request - data is null or invalid")
  }
  private fun startPaymentActivity(order: PaymentOrderManager, promise: Promise){
    currentActivity?.startActivityForResult(
      NativePaymentActivity.createIntent(currentActivity!!, order),
      PAYMENT_REQ_CODE
    ) ?: promise.reject("PaymentError","Current activity is null")
  }
  private fun resolvePromise(response: PaymentResponse) {
    sendEvent(reactApplicationContext, PAYMENT_RESPONSE_EVENT, response.toWritableMap())
//    waitingPromise?.resolve(response.toWritableMap())
//    waitingPromise = null
  }
  private fun rejectPromise(message: String?) {
    sendEvent(reactApplicationContext, PAYMENT_ERROR_EVENT,
      Arguments.createMap().apply {
        putString("code", "Payment Error")
        putString("message",  message?: "User cancelled payment or some error occurred during payment")
      })
//    waitingPromise?.reject(
//      "PaymentError",
//      message?: "User cancelled payment or some error occurred during payment")
//    waitingPromise = null
  }
  override fun getConstants(): Map<String, Any>? {
    val constants: MutableMap<String, Any> = HashMap()
    constants["PAYMENT_RESPONSE_EVENT_NAME"] = PAYMENT_RESPONSE_EVENT
    constants["PAYMENT_ERROR_EVENT_NAME"] = PAYMENT_ERROR_EVENT
    return constants
  }

  override fun initialize() {
    super.initialize()
  }
}

const val PAYMENT_REQ_CODE = 293
const val PAYMENT_RESPONSE_EVENT = "paymentResponseArrived"
const val PAYMENT_ERROR_EVENT = "paymentError"
const val PAYMENT_BROADCAST_ACTION = "com.yenepaysdkreactnative.broadcast.PAYMENT_RESPONSE_ACTION"



