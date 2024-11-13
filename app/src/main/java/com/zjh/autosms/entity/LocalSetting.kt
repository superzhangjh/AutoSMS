package com.zjh.autosms.entity

import com.zjh.autosms.utils.GsonUtils
import com.zjh.autosms.utils.SPUtils

data class LocalSetting(
    var addClipping: Boolean = true,
    var removeClippingAlterUse: Boolean = true,
    var toastAddClipping: Boolean = true,
) {
    companion object {
        fun getDefault(): LocalSetting {
            val json = SPUtils.getInstance().getString(SPUtils.KEY_LOCAL_SETTING)
            return if (json.isNullOrEmpty()) {
                LocalSetting()
            } else {
                GsonUtils.fromJson(json, LocalSetting::class.java)
            }
        }
    }
}