package com.zjh.autosms.utils

import android.util.Log
import java.util.regex.Pattern

object VerificationCodeUtils {
    private val pattern = Pattern.compile("([(\\[【{][^)\\]】}]*\\d+[^)\\]】}]*[)\\]】}])|(\\d+)")
    private val numPattern = Pattern.compile("\\d+")

    fun matchVerificationCode(text: String?): String? {
        if (text?.contains("验证码") == true) {
            val matchers = pattern.matcher(text)
            while (matchers.find()) {
                val group = matchers.group()
                val numMatcher = numPattern.matcher(group)
                //再匹配一遍纯数字，我不会写只匹配不被括号包裹的正则
                if (numMatcher.matches()) {
                    val number = numMatcher.group()
                    when (number.length) {
                        4, 6 -> {
                            Log.d("matchVerificationCode", "验证码：$number 原文：$text")
                            return number
                        }
                    }
                }
                Log.d("matchVerificationCode", "读取到不符合的数字:${group} 原文：$text")
            }
        }
        Log.d("matchVerificationCode", "读取验证码失败: $text")
        return null
    }
}