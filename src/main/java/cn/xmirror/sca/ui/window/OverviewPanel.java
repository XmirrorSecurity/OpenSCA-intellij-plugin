package cn.xmirror.sca.ui.window;

import cn.xmirror.sca.common.dto.Component;
import cn.xmirror.sca.common.dto.Overview;
import cn.xmirror.sca.common.dto.Vulnerability;
import cn.xmirror.sca.ui.window.tree.ComponentTreeNode;
import cn.xmirror.sca.ui.window.tree.RootTreeNode;
import cn.xmirror.sca.ui.window.tree.VulnerabilityTreeNode;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.Alarm;

import javax.swing.*;
import javax.swing.tree.*;
import java.util.Objects;

public class OverviewPanel extends SimpleToolWindowPanel implements Disposable {

    private Project project;
    private ToolWindowContentPanel contentPanel;

    private Tree tree;
    private DefaultMutableTreeNode rootNode;
    private Alarm scrollPanelAlarm = new Alarm();

    public OverviewPanel(Project project, ToolWindowContentPanel contentPanel) {
        super(true, true);
        this.project = project;
        this.contentPanel = contentPanel;
        generateTree();
        setContent(PaintUtils.wrapWithScrollPanel(tree));
    }

    private void generateTree() {
        rootNode = new RootTreeNode(new Overview());
        tree = new Tree(rootNode);
        tree.setRootVisible(false);
        tree.setCellRenderer(new TreeCellRenderer());
        tree.addTreeSelectionListener(e -> ApplicationManager.getApplication().invokeLater(this::updateDescriptionPanelBySelectedTreeNode));
    }

    private void updateDescriptionPanelBySelectedTreeNode() {
        DescriptionPanel descriptionPanel = contentPanel.getDescriptionPanel();
        TreePath selectionPath = tree.getSelectionPath();
        if (Objects.nonNull(selectionPath)) {
            DefaultMutableTreeNode value = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
            if (value instanceof ComponentTreeNode) {
                ComponentTreeNode node = (ComponentTreeNode) value;
                Component component = (Component) node.getUserObject();
                DescriptionComponentPanel desPanel = new DescriptionComponentPanel(component, project);
                descriptionPanel.setDetailDescriptionPanel(desPanel);
            } else if (value instanceof VulnerabilityTreeNode) {
                VulnerabilityTreeNode node = (VulnerabilityTreeNode) value;
                Vulnerability vulnerability = (Vulnerability) node.getUserObject();
                DescriptionVulnerabilityPanel desPanel = new DescriptionVulnerabilityPanel(vulnerability);
                descriptionPanel.setDetailDescriptionPanel(desPanel);
            } else if (value instanceof RootTreeNode) {
                Object rootValue = value.getUserObject();
                Overview rootUserObject = (Overview) rootValue;
                DescriptionOverviewPanel desPanel = new DescriptionOverviewPanel(tree, descriptionPanel, this, rootUserObject, project);
                descriptionPanel.setDetailDescriptionPanel(desPanel);
            }
        }
    }

    public void render(MutableTreeNode root) {
        rootNode.removeAllChildren();
        MutableTreeNode node = rootNode;
        if (root != null) {
            rootNode.add(root);
            node = root;
        }
        // 展示根节点内容
        tree.getSelectionModel().clearSelection();
        tree.setSelectionPath(new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(node)));
        // 刷新树
        SwingUtilities.invokeLater(() -> tree.updateUI());
        // 展开根节点
        tree.expandPath(new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(node)));
    }

    public Tree getTree() {
        return tree;
    }

    public DefaultMutableTreeNode getRootNode() {
        return rootNode;
    }

    @Override
    public void dispose() {

    }
}
