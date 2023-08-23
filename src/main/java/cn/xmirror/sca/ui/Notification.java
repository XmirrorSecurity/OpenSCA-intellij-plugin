package cn.xmirror.sca.ui;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.ProjectManager;

/**
 * @author xingluheng
 */
public class Notification {

    /**
     * 右下角气泡方式提醒
     *
     * @param message
     * @param type
     */
    public static void balloonNotify(String message, NotificationType type) {
        if (message == null) {
            return;
        }
        NotificationGroupManager.getInstance()
                .getNotificationGroup("OpenSCA Notification Group")
                .createNotification("OpenSCA",message, type)
                .notify(ProjectManager.getInstance().getDefaultProject());
    }
}
