package com.hendry.saku.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val balance: Long = 0L,
    val accountNumber: String = "",
    val createdAt: Long = System.currentTimeMillis()
)