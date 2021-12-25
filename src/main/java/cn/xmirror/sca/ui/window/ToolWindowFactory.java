package cn.xmirror.sca.ui.window;

import cn.xmirror.sca.ui.ToolWindowManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

public class ToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ToolWindowMainPanel mainPanel = new ToolWindowMainPanel(project);
        ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentManager.getFactory().createContent(mainPanel, "Xcheck", false);
        contentManager.addContent(content);

        Disposer.register(project, mainPanel);
        ToolWindowManager.addMainWindow(project, mainPanel);
    }
}
