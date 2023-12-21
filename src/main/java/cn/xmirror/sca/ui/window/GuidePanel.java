package cn.xmirror.sca.ui.window;

import cn.xmirror.sca.ui.NotificationUtils;
import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.Spacer;
import icons.Icons;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;

public class GuidePanel extends JPanel {
    private Project project;
    private OverviewPanel overviewPanel;

    public GuidePanel(Project project, OverviewPanel overviewPanel) {
        super(PaintUtils.defaultMarginGridLayoutManager(10,1));
        this.project = project;
        this.overviewPanel = overviewPanel;
        this.add(getTitlePanel(), PaintUtils.panelGridConstraints(0));
        this.add(getSettingPanel(), PaintUtils.panelGridConstraints(1));
        this.add(getRunPanel(), PaintUtils.panelGridConstraints(2));
        this.add(getStopPanel(), PaintUtils.panelGridConstraints(3));
        this.add(getCleanPanel(), PaintUtils.panelGridConstraints(4));
        this.add(new Spacer(), PaintUtils.spacerGridConstraints(9));
    }

    private JPanel getTitlePanel() {
        JPanel panel = PaintUtils.defaultGridPanel(2,1);

        Icon titleIcon = Icons.XMIRROR_LOGO_24;
        String titleText = "OpenSCA Xcheck组件漏洞检测工具";
        Font titleFont = PaintUtils.primaryTitleFont();
        JLabel titleLabel = new JLabel();
        titleLabel.setFont(titleFont);
        titleLabel.setText(titleText);
        titleLabel.setIcon(titleIcon);
        titleLabel.setForeground(JBColor.BLUE);

        panel.add(titleLabel, MyGridConstraints.gridBuilder(0).build());
        panel.add(titleDescription(),
                MyGridConstraints
                        .gridBuilder(1)
                        .setFill(GridConstraints.FILL_HORIZONTAL)
                        .setHSizePolicy(GridConstraints.SIZEPOLICY_CAN_GROW)
                        .build());
        return panel;
    }

    private JPanel titleDescription() {
        JPanel panel = PaintUtils.defaultGridPanel(1, 2);
        String descriptionText = "本工具实时检测项目的组件及漏洞信息，开始使用前请先配置检测平台地址。";
        JLabel descriptionLabel = new JLabel();
        descriptionLabel.setText(descriptionText);
        JButton checkDescriptionButton = new JButton("检测概览");
        checkDescriptionButton.addActionListener(e -> triggerRootNode());
        panel.add(descriptionLabel, MyGridConstraints.gridBuilder(0).build());
        panel.add(checkDescriptionButton,
                MyGridConstraints
                        .gridBuilder(0)
                        .setColumn(1)
                        .setAnchor(GridConstraints.ANCHOR_EAST)
                        .setHSizePolicy(GridConstraints.SIZEPOLICY_CAN_GROW)
                        .build());
        return panel;
    }

    /**
     * 触发树中的根节点
     */
    public void triggerRootNode() {
        DefaultMutableTreeNode rootNode = overviewPanel.getRootNode();
        Tree tree = overviewPanel.getTree();
        tree.getSelectionModel().clearSelection();
        if (rootNode.getChildCount() != 0) {
            TreePath treePath = new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(rootNode.getFirstChild()));
            tree.setSelectionPath(treePath);
        }else {
            NotificationUtils.balloonNotify("请检测完成后,查看检测结果", NotificationType.WARNING);
        }
    }

    private JPanel getSettingPanel() {
        Icon titleIcon = AllIcons.General.Settings;
        String titleText = "连接配置";
        String descriptionText = "点击工具栏的“Setting”按钮，点击“Quick Authentication”按钮，跳转平台授权后，自动填充Url,Token！";
        return getDefaultPanel(titleIcon, titleText, descriptionText);
    }

    private JPanel getRunPanel() {
        Icon titleIcon = AllIcons.Actions.Execute;
        String titleText = "开始检测";
        String descriptionText = "点击工具栏的“Run”，开始检测当前项目内的组件漏洞风险情况。";
        return getDefaultPanel(titleIcon, titleText, descriptionText);
    }

    private JPanel getStopPanel() {
        Icon titleIcon = AllIcons.Actions.Suspend;
        String titleText = "停止检测";
        String descriptionText = "点击工具栏的“Stop”，停止检测当前项目内的组件漏洞风险情况。";
        return getDefaultPanel(titleIcon, titleText, descriptionText);
    }

    private JPanel getCleanPanel() {
        Icon titleIcon = AllIcons.Actions.GC;
        String titleText = "清除检测结果";
        String descriptionText = "点击工具栏的“Clean”，清除当前项目的检测结果。";
        return getDefaultPanel(titleIcon, titleText, descriptionText);
    }

    private JPanel getDefaultPanel(Icon titleIcon, String titleText, String descriptionText) {
        Font defaultTitleFont = PaintUtils.secondaryTitleFont();
        return getDefaultPanel(titleIcon, titleText, defaultTitleFont, descriptionText);
    }

    private JPanel getDefaultPanel(Icon titleIcon, String titleText, Font titleFont, String descriptionText) {
        JPanel panel = PaintUtils.defaultGridPanel(2,1);

        JLabel titleLabel = new JLabel();
        titleLabel.setFont(titleFont);
        titleLabel.setText(titleText);
        titleLabel.setIcon(titleIcon);

        JLabel descriptionLabel = new JLabel();
        descriptionLabel.setText("    " + descriptionText);

        panel.add(titleLabel, MyGridConstraints.gridBuilder(0).build());
        panel.add(descriptionLabel, MyGridConstraints.gridBuilder(1).build());
        return panel;
    }
}
