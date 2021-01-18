package com.yenepaysdkreactnative

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.yenepaySDK.PaymentOrderManager
import com.yenepaySDK.PaymentResponse
import com.yenepaySDK.model.OrderedItem

inline val ReadableMap?.ipnUrl: String?
  get() = this?.getString("ipnUrl")
inline val ReadableMap?.returnUrl: String?
  get() = this?.getString("returnUrl")
inline val ReadableMap?.merchantCode: String?
  get() = this?.getString("merchantCode")
inline val ReadableMap?.merchantOrderId: String?
  get() = this?.getString("merchantOrderId")
inline val ReadableMap?.discount: Double
  get() = this.extractDouble("discount")
inline val ReadableMap?.tax1: Double
  get() = this.extractDouble("tax1")
inline val ReadableMap?.tax2: Double
  get() = this.extractDouble("tax2")
inline val ReadableMap?.handlingFee: Double
  get() = this.extractDouble("handlingFee")
inline val ReadableMap?.shippingFee: Double
  get() = this.extractDouble("shippingFee")
inline val HashMap<*, *>?.itemId: String?
  get() = if(this?.containsKey("itemId") == true) this["itemId"] as String else null
inline val HashMap<*, *>?.itemName: String?
  get() = if(this?.containsKey("itemName") == true) this["itemName"] as String else null
inline val HashMap<*, *>?.unitPrice: Double
  get() = this.extractDouble("unitPrice")
inline val ReadableMap?.sandboxMode: Boolean
  get() = this.extractBoolean("isUseSandboxEnabled")
fun ReadableMap?.extractItems(): MutableList<OrderedItem> {
  val result = mutableListOf<OrderedItem>()
  this?.let {
    if(it.hasKey("items") && !it.isNull("items")){
      val items = it.getArray("items")
      items?.toArrayList()?.forEach { item ->
        if(item is HashMap<*,*>){
          result.add(OrderedItem(
            item.itemId, item.itemName, item.extractInt("quantity"), item.unitPrice
          ))
        }
      }
    }
  }
  return result
}
fun ReadableMap?.extractDouble(key: String, defaultValue: Double = 0.0): Double {
  return this?.let {
    return if(it.hasKey(key) && !it.isNull(key)){
      it.getDouble(key)
    } else defaultValue
  }?: defaultValue
}

fun HashMap<*, *>?.extractDouble(key: String, defaultValue: Double = 0.0): Double {
  return this?.let {
    return if(it.containsKey(key) && it[key] is Double){
      it[key] as Double
    } else defaultValue
  }?: defaultValue
}
fun HashMap<*, *>?.extractInt(key: String, defaultValue: Int = 1 ): Int {
  return this?.let {
    return if(it.containsKey(key) && it[key] is Int){
      it[key] as Int
    } else defaultValue
  }?: defaultValue
}
fun ReadableMap?.extractInt(key: String, defaultValue: Int = 1 ): Int {
  return this?.let {
    return if(it.hasKey(key) && !it.isNull(key)){
      it.getInt(key)
    } else defaultValue
  }?: defaultValue
}

fun ReadableMap?.extractBoolean(key: String, defaultValue: Boolean = false ): Boolean {
  return this?.let {
    return if(it.hasKey(key) && !it.isNull(key)){
      it.getBoolean(key)
    } else defaultValue
  }?: defaultValue
}

fun ReadableMap?.generatePaymentManager(): PaymentOrderManager? {
  if(this == null){
    return null
  }
  return PaymentOrderManager(merchantCode, merchantOrderId).also {
    it.discount = discount
    it.handlingFee = handlingFee
    it.ipnUrl = ipnUrl
    it.returnUrl = returnUrl
    it.shippingFee = shippingFee
    it.tax1 = tax1
    it.tax2 = tax2
    it.paymentProcess = "Cart"
    it.isUseSandboxEnabled = sandboxMode
    it.addItems(extractItems())
  }
}

fun PaymentResponse.toWritableMap(): WritableMap {
  return Arguments.createMap().also {
    it.putString("buyerId", buyerId)
    it.putString("customerCode", customerCode)
    it.putString("customerEmail", customerEmail)
    it.putString("customerName", customerName)
    it.putString("invoiceId", invoiceId)
    it.putString("invoiceUrl", invoiceUrl)
    it.putString("merchantCode", merchantCode)
    it.putString("merchantId", merchantId)
    it.putString("merchantOrderId", merchantOrderId)
    it.putString("orderCode", orderCode)
    it.putString("paymentOrderId", paymentOrderId)
    it.putString("signature", signature)
    it.putString("statusDescription", statusDescription)
    it.putInt("status", status)
    it.putString("statusText", statusText)
    it.putString("verificationString", verificationString)
    it.putDouble("discount", discount)
    it.putDouble("grandTotal", grandTotal)

    it.putDouble("handlingFee", handlingFee)
    it.putDouble("itemsTotal", itemsTotal)
    it.putDouble("merchantCommisionFee", merchantCommisionFee)
    it.putDouble("shippingFee", shippingFee)
    it.putDouble("tax1", tax1)
    it.putDouble("tax2", tax2)
    it.putDouble("transactionFee", transactionFee)

    it.putBoolean("isCanceled", isCanceled)
    it.putBoolean("isDelivered", isDelivered)
    it.putBoolean("isExpired", isExpiered)
    it.putBoolean("isPaymentCompleted", isPaymentCompleted)
    it.putBoolean("isPending", isPending)
    it.putBoolean("isVerifying", isVerifying)
    it.putBoolean("hasOpenDispute", hasOpenDipute())
  }
}
