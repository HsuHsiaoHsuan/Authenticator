package idv.hsu.authenticator.presentation.utils

import idv.hsu.authenticator.utils.SecretKeyUtils
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UtilsTest {

    @Before
    fun setUp() {
        mockkObject(SecretKeyUtils)
        every { SecretKeyUtils.encryptWithKeystore(any()) } answers { firstArg() }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `convertTotpDataToTOTPAccount returns account for valid uri`() {
        val uri = "otpauth://totp/issuer:alice?secret=SECRET&issuer=issuer"

        val account = convertTotpDataToTOTPAccount(uri)

        assertNotNull(account)
        assertEquals("alice", account!!.accountName)
        assertEquals("SECRET", account.secret)
        assertEquals("issuer", account.issuer)
    }

    @Test
    fun `convertTotpDataToTOTPAccount returns null when secret missing`() {
        val uri = "otpauth://totp/issuer:alice?issuer=issuer"

        val account = convertTotpDataToTOTPAccount(uri)

        assertNull(account)
    }

    @Test
    fun `convertTotpDataToTOTPAccount returns null for non totp scheme`() {
        val uri = "https://example.com?secret=SECRET"

        val account = convertTotpDataToTOTPAccount(uri)

        assertNull(account)
    }
}