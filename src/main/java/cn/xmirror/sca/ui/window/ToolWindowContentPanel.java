package cn.xmirror.sca.ui.window;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.OnePixelSplitter;

import javax.swing.*;
import java.awt.*;

/**
 * 主面板中的内容面板
 */
public class ToolWindowContentPanel extends JPanel implements Disposable {

    private static final String TOOL_WINDOW_SPLITTER_PROPORTION_KEY = "XMIRROR_TOOL_WINDOW_SPLITTER_PROPORTION";

    // 概览面板
    private OverviewPanel overviewPanel;

    // 描述面板
    private DescriptionPanel descriptionPanel;

    public ToolWindowContentPanel(Project project) {
        super(new BorderLayout());
        overviewPanel = new OverviewPanel(project, this);
        descriptionPanel = new DescriptionPanel(project, this);
        OnePixelSplitter vulnerabilitiesSplitter = new OnePixelSplitter(TOOL_WINDOW_SPLITTER_PROPORTION_KEY, 0.4f);
        vulnerabilitiesSplitter.setFirstComponent(overviewPanel);
        vulnerabilitiesSplitter.setSecondComponent(descriptionPanel);
        add(vulnerabilitiesSplitter, BorderLayout.CENTER);
    }

    public DescriptionPanel getDescriptionPanel() {
        return descriptionPanel;
    }

    public OverviewPanel getOverviewPanel() {
        return overviewPanel;
    }

    @Override
    public void dispose() {

    }
}
