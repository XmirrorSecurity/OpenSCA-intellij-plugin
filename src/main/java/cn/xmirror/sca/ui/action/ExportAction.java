package cn.xmirror.sca.ui.action;

import cn.xmirror.sca.engine.EngineAssistant;
import cn.xmirror.sca.ui.NotificationUtils;
import cn.xmirror.sca.ui.window.OverviewPanel;
import cn.xmirror.sca.ui.window.ToolWindowContentPanel;
import cn.xmirror.sca.ui.window.ToolWindowMainPanel;
import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * 导出按钮
 *
 * @author xingluheng
 * @date 2023/07/18 20:38
 **/
public class ExportAction extends AnAction implements DumbAware {

    public ExportAction() {
        super(AllIcons.ToolbarDecorator.Export);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String checkResultPath = EngineAssistant.getCheckResultJsonPath(e.getProject());
        File file = new File(checkResultPath);
        if (file.exists()){
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                NotificationUtils.balloonNotify(ex.getMessage(),  NotificationType.ERROR);
            }
        }else {
            NotificationUtils.balloonNotify("检测结果文件未找到",  NotificationType.ERROR);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean enable = false;
        ToolWindowMainPanel mainWindow = cn.xmirror.sca.ui.ToolWindowManager.getMainWindow(e.getProject());
        if (mainWindow != null) {
            ToolWindowContentPanel contentPanel = mainWindow.getContentPanel();
            OverviewPanel overviewPanel = contentPanel.getOverviewPanel();
            DefaultMutableTreeNode rootNode = overviewPanel.getRootNode();
            if (rootNode.getChildCount() > 0) {
                enable = true;
            }
        }
        e.getPresentation().setEnabled(enable);
    }
}
