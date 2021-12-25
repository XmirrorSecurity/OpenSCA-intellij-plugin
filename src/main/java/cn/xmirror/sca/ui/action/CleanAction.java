package cn.xmirror.sca.ui.action;

import cn.xmirror.sca.service.CheckService;
import cn.xmirror.sca.ui.ToolWindowManager;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class CleanAction extends AnAction implements DumbAware {

    public CleanAction() {
        super(AllIcons.Actions.GC);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        CheckService.clean(e.getProject(),ToolWindowManager.getMainWindow(e.getProject()));
    }
}
