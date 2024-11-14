package com.zjh.autosms.service.node

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import android.view.inputmethod.InputMethodManager


/**
 * 数字输入框的填充
 */
class NumberNodeInterceptor(private val service: AccessibilityService) : NodeInterceptor() {
    private val acceptNodes = mutableListOf<AccessibilityNodeInfo>()

    override fun onAccept(code: String, rootNode: AccessibilityNodeInfo): Boolean {
        acceptNodes.clear()
        findNumericInputFields(rootNode)
        when (acceptNodes.size) {
            //单个输入框的填充（大部分情况）
            1 -> {
                fillCode(code, acceptNodes.first())
                return true
            }
            //输入框的个数与验证码相同时，逐个填充数字(哈啰APP的登录界面)
//            code.length -> {
//                acceptNodes.forEachIndexed { index, accessibilityNodeInfo ->
//                    accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
//                    fillCode(code.substring(index, index + 1), accessibilityNodeInfo)
//                }
//                return true
//            }
//            //每个都填充完整验证码
//            else -> {
//                acceptNodes.forEach {
//                    fillCode(code, it)
//                }
//                return true
//            }
        }
        return false
    }

    private fun findNumericInputFields(nodeInfo: AccessibilityNodeInfo?) {
        if (nodeInfo == null) {
            return
        }

        // 1. Check if the node is an EditText or other input type
        val className = nodeInfo.className.toString()
        Log.d("NumberNodeInterceptor", "text:${nodeInfo.text} "
                + " className:$className "
                + " isAccessibilityFocused:${nodeInfo.isAccessibilityFocused} "
                + " isEditable:${nodeInfo.isEditable} "
                + " isFocused:$${nodeInfo.isFocused}"
                + " isCheckable:$${nodeInfo.isCheckable}"
                + " isScrollable:$${nodeInfo.isScrollable}"
        )
        if (className == "android.widget.EditText" || className == "android.widget.AutoCompleteTextView" || className == "android.widget.MultiAutoCompleteTextView" || className == "com.google.android.material.textfield.TextInputLayout") {
            // 2. Check if the inputType indicates numeric input

            val inputType = nodeInfo.inputType
            if (isNumericInputType(inputType) && nodeInfo.isEditable) {
                // This is a numeric input field
                Log.d("NumberNodeInterceptor", "addNode: ${nodeInfo.text} className:$className ")
                acceptNodes.add(nodeInfo)
            }
        }

        // 3. Recursively check child nodes
        for (i in 0 until nodeInfo.childCount) {
            findNumericInputFields(nodeInfo.getChild(i))
        }
    }

    // Method to check if the input type is numeric
    private fun isNumericInputType(inputType: Int): Boolean {
        // Check if inputType is set to numeric (either integer, decimal, or phone number)
        return (inputType and InputType.TYPE_CLASS_NUMBER) == InputType.TYPE_CLASS_NUMBER
//                || ((inputType and InputType.TYPE_NUMBER_FLAG_DECIMAL) == InputType.TYPE_NUMBER_FLAG_DECIMAL)
//                || ((inputType and InputType.TYPE_CLASS_PHONE) == InputType.TYPE_CLASS_PHONE)
    }
}