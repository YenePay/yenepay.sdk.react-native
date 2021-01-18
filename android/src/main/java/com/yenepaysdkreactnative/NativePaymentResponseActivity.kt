package com.yenepaysdkreactnative

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.yenepaySDK.PaymentOrderManager
import com.yenepaySDK.PaymentResponse
import com.yenepaySDK.YenePayPaymentActivity
import com.yenepaySDK.handlers.PaymentHandlerActivity

class NativePaymentResponseActivity: YenePayPaymentActivity() {
  override fun onPaymentResponseArrived(response: PaymentResponse?) {
    super.onPaymentResponseArrived(response)
    response?.let {
      val intent = Intent().apply {
        action = PAYMENT_BROADCAST_ACTION
        putExtra(PaymentHandlerActivity.KEY_PAYMENT_RESPONSE, it)
      }
      applicationContext.sendBroadcast(intent)
    }
    finish()
  }

  override fun onPaymentResponseError(error: String?) {
    super.onPaymentResponseError(error)
    finish()
  }
}
