package cn.xmirror.sca.ui.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;

import javax.swing.*;

public class DescriptionPanel extends SimpleToolWindowPanel {

    private Project project;
    private ToolWindowContentPanel contentPanel;

    public DescriptionPanel(Project project, ToolWindowContentPanel contentPanel) {
        super(true,true);
        this.project = project;
        this.contentPanel = contentPanel;
        GuidePanel guidePanel = new GuidePanel(project, contentPanel.getOverviewPanel());
        setDetailDescriptionPanel(guidePanel);
    }

    public void setDetailDescriptionPanel(JPanel panel) {
        removeAll();
        JScrollPane scrollPane = PaintUtils.wrapWithScrollPanel(panel);
        setContent(scrollPane);
        revalidate();
        repaint();
    }
}
