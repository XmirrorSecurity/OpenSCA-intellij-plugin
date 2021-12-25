package cn.xmirror.sca.ui.window.tree;


import cn.xmirror.sca.common.dto.Component;

import javax.swing.tree.DefaultMutableTreeNode;

public class ComponentTreeNode extends DefaultMutableTreeNode {
    public ComponentTreeNode(Component o) {
        super(o);
    }
}
