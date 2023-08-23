package cn.xmirror.sca.ui.window;

import cn.xmirror.sca.common.dto.Origin;
import cn.xmirror.sca.common.pojo.DsnConfig;
import cn.xmirror.sca.ui.dialog.CancelOkDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.*;
import com.intellij.ui.table.JBTable;
import lombok.Getter;
import org.jdesktop.swingx.HorizontalLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.List;

/**
 * @author xingluheng
 * @date 2023/08/14 10:27
 **/
public class DsnListPanel extends JPanel {
    private static final int ACTIVE_COL_MIN_WIDTH = 40;
    private static final int ACTIVE_COL_MAX_WIDTH = 50;
    private static final int TYPE_COL_MIN_WIDTH = 50;
    private static final int TYPE_COL_MAX_WIDTH = 80;
    private static final Dimension DECORATOR_DIMENSIONS = new Dimension(300, 50);

    @Getter
    private DsnTableModel dsnTableModel;
    private JBTable dsnTable;
    private Project project;
    @Getter
    private JCheckBox localDataSourceCheckBox;

    public DsnListPanel(Project project,Boolean localDataSourceSelected, List<DsnConfig> dsnConfigList) {
        super(new BorderLayout());
        this.project = project;
        add(initCheckBoxPanel(localDataSourceSelected),BorderLayout.NORTH);
        add(initUi(dsnConfigList), BorderLayout.CENTER);
        localDataSourceCheckBox.addItemListener(e->{
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                dsnTableModel.getDsnConfigList().forEach(item->item.setSelect(false));
            }
        });
    }

    private JPanel initCheckBoxPanel(Boolean localDataSourceSelected){
        JPanel panel = new JPanel(new HorizontalLayout(20));
        localDataSourceCheckBox = new JCheckBox("Local Data Source");
        localDataSourceCheckBox.setSelected(localDataSourceSelected!=null?localDataSourceSelected:false);
        panel.add(localDataSourceCheckBox);
        return panel;
    }


    private Component initUi(List<DsnConfig> dsnConfigList) {
        dsnTableModel = new DsnTableModel(dsnConfigList,localDataSourceCheckBox);
        dsnTable =  new JBTable(dsnTableModel);
        setColumnWith(dsnTable, 0, ACTIVE_COL_MIN_WIDTH, ACTIVE_COL_MAX_WIDTH, ACTIVE_COL_MAX_WIDTH);
        setColumnWith(dsnTable, 1, TYPE_COL_MIN_WIDTH, TYPE_COL_MAX_WIDTH, TYPE_COL_MAX_WIDTH);
        setColumnWith(dsnTable, 2, TYPE_COL_MIN_WIDTH, TYPE_COL_MAX_WIDTH, TYPE_COL_MAX_WIDTH);
        dsnTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        dsnTable.setStriped(true);
        dsnTable.getTableHeader().setReorderingAllowed(true);
        final ToolbarDecorator tableDecorator = ToolbarDecorator.createDecorator(dsnTable);
        tableDecorator.setAddAction(new AddDsnAction());
        tableDecorator.setEditAction(new EditPropertiesAction());
        tableDecorator.setRemoveAction(new RemoveDsnAction());
        tableDecorator.setAddActionUpdater(e -> localDataSourceCheckBox.isSelected());
        tableDecorator.setEditActionUpdater(new EnableWhenSelected());
        tableDecorator.setRemoveActionUpdater(new EnableWhenSelectedAndRemovable());
        tableDecorator.setPreferredSize(DECORATOR_DIMENSIONS);

        final JPanel container = new JPanel(new BorderLayout());
        container.add(new TitledSeparator("Local Data Source Configuration"), BorderLayout.NORTH);
        container.add(tableDecorator.createPanel(), BorderLayout.CENTER);
        return container;
    }

    abstract static class ToolbarAction extends AbstractAction implements AnActionButtonRunnable {
        private static final long serialVersionUID = 7091312536206510956L;

        @Override
        public void run(final AnActionButton anActionButton) {
            actionPerformed(null);
        }
    }

    private final class AddDsnAction extends ToolbarAction {
        private static final long serialVersionUID = -7266120887003483814L;

        AddDsnAction() {
            putValue(Action.NAME, "add");
            putValue(Action.SHORT_DESCRIPTION, "Add a new configuration Data Source");
            putValue(Action.LONG_DESCRIPTION, "Add a new configuration  Data Source");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // 弹出一个新创窗口 添加数据源
            CancelOkDialog dialog = new CancelOkDialog(project, "添加数据源");
            dialog.show();
            if (dialog.isOK()) {
                if (dialog.getDataSourceComboBox().getSelectedItem() == null || dialog.getDataSourceComboBox().getSelectedItem() == Origin.originTypeList.get(0)) {
                    Messages.showErrorDialog("请选择数据源类型", "添加数据源");
                    return;
                }
                if (StringUtil.isEmpty(dialog.getDsnTextField().getText())) {
                    Messages.showErrorDialog("请配置数据源地址", "添加数据源");
                    return;
                }
                if (!dialog.getDataSourceComboBox().getItem().equals("JSON") && StringUtil.isEmpty(dialog.getTableNameTextField().getText())) {
                    Messages.showErrorDialog("请配置数据源表名称", "添加数据源");
                    return;
                }
                String type = dialog.getDataSourceComboBox().getSelectedItem().toString();
                dsnTableModel.addRow(new DsnConfig(false, type,dialog.getDsnTextField().getText() ,dialog.getTableNameTextField().getText()));
            }
        }
    }

    private final class EditPropertiesAction extends ToolbarAction {
        private static final long serialVersionUID = -7266120887003483814L;

        EditPropertiesAction() {
            putValue(Action.NAME, "edit");
            putValue(Action.SHORT_DESCRIPTION, "edit a new configuration Data Source");
            putValue(Action.LONG_DESCRIPTION, "edit a new configuration Data Source");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // 弹出窗口 编辑数据
            int selectedIndex = dsnTable.getSelectedRow();
            if (selectedIndex == -1) {
                return;
            }
            DsnConfig dsnConfig = dsnTableModel.getLocationAt(selectedIndex);
            CancelOkDialog dialog = new CancelOkDialog(project, "修改数据源");
            dialog.setDataSourceType(dsnConfig.getType());
            dialog.getDsnTextField().setText(dsnConfig.getDsn());
            dialog.getTableNameTextField().setText(dsnConfig.getTableName());
            dialog.show();

            if (dialog.isOK()) {
                if (dialog.getDataSourceComboBox().getSelectedItem() == null || dialog.getDataSourceComboBox().getSelectedItem() == Origin.originTypeList.get(0)) {
                    Messages.showErrorDialog("请选择数据源类型", "修改数据源");
                    return;
                }
                if (StringUtil.isEmpty(dialog.getDsnTextField().getText())) {
                    Messages.showErrorDialog("请配置数据源地址", "修改数据源");
                    return;
                }
                if (!dialog.getDataSourceComboBox().getItem().equals("JSON") && StringUtil.isEmpty(dialog.getTableNameTextField().getText())) {
                    Messages.showErrorDialog("请配置数据源表名称", "修改数据源");
                    return;
                }
                String type = dialog.getDataSourceComboBox().getSelectedItem().toString();
                DsnConfig newDsnConfig = new DsnConfig(dsnConfig.getSelect(), type, dialog.getDsnTextField().getText(),dialog.getTableNameTextField().getText());
                dsnTableModel.updateLocation(dsnConfig,newDsnConfig);
            }

        }
    }

    private final class RemoveDsnAction extends ToolbarAction {
        private static final long serialVersionUID = -7266120887003483814L;

        RemoveDsnAction() {
            putValue(Action.NAME, "remove");
            putValue(Action.SHORT_DESCRIPTION, "remove a new configuration Data Source");
            putValue(Action.LONG_DESCRIPTION, "remove a new configuration Data Source");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // 集合列表-1
            final int selectedIndex = dsnTable.getSelectedRow();
            if (selectedIndex == -1) {
                return;
            }
            dsnTableModel.removeLocationAt(selectedIndex);
        }
    }

    private class EnableWhenSelectedAndRemovable implements AnActionButtonUpdater {
        @Override
        public boolean isEnabled(@NotNull final AnActionEvent e) {
            final int selectedItem = dsnTable.getSelectedRow();
            return localDataSourceCheckBox.isSelected() && selectedItem >= 0;
        }
    }

    private class EnableWhenSelected implements AnActionButtonUpdater {
        @Override
        public boolean isEnabled(@NotNull final AnActionEvent e) {
            final int selectedItem = dsnTable.getSelectedRow();
            return localDataSourceCheckBox.isSelected() && selectedItem >= 0;
        }
    }


    private void setColumnWith(final JTable table,
                               final int columnIndex,
                               final int minSize,
                               final int preferredSize,
                               final Integer maxSize) {
        final TableColumn column = table.getColumnModel().getColumn(columnIndex);
        column.setMinWidth(minSize);
        column.setWidth(preferredSize);
        column.setPreferredWidth(preferredSize);
        if (maxSize != null) {
            column.setMaxWidth(maxSize);
        }
    }

}
