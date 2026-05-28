package com.hendry.saku.utils

object TransferValidator {

    fun validateAmount(amountText: String): String? {
        val amount = amountText.toLongOrNull()

        return when {
            amountText.isBlank() -> "Nominal tidak boleh kosong"
            amount == null -> "Nominal harus berupa angka"
            amount <= 0 -> "Nominal harus lebih dari 0"
            else -> null
        }
    }

    fun validateReceiverAccount(
        senderAccountNumber: String,
        receiverAccountNumber: String
    ): String? {
        return when {
            receiverAccountNumber.isBlank() -> "Nomor rekening tujuan tidak boleh kosong"
            receiverAccountNumber == senderAccountNumber -> "Tidak bisa transfer ke rekening sendiri"
            else -> null
        }
    }

    fun validateBalance(
        amount: Long,
        balance: Long
    ): String? {
        return when {
            amount > balance -> "Saldo tidak mencukupi"
            else -> null
        }
    }
}