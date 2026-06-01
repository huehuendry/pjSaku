package com.hendry.saku.utils


import com.hendry.saku.utils.validator.TransferValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test


class TransferValidatorTest {

    @Test
    fun `validateAmount returns error when amount is empty`() {
        val result = TransferValidator.validateAmount("")

        assertEquals("Nominal tidak boleh kosong", result)
    }

    @Test
    fun `validateAmount returns error when amount is not number`() {
        val result = TransferValidator.validateAmount("abc")

        assertEquals("Nominal harus berupa angka", result)
    }

    @Test
    fun `validateAmount returns error when amount is zero`() {
        val result = TransferValidator.validateAmount("0")

        assertEquals("Nominal harus lebih dari 0", result)
    }

    @Test
    fun `validateAmount returns null when amount is valid`() {
        val result = TransferValidator.validateAmount("50000")

        assertNull(result)
    }

    @Test
    fun `validateReceiverAccount returns error when receiver account is empty`() {
        val result = TransferValidator.validateReceiverAccount(
            senderAccountNumber = "1234567890",
            receiverAccountNumber = ""
        )

        assertEquals("Nomor rekening tujuan tidak boleh kosong", result)
    }

    @Test
    fun `validateReceiverAccount returns error when transfer to own account`() {
        val result = TransferValidator.validateReceiverAccount(
            senderAccountNumber = "1234567890",
            receiverAccountNumber = "1234567890"
        )

        assertEquals("Tidak bisa transfer ke rekening sendiri", result)
    }

    @Test
    fun `validateReceiverAccount returns null when receiver account is valid`() {
        val result = TransferValidator.validateReceiverAccount(
            senderAccountNumber = "1234567890",
            receiverAccountNumber = "9876543210"
        )

        assertNull(result)
    }

    @Test
    fun `validateBalance returns error when balance is not enough`() {
        val result = TransferValidator.validateBalance(
            amount = 100_000,
            balance = 50_000
        )

        assertEquals("Saldo tidak mencukupi", result)
    }

    @Test
    fun `validateBalance returns null when balance is enough`() {
        val result = TransferValidator.validateBalance(
            amount = 50_000,
            balance = 100_000
        )

        assertNull(result)
    }
}