package cn.xmirror.sca.ui.window;

import com.intellij.uiDesigner.core.GridConstraints;

public class MyGridConstraints {
    private final GridConstraints gridConstraints;

    public MyGridConstraints(int row) {
        gridConstraints = new GridConstraints(
                row,
                0,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null,
                null,
                null,
                1,
                false);
    }

    public static MyGridConstraints gridBuilder(int row) {
        return new MyGridConstraints(row);
    }

    public GridConstraints build() {
        return gridConstraints;
    }

    public MyGridConstraints setRow(int row) {
        gridConstraints.setRow(row);
        return this;
    }

    public MyGridConstraints setColumn(int column) {
        gridConstraints.setColumn(column);
        return this;
    }

    public MyGridConstraints setRowSpan(int rowSpan) {
        gridConstraints.setRowSpan(rowSpan);
        return this;
    }

    public MyGridConstraints setColSpan(int colSpan) {
        gridConstraints.setColSpan(colSpan);
        return this;
    }

    public MyGridConstraints setAnchor(int anchor) {
        gridConstraints.setAnchor(anchor);
        return this;
    }

    public MyGridConstraints setFill(int fill) {
        gridConstraints.setFill(fill);
        return this;
    }

    /**
     * 内部组件水平方向间距
     * @param HSizePolicy 间距
     */
    public MyGridConstraints setHSizePolicy(int HSizePolicy) {
        gridConstraints.setHSizePolicy(HSizePolicy);
        return this;
    }

    /**
     * 内部组件垂直方向间距
     * @param VSizePolicy 间距
     */
    public MyGridConstraints setVSizePolicy(int VSizePolicy) {
        gridConstraints.setVSizePolicy(VSizePolicy);
        return this;
    }

    /**
     * 首行缩进
     * @param indent 0：不缩进 1：缩进
     */
    public MyGridConstraints setIndent(int indent) {
        gridConstraints.setIndent(indent);
        return this;
    }
}
