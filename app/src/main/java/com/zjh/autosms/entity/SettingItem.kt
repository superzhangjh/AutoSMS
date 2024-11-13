package com.zjh.autosms.entity

import com.zjh.autosms.R
import com.zjh.autosms.app.App.Companion.getApp

data class SettingItem(
    val id: Int,
    val enabled: Boolean,
    @Deprecated("不生效")
    val canModify: Boolean
) {
    companion object {
        const val ID_ACC_SERVICE = 1
        const val ID_ADD_CLIPPING = 2
        const val ID_TOAST_ADD_CLIPPING = 3
        const val ID_REMOVE_CLIPPING = 4
        const val ID_BATTERY_UNLIMITED = 5
        const val ID_KEYBOARD = 6
        const val ID_NOTIFICATION_PERMISSION = 7
    }

    fun getName() = when (id) {
        ID_ACC_SERVICE -> "无障碍服务"
        ID_ADD_CLIPPING -> "复制验证码"
        ID_TOAST_ADD_CLIPPING -> "验证码Toast"
        ID_REMOVE_CLIPPING -> "一次性验证码"
        ID_BATTERY_UNLIMITED -> "取消电池限制"
        ID_KEYBOARD -> "使用输入法填充"
        ID_NOTIFICATION_PERMISSION -> "读取通知权限"
        else -> "未知"
    }

    fun getDescription() = when (id) {
        ID_ACC_SERVICE -> "在设置里找到“${getApp().getString(R.string.app_name)}”并开启服务"
        ID_ADD_CLIPPING -> "部分应用可能无法自动填充，开启后自动将短信验证码复制到剪切板，以便手动粘贴"
        ID_TOAST_ADD_CLIPPING -> "自动复制验证码后是否弹出Toast提示"
        ID_REMOVE_CLIPPING -> "自动填充完成后自动清除剪切板的验证码"
        ID_BATTERY_UNLIMITED -> "部分系统(小米)会限制后台服务，需关闭后才能正常使用该功能，如无电池限制可忽略"
        ID_KEYBOARD -> "部分应用自动填充无效的，可开启该选项"
        ID_NOTIFICATION_PERMISSION -> "开启使用验证码读取功能，同时自动清除验证码通知"
        else -> "未知"
    }
}