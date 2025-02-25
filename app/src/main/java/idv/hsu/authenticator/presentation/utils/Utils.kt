package idv.hsu.authenticator.presentation.utils

import android.net.Uri
import idv.hsu.authenticator.data.local.TOTPAccount
import idv.hsu.authenticator.utils.SecretKeyUtils
import org.apache.commons.codec.binary.Base32
import timber.log.Timber
import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun generateTOTPWithTime(secret: String, time: Long, timeStepSeconds: Long = 30): Pair<String, Long> {
    val decryptedSecret = SecretKeyUtils.decryptWithKeystore(secret)
    val key = Base32().decode(decryptedSecret)
    val hmacKey = SecretKeySpec(key, "HmacSHA1")

    val timeCounter = time / timeStepSeconds
    val data = ByteBuffer.allocate(8).putLong(timeCounter).array()

    val mac = Mac.getInstance("HmacSHA1")
    mac.init(hmacKey)
    val hash = mac.doFinal(data)

    val offset = hash.last().toInt() and 0xf
    val binary = (hash[offset].toInt() and 0x7f shl 24) or
            (hash[offset + 1].toInt() and 0xff shl 16) or
            (hash[offset + 2].toInt() and 0xff shl 8) or
            (hash[offset + 3].toInt() and 0xff)

    val otp = binary % 1000000
    val otpString = String.format("%06d", otp)

    val timeRemaining = timeStepSeconds - (time % timeStepSeconds)

    return Pair(otpString, timeRemaining)
}

fun generateTOTP(secret: String, time: Long, timeStepSeconds: Long = 30): String {
    val decryptedSecret = SecretKeyUtils.decryptWithKeystore(secret)
    val key = Base32().decode(decryptedSecret)
    val hmacKey = SecretKeySpec(key, "HmacSHA1")

    val timeCounter = time / timeStepSeconds
    val data = ByteBuffer.allocate(8).putLong(timeCounter).array()

    val mac = Mac.getInstance("HmacSHA1")
    mac.init(hmacKey)
    val hash = mac.doFinal(data)

    val offset = hash.last().toInt() and 0xf
    val binary = (hash[offset].toInt() and 0x7f shl 24) or
            (hash[offset + 1].toInt() and 0xff shl 16) or
            (hash[offset + 2].toInt() and 0xff shl 8) or
            (hash[offset + 3].toInt() and 0xff)

    val otp = binary % 1000000
    val otpString = String.format("%06d", otp)

    return otpString
}

fun convertTotpDataToTOTPAccount(qrCodeData: String): TOTPAccount? {
    if (qrCodeData.startsWith("otpauth://totp/")) {
        val uri = Uri.parse(qrCodeData)
        val accountName = uri.path?.substring(1)?.split(":")?.get(1) ?: ""
        val secret = uri.getQueryParameter("secret")
        val issuer = uri.getQueryParameter("issuer") ?: ""
        Timber.d("accountName: $accountName")
        Timber.d("secret: $secret")
        Timber.d("issuer: $issuer")
        if (accountName.isEmpty() || secret == null) {
            return null
        }
        return TOTPAccount(
            accountName = accountName,
            secret = SecretKeyUtils.encryptWithKeystore(secret),
            issuer = issuer
        )
    } else {
        return null
    }
}