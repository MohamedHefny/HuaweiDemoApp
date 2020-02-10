package com.mohamedhefny.huawei.utils

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

object CipherUtil {

    /** *Verify signature information.
     * @param content Result string.
     * @param sign Signature string.
     * @param publicKey Payment public key.
     * @paramWhether the verification is successful.
     */
    fun doCheck(content: String, sign: String?, publicKey: String?): Boolean {

        if (sign == null) return false
        else if (publicKey == null) return false

        try {
            val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
            val encodedKey: ByteArray = Base64.decode(publicKey, Base64.DEFAULT)
            val pubKey: PublicKey = keyFactory.generatePublic(X509EncodedKeySpec(encodedKey))
            val signature: Signature = Signature.getInstance("SHA256WithRSA")
            signature.initVerify(pubKey)
            signature.update(content.toByteArray(StandardCharsets.UTF_8))
            val bsign: ByteArray = Base64.decode(sign, Base64.DEFAULT)
            return signature.verify(bsign)
        } catch (e: RuntimeException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}