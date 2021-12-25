package cn.xmirror.sca.common;

import javax.swing.tree.MutableTreeNode;

/**
 * 检查监听器
 *
 * @author Yuan Shengjun
 */
public interface CheckListener {

    void progress(boolean running);

    void onSuccess(MutableTreeNode resultTree);

    void onError(Exception e);

    void clean();
}
