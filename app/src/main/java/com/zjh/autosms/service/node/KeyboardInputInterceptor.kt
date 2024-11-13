package com.zjh.autosms.service.node

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityService.INPUT_METHOD_SERVICE
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import android.view.inputmethod.InputMethodManager
import com.zjh.autosms.service.AutoFillInputMethodService
import com.zjh.autosms.utils.SPUtils

class KeyboardInputInterceptor(private val service: AccessibilityService) : NodeInterceptor() {
    companion object {
        private val TAG = KeyboardInputInterceptor::class.java.simpleName
        val serviceName = AutoFillInputMethodService::class.java.canonicalName
    }

    override fun onAccept(code: String, rootNode: AccessibilityNodeInfo): Boolean {
        val focusedNode = findFocusedNode(rootNode)
        if (focusedNode != null && focusedNode.text?.toString()?.trim() != code) {
            Log.d(TAG, "Service 焦点View的内容:${focusedNode.text}")
            switchInputMethod(code)
            return true
        }
        return false
    }

    private fun switchInputMethod(code: String): Boolean {
        //默认输入法的id
        var defaultInputMethodId: String? = null
        //是否切换成功
        var switchServiceSuccess = false

        Log.d(TAG, "Service 类名:${serviceName}")
        val imm = service.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        for (inputMethodInfo in imm.enabledInputMethodList) {
            Log.d(TAG, "读取到输入法:${inputMethodInfo.component.className} id:${inputMethodInfo.id} isDefaultResourceId:${inputMethodInfo.isDefaultResourceId}")
            //记录默认id，填充完需要复原回去
            if (inputMethodInfo.isDefaultResourceId > 0) {
                defaultInputMethodId = inputMethodInfo.id
            }
            if (serviceName == inputMethodInfo.component.className) {
                // 切换到我们自己的输入法
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    switchServiceSuccess = service.softKeyboardController.switchToInputMethod(inputMethodInfo.id)
                    Log.d(TAG, "切换输入法：${inputMethodInfo.component.className} success:$switchServiceSuccess")

                    //将验证码写入SP等待输入法调用
                    SPUtils.getInstance().putString(SPUtils.KEY_AUTO_FILL_CODE, code)
//                    EventBus.getDefault().post(BusEvent(BusEvent.PARSE_CODE, code))
                }
            }

            if (switchServiceSuccess && defaultInputMethodId != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    //切换到默认的输入法
//                    service.softKeyboardController.switchToInputMethod(defaultInputMethodId)
                }
                return true
            }
        }
        return false
    }

    private fun findFocusedNode(rootNode: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (rootNode.isFocused) {
            return rootNode // 如果当前节点已经获得焦点，直接返回
        }

        // 递归查找子节点
        for (i in 0 until rootNode.childCount) {
            val childNode = rootNode.getChild(i)
            if (childNode != null) {
                val focusedChild = findFocusedNode(childNode)
                if (focusedChild != null) {
                    return focusedChild
                }
            }
        }
        return null
    }

    fun onFinishInput() {

    }
}