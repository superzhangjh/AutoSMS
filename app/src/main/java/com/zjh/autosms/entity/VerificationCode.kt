package com.zjh.autosms.entity

data class VerificationCode(
    val content: String,
    var used: Boolean
)