package cn.xmirror.sca.ui.action;

import cn.xmirror.sca.service.CheckService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class StopAction extends AnAction implements DumbAware {
    public StopAction() {
        super(AllIcons.Actions.Suspend);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        CheckService.stop(e.getProject());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(CheckService.isRunning(e.getProject()));
    }
}
