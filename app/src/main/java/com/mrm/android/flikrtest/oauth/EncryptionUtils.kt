package com.mrm.android.flikrtest.oauth

import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun calcHmac(data: String, key: String):String{
    val signingKey = SecretKeySpec(key.toByteArray(), "HmacSHA1")
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(signingKey)
    //return transToHexString(mac.doFinal(data.toByteArray()))
    return Base64.getEncoder().encodeToString(mac.doFinal(data.toByteArray()))
}