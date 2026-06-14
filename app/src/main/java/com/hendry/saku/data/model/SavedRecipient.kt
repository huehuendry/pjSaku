package com.hendry.saku.data.model

import com.google.firebase.Timestamp

data class SavedRecipient(
    val recipientUid: String = "",
    val recipientName: String = "",
    val recipientAccountNumber: String = "",
    val lastTransferAt: Timestamp? = null
)