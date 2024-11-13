# 关于流程
(1) SmsAccessibilityService(ABS) 接收通知时，做保活操作拉起 SmsNotificationListenerService(NLS)
(2) NLS 接收通知时 (onNotificationPosted) 使用正则匹配出4/6位验证码，然后将验证码及通知key发送给 ABS
(3) ABS 使用无障碍操作填充验证码，成功后将通知 Key 发送给 NLS，NLS清空 Key 对应的验证码通知

# 关于填充服务
(1) 使用节点拦截器模式逐步处理验证码，成功后消费验证码，不再向下传递
(2) 节点拦截器：
1️⃣ FocusedNodeInterceptor：查找 isFocused & isEditable 的节点执行填充文本操作
2️⃣ NumberNodeInterceptor：查找 EditText 相关的组件进行填充，支持多个 EditText 逐个填充
3️⃣ KeyboardInputInterceptor：软键盘填充模式，针对非 EditText 控件，将 isFocused 的 Node 使用 inputConnection 的方式填充，期间设计到自动切换到自定义的输入法填充  
