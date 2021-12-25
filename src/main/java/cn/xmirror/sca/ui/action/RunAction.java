package cn.xmirror.sca.ui.action;

import cn.xmirror.sca.service.CheckService;
import cn.xmirror.sca.ui.ToolWindowManager;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class RunAction extends AnAction implements DumbAware {

    public RunAction() {
        super(AllIcons.Actions.Execute);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        CheckService.run(e.getProject(), ToolWindowManager.getMainWindow(e.getProject()));
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(CheckService.isStopped(e.getProject()));
    }
}
