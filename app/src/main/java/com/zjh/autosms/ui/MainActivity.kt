package com.zjh.autosms.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zjh.autosms.BuildConfig
import com.zjh.autosms.R
import com.zjh.autosms.adapter.SettingItemAdapter
import com.zjh.autosms.entity.LocalSetting
import com.zjh.autosms.entity.SettingItem
import com.zjh.autosms.event.BusEvent
import com.zjh.autosms.service.SmsAccessibilityService
import com.zjh.autosms.utils.GsonUtils
import com.zjh.autosms.utils.SPUtils
import com.zjh.autosms.utils.SettingUtils
import com.zjh.autosms.utils.VerificationCodeUtils
import org.greenrobot.eventbus.EventBus

class MainActivity : ComponentActivity(R.layout.activity_main) {
    companion object {
//        private const val REQUEST_CODE_PERMISSION_SMS = 0x0001
    }
    private val rv: RecyclerView by lazy { findViewById(R.id.rv) }
    private val settingAdapter = SettingItemAdapter()
    private val localSetting by lazy { LocalSetting.getDefault() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRv()
        if (BuildConfig.DEBUG) {
            testCode()
        }
    }

    private fun testCode() {
        VerificationCodeUtils.matchVerificationCode("验证码是1234是")
        VerificationCodeUtils.matchVerificationCode("验证码为1234")
        VerificationCodeUtils.matchVerificationCode("1234验证码")
        VerificationCodeUtils.matchVerificationCode("验证码:1234")
        VerificationCodeUtils.matchVerificationCode("验证码：1234")
        VerificationCodeUtils.matchVerificationCode("【12306】验证码为1023")
        VerificationCodeUtils.matchVerificationCode("1231415912541248912421 您的验证码为1023")
        VerificationCodeUtils.matchVerificationCode("这是数字123和【456】还有(789)，以及[100]和{200}")
    }

    private fun initRv() {
        rv.layoutManager = LinearLayoutManager(this)
        settingAdapter.onChildClickListener = {
            when (it.id) {
                SettingItem.ID_ACC_SERVICE -> {
                    SettingUtils.toAccessibilitySetting(this)
                }
                SettingItem.ID_NOTIFICATION_PERMISSION -> {
                    SettingUtils.toNotificationListenerSettings(this)
                }
                SettingItem.ID_BATTERY_UNLIMITED -> {
                    SettingUtils.toBatterySetting(this)
                }
                SettingItem.ID_KEYBOARD -> {
                    SettingUtils.openInputMethodSettings(this)
                }
                SettingItem.ID_ADD_CLIPPING -> {
                    localSetting.addClipping = !localSetting.addClipping
                    updateSetting()
                }
                SettingItem.ID_TOAST_ADD_CLIPPING -> {
                    localSetting.toastAddClipping = !localSetting.toastAddClipping
                    updateSetting()
                }
                SettingItem.ID_REMOVE_CLIPPING -> {
                    localSetting.removeClippingAlterUse = !localSetting.removeClippingAlterUse
                    updateSetting()
                }
            }
        }
        rv.adapter = settingAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initSettings() {
        settingAdapter.data = listOf(
            SettingItem(SettingItem.ID_ACC_SERVICE, SettingUtils.isAccessibilitySettingsOn(this, SmsAccessibilityService::class.java), false),
            SettingItem(SettingItem.ID_NOTIFICATION_PERMISSION, SettingUtils.isNotificationListenerServiceEnabled(this), false),
            SettingItem(SettingItem.ID_BATTERY_UNLIMITED, SettingUtils.isIgnoringBatteryOptimizations(this), false),
            SettingItem(SettingItem.ID_KEYBOARD, SettingUtils.isInputMethodServiceOn(this), false),
//            SettingItem(SettingItem.ID_ADD_CLIPPING, localSetting.addClipping, true),
            SettingItem(SettingItem.ID_TOAST_ADD_CLIPPING, localSetting.toastAddClipping, true),
            SettingItem(SettingItem.ID_REMOVE_CLIPPING, localSetting.removeClippingAlterUse, true),

        )
        settingAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        initSettings()
    }

    private fun updateSetting() {
        SPUtils.getInstance().putString(SPUtils.KEY_LOCAL_SETTING, GsonUtils.toJson(localSetting))
        EventBus.getDefault().post(BusEvent(BusEvent.UPDATE_SETTING, localSetting))
        initSettings()
    }
}