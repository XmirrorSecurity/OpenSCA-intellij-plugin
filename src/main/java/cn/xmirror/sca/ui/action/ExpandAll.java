package cn.xmirror.sca.ui.action;

import cn.xmirror.sca.ui.ToolWindowAccess;
import cn.xmirror.sca.ui.ToolWindowManager;
import cn.xmirror.sca.ui.window.OverviewPanel;
import cn.xmirror.sca.ui.window.ToolWindowContentPanel;
import cn.xmirror.sca.ui.window.ToolWindowMainPanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

/**
 * @author xingluheng
 * @date 2023/07/17 16:34
 **/
public class ExpandAll extends AnAction implements DumbAware {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ToolWindowAccess.getOverViewPanel(e.getProject(),panel->{
            panel.expandAll(4);
        });
    }
}
