package com.zjh.autosms.event

data class BusEvent(
    val what: Int,
    val extra: Any? = null
) {
    companion object {
        const val PARSE_CODE = 0
        const val UPDATE_SETTING = 1
        const val SERVICE_FINISH_INPUT = 3
    }
}