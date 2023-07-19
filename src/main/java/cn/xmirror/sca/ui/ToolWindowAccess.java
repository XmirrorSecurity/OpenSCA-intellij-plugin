package cn.xmirror.sca.ui;

import cn.xmirror.sca.ui.window.OverviewPanel;
import cn.xmirror.sca.ui.window.ToolWindowContentPanel;
import cn.xmirror.sca.ui.window.ToolWindowMainPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author xingluheng
 * @date 2023/07/17 17:48
 **/
public class ToolWindowAccess {

    private ToolWindowAccess(){
    }

    public static <R> R getOverViewPanel(final Project project, final Function<OverviewPanel, R> action) {
        ToolWindowMainPanel mainWindow = cn.xmirror.sca.ui.ToolWindowManager.getMainWindow(project);
        if (mainWindow == null) return null;
        ToolWindowContentPanel contentPanel = mainWindow.getContentPanel();
        OverviewPanel overviewPanel = contentPanel.getOverviewPanel();
        return action.apply(overviewPanel);
    }

    public static void getOverViewPanel(final Project project, final Consumer<OverviewPanel> action) {
        ToolWindowMainPanel mainWindow = cn.xmirror.sca.ui.ToolWindowManager.getMainWindow(project);
        ToolWindowContentPanel contentPanel = mainWindow.getContentPanel();
        OverviewPanel overviewPanel = contentPanel.getOverviewPanel();
        action.accept(overviewPanel);
    }

    public static ToolWindow toolWindow(final Project project) {
        return ToolWindowManager
                .getInstance(project)
                .getToolWindow("OpenSCA");
    }
}
