package com.zjh.autosms.service.node

import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo

/**
 * 焦点输入框的填充
 */
class FocusedNodeInterceptor : NodeInterceptor() {
    override fun onAccept(code: String, rootNode: AccessibilityNodeInfo): Boolean {
        val focusedNode = findFocusedNode(rootNode)
        if (focusedNode != null) {
            fillCode(code, focusedNode)
            return true
        }
        return false
    }

    // 查找获得焦点的控件
    private fun findFocusedNode(rootNode: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (rootNode.isFocused && rootNode.isEditable) {
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
}