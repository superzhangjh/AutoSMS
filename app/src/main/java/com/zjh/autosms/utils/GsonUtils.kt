package com.zjh.autosms.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object GsonUtils {
    private val gson = Gson()

    // 将对象转换为 JSON 字符串
    fun toJson(obj: Any?): String {
        return gson.toJson(obj)
    }

    // 将 JSON 字符串转换为对象
    fun <T> fromJson(json: String?, clazz: Class<T>?): T {
        return gson.fromJson(json, clazz)
    }

    // 将 JSON 字符串转换为对象列表
    fun <T> fromJsonArray(json: String?, clazz: Class<T>?): List<T> {
        val listType = TypeToken.getParameterized(MutableList::class.java, clazz).type
        return gson.fromJson(json, listType)
    }
}
