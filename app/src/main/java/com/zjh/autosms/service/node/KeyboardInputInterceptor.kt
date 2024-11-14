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
        Log.d(TAG, "Service 类名:${serviceName}")
        val imm = service.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        for (inputMethodInfo in imm.enabledInputMethodList) {
            Log.d(TAG, "读取到输入法:${inputMethodInfo.component.className} id:${inputMethodInfo.id} isDefaultResourceId:${inputMethodInfo.isDefaultResourceId}")
            if (serviceName == inputMethodInfo.component.className) {
                // 切换到我们自己的输入法
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    //将验证码写入SP等待输入法调用
                    SPUtils.getInstance().putString(SPUtils.KEY_AUTO_FILL_CODE, code)

                    //适配安卓13，在填充完毕后会将填充输入法禁用。所以在填充前将输入法启用
                    var enable = 0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        enable = service.softKeyboardController.setInputMethodEnabled(inputMethodInfo.id, true)
                    }
                    //切换输入法
                    val success = service.softKeyboardController.switchToInputMethod(inputMethodInfo.id)
                    Log.d(TAG, "切换输入法：${inputMethodInfo.component.className} success:$success enable:$enable")
                    if (success) {
                        return true
                    } else {
                        //失败时清空验证码
                        SPUtils.getInstance().putString(SPUtils.KEY_AUTO_FILL_CODE, "")
                    }
                }
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