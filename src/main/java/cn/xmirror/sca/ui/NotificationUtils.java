package cn.xmirror.sca.ui;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

/**
 * @author xingluheng
 */
public class NotificationUtils {

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

    public static void balloonNotify(String message, NotificationType type, Project project) {
        if (message == null) {
            return;
        }
        NotificationGroupManager.getInstance()
                .getNotificationGroup("OpenSCA Notification Group")
                .createNotification("OpenSCA",message, type)
                .notify(project);
    }

    public static void balloonNotifyWithAction(String message, NotificationType type, AnAction anAction, Project project) {
        if (message == null) {
            return;
        }
        NotificationGroupManager.getInstance()
                .getNotificationGroup("OpenSCA Notification Group")
                .createNotification("OpenSCA",message, type)
                .addAction(anAction)
                .notify(project);
    }
}
