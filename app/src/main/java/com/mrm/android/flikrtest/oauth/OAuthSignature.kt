package com.mrm.android.flikrtest.oauth

import android.util.Log
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class OAuthSignature {
    fun encryptHmacsha1(string: String, key: String): String{
        try{
            val mac = Mac.getInstance("HmacSHA1")
            val secret = SecretKeySpec(key.toByteArray(), "HmacSHA1")
            mac.init(secret)
            val digest = mac.doFinal(string.toByteArray())
            val sb = StringBuilder(digest.size*2)
            for(b in digest){
               val s = Integer.toHexString(b.toInt())
                if(s.length == 1) sb.append('0')
                sb.append(s)
            }
            return sb.toString()
        }catch (e: Exception){
            Log.i("Sig","Exception :" + e.message + e)
        }
        return ""
    }

}