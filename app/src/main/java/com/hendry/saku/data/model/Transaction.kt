package com.hendry.saku.data.model


data class Transaction(
    val id: String = "",
    val userId: String = "",
    val type: String = "",
    val title: String = "",
    val description: String = "",
    val amount: Long = 0L,
    val senderAccountNumber: String = "",
    val receiverAccountNumber: String = "",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)