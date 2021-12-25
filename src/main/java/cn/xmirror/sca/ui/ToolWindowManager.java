package cn.xmirror.sca.ui;

import cn.xmirror.sca.ui.window.ToolWindowMainPanel;
import com.intellij.openapi.project.Project;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ToolWindowManager {
    private static final Map<Project, ToolWindowMainPanel> mainPanelMap = new ConcurrentHashMap<>();

    public static void addMainWindow(Project project, ToolWindowMainPanel mainPanel) {
        mainPanelMap.put(project, mainPanel);
    }

    public static ToolWindowMainPanel getMainWindow(Project project) {
        return mainPanelMap.get(project);
    }
}
