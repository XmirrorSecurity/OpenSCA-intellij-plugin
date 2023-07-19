package cn.xmirror.sca.ui.window;

import cn.xmirror.sca.common.CheckListener;
import cn.xmirror.sca.ui.Notification;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.SimpleToolWindowPanel;

import javax.swing.*;
import javax.swing.tree.MutableTreeNode;
import java.awt.*;

/**
 * 主窗口。组合 窗口工具栏 和 窗口内容面板
 *
 * @author Yuan Shengjun
 */
public class ToolWindowMainPanel extends JPanel implements Disposable, CheckListener {

    private JProgressBar progressBar;
    private ToolWindowContentPanel contentPanel;
    private final Project project;

    public ToolWindowMainPanel(Project project) {
        super(new BorderLayout());
        this.project = project;
        initPanel();
    }

    public ToolWindowContentPanel getContentPanel() {
        return contentPanel;
    }

    private void initPanel() {
        // 窗口内容面板
        contentPanel = new ToolWindowContentPanel(project);
        // 窗口工具栏
        ActionManager actionManager = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) actionManager.getAction("cn.xmirror.sca.ActionBar");
        ActionToolbar actionToolbar = actionManager.createActionToolbar("Xmirror Toolbar", actionGroup, false);
        actionToolbar.setTargetComponent(contentPanel);
        initializeToolbar();

        SimpleToolWindowPanel mainPanel = new SimpleToolWindowPanel(false, true);
        mainPanel.setToolbar(actionToolbar.getComponent());
        mainPanel.setContent(contentPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void initializeToolbar() {
//        project.getMessageBus().connect(this).subscribe(Topic.create("Xmirror Scan",));
    }

    @Override
    public void clean() {
        contentPanel.getOverviewPanel().render(null);
    }

    @Override
    public void progress(boolean running) {
        if (running) {
            if (progressBar == null) {
                progressBar = new JProgressBar();
                progressBar.setIndeterminate(true);
            }
            add(progressBar, BorderLayout.NORTH);
        } else {
            remove(progressBar);
        }
        updateUI();
    }

    @Override
    public void onSuccess(MutableTreeNode resultTree) {
        contentPanel.getOverviewPanel().render(resultTree);
    }

    @Override
    public void onError(Exception e) {
        Notification.balloonNotify(e.getMessage(), MessageType.ERROR);
    }

    @Override
    public void dispose() {

    }
}
