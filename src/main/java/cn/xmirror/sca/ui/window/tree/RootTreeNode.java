package cn.xmirror.sca.ui.window.tree;


import cn.xmirror.sca.common.dto.Overview;

import javax.swing.tree.DefaultMutableTreeNode;

public class RootTreeNode extends DefaultMutableTreeNode {
    public RootTreeNode(Overview o) {
        super(o);
    }
}
