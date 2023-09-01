package cn.xmirror.sca.ui.window;

import cn.xmirror.sca.common.dto.Component;
import cn.xmirror.sca.common.dto.Overview;
import cn.xmirror.sca.common.dto.Vulnerability;
import cn.xmirror.sca.ui.window.tree.ComponentTreeNode;
import cn.xmirror.sca.ui.window.tree.RootTreeNode;
import cn.xmirror.sca.ui.window.tree.VulnerabilityTreeNode;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.ui.UIUtil;
import icons.Icons;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class TreeCellRenderer extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(
            @NotNull JTree tree,
            Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        Icon nodeIcon = null;
        String text = null;
        SimpleTextAttributes attributes = SimpleTextAttributes.REGULAR_ATTRIBUTES;
        if (value instanceof ComponentTreeNode) {
            ComponentTreeNode node = (ComponentTreeNode) value;
            Component component = (Component) node.getUserObject();
            nodeIcon = Icons.getIconFromResources(component.getSecurityLevelId());
            text = component.getName() + ":" + component.getVersion();
        } else if (value instanceof VulnerabilityTreeNode) {
            VulnerabilityTreeNode node = (VulnerabilityTreeNode) value;
            Vulnerability vulnerability = (Vulnerability) node.getUserObject();
            nodeIcon = Icons.getIconFromResources(vulnerability.getSecurityLevelId());
            text = vulnerability.getName();
        } else if (value instanceof RootTreeNode) {
            RootTreeNode node = (RootTreeNode) value;
            Object userObject = node.getUserObject();
            Overview rootUserObject = (Overview) userObject;
            nodeIcon = Icons.OPEN_SCA_LOGO;
            text = "OpenSCA";
            attributes = SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES;
        } else {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            // 结果树的顶层根节点为空
            if (StringUtils.isEmpty(value.toString())) {
                return;
            }else {
                text = node.getUserObject().toString();
            }
        }

        setIcon(nodeIcon);
        setFont(UIUtil.getTreeFont());
        if (text != null) {
            append(text, attributes);
        }
    }
}
