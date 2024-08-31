package idv.hsu.authenticator

import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base32

object TOTPGenerator {
    fun generateTOTP(secret: String, time: Long): String {
        val base32 = Base32()
        val key = base32.decode(secret)
        val hmacKey = SecretKeySpec(key, "HmacSHA1")
        val data = ByteBuffer.allocate(8).putLong(time / 30).array()
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(hmacKey)
        val hash = mac.doFinal(data)
        val offset = hash[hash.size - 1].toInt() and 0x0F
        val binary = ((hash[offset].toInt() and 0x7f) shl 24) or
                ((hash[offset + 1].toInt() and 0xff) shl 16) or
                ((hash[offset + 2].toInt() and 0xff) shl 8) or
                (hash[offset + 3].toInt() and 0xff)
        val otp = binary % 1000000
        return String.format("%06d", otp)
    }
}