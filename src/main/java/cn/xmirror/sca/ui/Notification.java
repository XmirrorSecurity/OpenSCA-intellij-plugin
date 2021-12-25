package cn.xmirror.sca.ui;

import com.intellij.notification.*;
import com.intellij.notification.impl.NotificationGroupEP;
import com.intellij.openapi.ui.MessageType;

public class Notification {
    private static final NotificationGroup BALLOON_NOTIFICATION_GROUP =
            new NotificationGroup("Xcheck Notification", NotificationDisplayType.BALLOON, true);

    private static final NotificationGroup WINDOW_NOTIFICATION_GROUP =
            new NotificationGroup("Xcheck Notification", NotificationDisplayType.TOOL_WINDOW, true);

    /**
     * 右下角气泡方式提醒
     *
     * @param message
     * @param type
     */
    public static void balloonNotify(String message, MessageType type) {
        if (message == null) return;
        Notifications.Bus.notify(BALLOON_NOTIFICATION_GROUP.createNotification(message, type));
    }

    /**
     * 窗口中间展示dialog方式提醒
     *
     * @param message
     * @param type
     */
    public static void windowNotify(String message, MessageType type) {
        if (message == null) return;
        Notifications.Bus.notify(WINDOW_NOTIFICATION_GROUP.createNotification(message, type));
    }
}
