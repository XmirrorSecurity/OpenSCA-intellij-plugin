package cn.xmirror.sca.ui.dialog;

import cn.xmirror.sca.common.dto.Origin;
import cn.xmirror.sca.ui.window.MyGridConstraints;
import com.intellij.ide.HelpTooltip;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBTextField;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 添加数据源
 *
 * @author xingluheng
 * @date 2023/08/14 11:49
 **/
public class CancelOkDialog extends DialogWrapper {
    private ComboBox<String> dataSourceComboBox;
    private JBTextField tableNameTextField;
    private JTextArea dsnTextField;

    public CancelOkDialog(@Nullable Project project, String title) {
        super(project, false);
        init();
        setTitle(title);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridLayoutManager(4, 2));
        panel.setPreferredSize(new Dimension(500, 120));
        List<String> originTypeList = Origin.originTypeList;
        dataSourceComboBox = new ComboBox<String>(originTypeList.toArray(new String[originTypeList.size()]));
        tableNameTextField = new JBTextField();
        dsnTextField = new JTextArea();
        dsnTextField.setBorder(tableNameTextField.getBorder());
        dsnTextField.setSize(tableNameTextField.getSize());
        panel.add(new JLabel("Type:"), new MyGridConstraints(0).setAnchor(GridConstraints.ANCHOR_WEST).build());
        panel.add(dataSourceComboBox, new MyGridConstraints(0).setColumn(1).setFill(GridConstraints.FILL_HORIZONTAL)
                .setHSizePolicy(GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK).build());
        panel.add(new JLabel("Data Source:"), new MyGridConstraints(1).setColumn(0).setAnchor(GridConstraints.ANCHOR_WEST).build());
        panel.add(dsnTextField, new MyGridConstraints(1).setColumn(1).setFill(GridConstraints.FILL_HORIZONTAL)
                .setHSizePolicy(GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK).build());
        JLabel tableLabel = new JLabel("Table Name:");
        panel.add(tableLabel, new MyGridConstraints(3).setAnchor(GridConstraints.ANCHOR_WEST).build());
        panel.add(tableNameTextField, new MyGridConstraints(3).setColumn(1).setFill(GridBagConstraints.BOTH).build());

        dataSourceComboBox.addItemListener(e -> {
            String selectedValue = (String) dataSourceComboBox.getSelectedItem();
            if ("JSON".equals(selectedValue)) {
                HelpTooltip dataSourceHelpTooltip = new HelpTooltip();
                dataSourceHelpTooltip.setDescription("数据源配置，如:/Users/xingluheng/Desktop/origin.sql");
                dataSourceHelpTooltip.installOn(dsnTextField);
                tableLabel.setVisible(false);
                tableNameTextField.setVisible(false);
            } else {
                HelpTooltip dataSourceHelpTooltip = new HelpTooltip();
                dataSourceHelpTooltip.setDescription("数据源配置，如:root:123456@tcp(127.0.0.1:3306)/db");
                dataSourceHelpTooltip.installOn(dsnTextField);
                tableLabel.setVisible(true);
                tableNameTextField.setVisible(true);
            }
        });
        return panel;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
    }

    public void setDataSourceType(String item) {
        dataSourceComboBox.setSelectedItem(item);
    }

    public ComboBox<String> getDataSourceComboBox() {
        return dataSourceComboBox;
    }

    public JTextArea getDsnTextField() {
        return dsnTextField;
    }

    public JBTextField getTableNameTextField() {
        return tableNameTextField;
    }
}
