package cn.xmirror.sca.ui.window;

import cn.xmirror.sca.common.ResultParser;
import cn.xmirror.sca.common.constant.SecurityLevelEnum;
import cn.xmirror.sca.common.dto.Component;
import cn.xmirror.sca.common.dto.FilePath;
import cn.xmirror.sca.common.dto.Overview;
import cn.xmirror.sca.common.dto.Vulnerability;
import cn.xmirror.sca.engine.EngineAssistant;
import cn.xmirror.sca.service.CheckService;
import cn.xmirror.sca.ui.dialog.SyncResultDialog;
import cn.xmirror.sca.ui.window.tree.ComponentTreeNode;
import cn.xmirror.sca.ui.window.tree.FilePathTreeNode;
import cn.xmirror.sca.ui.window.tree.RootTreeNode;
import cn.xmirror.sca.ui.window.tree.VulnerabilityTreeNode;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.Alarm;
import icons.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OverviewPanel extends SimpleToolWindowPanel implements Disposable {

    private Project project;
    private ToolWindowContentPanel contentPanel;

    private Tree tree;
    private DefaultMutableTreeNode rootNode;
    private Alarm scrollPanelAlarm = new Alarm();

    private boolean displayingCritical = true;
    private boolean displayingHigh = true;
    private boolean displayingMedium = true;
    private boolean displayingLow = true;

    public OverviewPanel(Project project, ToolWindowContentPanel contentPanel) {
        super(true, true);
        this.project = project;
        this.contentPanel = contentPanel;
        generateTree();
        setToolbar(levelFilterBar());
        tree.getEmptyText().setText("Run the detection and check the results here");
        setContent(PaintUtils.wrapWithScrollPanel(tree));
    }

    private @NotNull JPanel levelFilterBar() {
        JPanel severityToolbarPanel = new JPanel(new BorderLayout());
        severityToolbarPanel.add(new JLabel(" Severity: "), BorderLayout.WEST);
        ActionManager actionManager = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) actionManager.getAction("cn.xmirror.sca.LevelFilterBar");
        JComponent component = new ActionToolbarImpl("OpenSCA", actionGroup, true);
        severityToolbarPanel.add(component, BorderLayout.CENTER);
        return severityToolbarPanel;
    }

    private void generateTree() {
        rootNode = new DefaultMutableTreeNode();
        // 指定一个跟节点创建树
        tree = new Tree(rootNode);
        tree.setRootVisible(false);
        // 设置树组件节点渲染器
        tree.setCellRenderer(new TreeCellRenderer());
        // 树的选择事件
        tree.addTreeSelectionListener(e -> ApplicationManager.getApplication().invokeLater(this::updateDescriptionPanelBySelectedTreeNode));
        // 树的右键选择事件
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleRightClickEvent(e);
            }
        });
    }


    /**
     * 处理组件漏洞树右键监听事件
     *
     * @param e
     */
    private void handleRightClickEvent(MouseEvent e) {
        JBPopupMenu jbPopupMenu = new JBPopupMenu();
        jbPopupMenu.setBorder(BorderFactory.createEmptyBorder());

        if (SwingUtilities.isRightMouseButton(e)) {
            TreePath selectionPath = tree.getSelectionPath();
            if (selectionPath != null) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
                // 可以根据treeNode.getChildCount()是否大于0判断 组件OR漏洞
                JBMenuItem uploadSaas = new JBMenuItem("Upload Saas", Icons.UPLOAD_SAAS);
                uploadSaas.addActionListener(actionEvent -> {
                    jbPopupMenu.add(uploadSaas);
                    SyncResultDialog syncResultDialog = new SyncResultDialog(project);
                    syncResultDialog.show();
                });
                jbPopupMenu.add(uploadSaas);
            }
            jbPopupMenu.show(tree, e.getX(), e.getY() + 10); // 显示弹出菜单
        }
    }

    private void updateDescriptionPanelBySelectedTreeNode() {
        DescriptionPanel descriptionPanel = contentPanel.getDescriptionPanel();
        TreePath selectionPath = tree.getSelectionPath();
        if (Objects.nonNull(selectionPath)) {
            // 返回路径中最后的一个元素
            DefaultMutableTreeNode value = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
            if (value instanceof ComponentTreeNode) {
                // 组件节点
                ComponentTreeNode node = (ComponentTreeNode) value;
                // 返回用户在该结点存储的对象
                Component component = (Component) node.getUserObject();
                DescriptionComponentPanel desPanel = new DescriptionComponentPanel(component, project);
                // 设置描述面板中的内容
                descriptionPanel.setDetailDescriptionPanel(desPanel);
            } else if (value instanceof VulnerabilityTreeNode) {
                // 漏洞树节点
                VulnerabilityTreeNode node = (VulnerabilityTreeNode) value;
                Vulnerability vulnerability = (Vulnerability) node.getUserObject();
                DescriptionVulnerabilityPanel desPanel = new DescriptionVulnerabilityPanel(vulnerability);
                descriptionPanel.setDetailDescriptionPanel(desPanel);
            } else if (value instanceof RootTreeNode) {
                // 根节点
                Object rootValue = value.getUserObject();
                Overview rootUserObject = (Overview) rootValue;
                DescriptionOverviewPanel desPanel = new DescriptionOverviewPanel(tree, descriptionPanel, this, rootUserObject, project);
                descriptionPanel.setDetailDescriptionPanel(desPanel);
            }
        }
    }

    /**
     * 重新渲染根节点的内容
     *
     * @param root
     */
    public void render(MutableTreeNode root) {
        rootNode.removeAllChildren();
        if (root != null) {
            rootNode.add(root);
        }
        // 展示根节点内容
        tree.getSelectionModel().clearSelection();
        // 展开根节点
        TreePath treePath = new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(rootNode));
        expandNode(tree, rootNode, treePath, 3);
        // 刷新树
        SwingUtilities.invokeLater(() -> tree.updateUI());
    }


    public Tree getTree() {
        return tree;
    }

    public DefaultMutableTreeNode getRootNode() {
        return rootNode;
    }

    /**
     * Refresh the displayed results based on the current filter settings.
     */
    public void filterDisplayedResults() {
        final List<Integer> severityLevels = new ArrayList<>();
        if (displayingCritical) {
            severityLevels.add(SecurityLevelEnum.CRITICAL.getLevel());
        }
        if (displayingHigh) {
            severityLevels.add(SecurityLevelEnum.HIGH.getLevel());
        }
        if (displayingMedium) {
            severityLevels.add(SecurityLevelEnum.MEDIUM.getLevel());
        }
        if (displayingLow) {
            severityLevels.add(SecurityLevelEnum.LOW.getLevel());
        }
        String outputPath = EngineAssistant.getCheckResultJsonPath(project);
        if (rootNode.getChildCount() == 0) {
            return;
        }
        // 获取当前树的根节点
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) this.rootNode.getChildAt(0);
        // 转换为树结点的对象
        Overview overview = (Overview) defaultMutableTreeNode.getUserObject();
        // 重新解析结果数 根据风险等级筛选
        MutableTreeNode rootNode = ResultParser.parseResult(outputPath, overview);
        // 重新渲染的根结点
        RootTreeNode rootTreeNode = new RootTreeNode(overview);
        // 遍历循环根结点下子结点 递归获取子结点的的对象 判断风险等级 进行筛选
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            DefaultMutableTreeNode fileNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
            FilePath filePath = (FilePath) fileNode.getUserObject();
            String newFilePath = filePath.getPath().replace(Objects.requireNonNullElse(CheckService.PROJECT_BASE_PATH, ""), "");
            FilePathTreeNode filePathTreeNode = new FilePathTreeNode(new FilePath(newFilePath));
            for (int i1 = 0; i1 < fileNode.getChildCount(); i1++) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) fileNode.getChildAt(i1);
                Component component = (Component) childNode.getUserObject();
                if (severityLevels.contains(component.getSecurityLevelId())) {
                    ComponentTreeNode componentTreeNode = new ComponentTreeNode(component);
                    for (int i2 = 0; i2 < childNode.getChildCount(); i2++) {
                        DefaultMutableTreeNode c3 = (DefaultMutableTreeNode) childNode.getChildAt(i2);
                        Vulnerability vulnerability = (Vulnerability) c3.getUserObject();
                        if (severityLevels.contains(vulnerability.getSecurityLevelId())) {
                            VulnerabilityTreeNode vulnerabilityTreeNode = new VulnerabilityTreeNode(vulnerability);
                            componentTreeNode.add(vulnerabilityTreeNode);
                        }
                    }
                    filePathTreeNode.add(componentTreeNode);
                }
            }
            rootTreeNode.add(filePathTreeNode);
        }
        render(rootTreeNode);
    }

    /**
     * 展开所有结果
     */
    public void expandAll(Integer level) {
        if (tree != null && rootNode != null) {
            TreePath treePath = new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(rootNode));
            expandNode(tree, rootNode, treePath, level);
        }
    }

    /**
     * 收起所有结果
     */
    public void collapseAll() {
        if (tree != null) {
            for (int i = 1; i < tree.getRowCount(); ++i) {
                tree.collapseRow(i);
            }
        }
    }


    @Override
    public void dispose() {

    }

    /**
     * Expand the given tree to the given level, starting from the given node
     * and path.
     *
     * @param tree  The tree to be expanded
     * @param node  The node to start from
     * @param path  The path to start from
     * @param level The number of levels to expand to
     */
    private static void expandNode(final JTree tree,
                                   final TreeNode node,
                                   final TreePath path,
                                   final int level) {
        if (level <= 0) {
            return;
        }

        tree.expandPath(path);

        for (int i = 0; i < node.getChildCount(); ++i) {
            final TreeNode childNode = node.getChildAt(i);
            expandNode(tree, childNode, path.pathByAddingChild(childNode), level - 1);
        }
    }

    public boolean isDisplayingCritical() {
        return displayingCritical;
    }

    public void setDisplayingCritical(boolean displayingCritical) {
        this.displayingCritical = displayingCritical;
    }

    public boolean isDisplayingHigh() {
        return displayingHigh;
    }

    public void setDisplayingHigh(boolean displayingHigh) {
        this.displayingHigh = displayingHigh;
    }

    public boolean isDisplayingMedium() {
        return displayingMedium;
    }

    public void setDisplayingMedium(boolean displayingMedium) {
        this.displayingMedium = displayingMedium;
    }

    public boolean isDisplayingLow() {
        return displayingLow;
    }

    public void setDisplayingLow(boolean displayingLow) {
        this.displayingLow = displayingLow;
    }
}
