package cn.xmirror.sca.ui.action;

import cn.xmirror.sca.ui.ToolWindowAccess;
import cn.xmirror.sca.ui.window.OverviewPanel;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareToggleAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author xingluheng
 * @date 2023/07/18 11:36
 **/
public class DisplayLow extends DumbAwareToggleAction {

    @Override
    public boolean isSelected(@NotNull AnActionEvent event) {
        final Project project = getEventProject(event);
        if (project == null) {
            return false;
        }
        Boolean displayingErrors = ToolWindowAccess.getOverViewPanel(project, OverviewPanel::isDisplayingLow);
        return Objects.requireNonNullElse(displayingErrors, false);
    }

    @Override
    public void setSelected(@NotNull AnActionEvent event, boolean selected) {
        final Project project = getEventProject(event);
        if (project == null) {
            return;
        }
        ToolWindowAccess.getOverViewPanel(project,panel ->{
            panel.setDisplayingLow(selected);
            panel.filterDisplayedResults();
        });
    }
}
