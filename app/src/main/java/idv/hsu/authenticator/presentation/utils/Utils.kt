package idv.hsu.authenticator.presentation.utils

import idv.hsu.authenticator.data.local.TOTPAccount
import idv.hsu.authenticator.utils.SecretKeyUtils
import org.apache.commons.codec.binary.Base32
import java.net.URI
import java.net.URLDecoder
import java.nio.ByteBuffer
import java.util.Locale
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
    val otpString = String.format(Locale.US, "%06d", otp)

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
    val otpString = String.format(Locale.US, "%06d", otp)

    return otpString
}

fun convertTotpDataToTOTPAccount(qrCodeData: String): TOTPAccount? {
    val uri = runCatching { URI(qrCodeData) }.getOrNull() ?: return null
    if (!uri.scheme.equals("otpauth", ignoreCase = true) ||
        !uri.host.equals("totp", ignoreCase = true)
    ) {
        return null
    }

    val label = uri.path?.removePrefix("/").orEmpty()
    val accountName = label.substringAfter(':', label).trim()
    val labelIssuer = label.substringBefore(':', missingDelimiterValue = "").trim()
    val queryParams = parseQueryParams(uri.rawQuery)
    val secret = queryParams["secret"]?.takeIf { it.isNotBlank() } ?: return null
    val issuer = queryParams["issuer"]?.takeIf { it.isNotBlank() } ?: labelIssuer

    if (accountName.isBlank()) {
        return null
    }

    return TOTPAccount(
        accountName = accountName,
        secret = SecretKeyUtils.encryptWithKeystore(secret),
        issuer = issuer
    )
}

private fun parseQueryParams(rawQuery: String?): Map<String, String> {
    if (rawQuery.isNullOrBlank()) {
        return emptyMap()
    }

    return rawQuery.split("&")
        .mapNotNull { pair ->
            val separatorIndex = pair.indexOf('=')
            val rawKey = if (separatorIndex >= 0) pair.substring(0, separatorIndex) else pair
            if (rawKey.isBlank()) {
                return@mapNotNull null
            }

            val rawValue = if (separatorIndex >= 0) pair.substring(separatorIndex + 1) else ""
            urlDecode(rawKey) to urlDecode(rawValue)
        }
        .toMap()
}

private fun urlDecode(value: String): String {
    return runCatching { URLDecoder.decode(value, "UTF-8") }.getOrDefault(value)
}
