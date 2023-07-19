package cn.xmirror.sca.ui.window.tree;

import cn.xmirror.sca.common.dto.FilePath;
import com.intellij.openapi.project.Project;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author xingluheng
 * @date 2023/07/18 16:21
 **/
public class FilePathTreeNode extends DefaultMutableTreeNode {
    public FilePathTreeNode(FilePath filePath){
        super(filePath);
    }
}
