package cn.xmirror.sca.ui.window;

import cn.xmirror.sca.common.pojo.DsnConfig;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xingluheng
 * @date 2023/08/14 10:14
 **/
public class DsnTableModel extends AbstractTableModel {

    @Getter
    private List<DsnConfig> dsnConfigList = new ArrayList<>();
    private final String[] columnNames = {" ", "Type", "Table","Data Source"};
    private JCheckBox localDataSourceCheckBox;


    public DsnTableModel() {
        dsnConfigList.add(new DsnConfig(false, "MySQL", "test","jdbc:mysql://localhost:3306/mydatabase?user=username&password=password&useSSL=false"));
        dsnConfigList.add(new DsnConfig(true, "Redis", "test","jdbc:mysql://localhost:3306/mydatabase?user=username&password=password&useSSL=false"));
    }

    public DsnTableModel(List<DsnConfig> dsnConfigList,JCheckBox localDataSourceCheckBox) {
        this.localDataSourceCheckBox = localDataSourceCheckBox;
        if (!CollectionUtils.isEmpty(dsnConfigList)) {
            this.dsnConfigList = dsnConfigList;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    public void addRow(DsnConfig rowData) {
        dsnConfigList.add(rowData);
        fireTableRowsInserted(dsnConfigList.size() - 1, dsnConfigList.size() - 1);
    }

    public void removeRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < dsnConfigList.size()) {
            dsnConfigList.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    public DsnConfig getLocationAt(final int index) {
        return dsnConfigList.get(index);
    }

    @Override
    public int getRowCount() {
        return dsnConfigList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DsnConfig data = dsnConfigList.get(rowIndex);
        switch (columnIndex) {
            case 1:
                return data.getType();
            case 2:
                return data.getTableName();
            case 3:
                return data.getDsn();
            case 0:
                return data.getSelect();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        DsnConfig dsnConfig = dsnConfigList.get(rowIndex);
        if (columnIndex == 0 && localDataSourceCheckBox.isSelected()) {
            dsnConfig.setSelect(!dsnConfig.getSelect());
            fireTableCellUpdated(rowIndex, 0);
        }
    }

    public void removeLocationAt(int selectedIndex) {
        removeRow(selectedIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    public void updateLocation(DsnConfig dsnConfig, DsnConfig newDsnConfig) {
        if (dsnConfig != null && newDsnConfig != null) {
            final int index = dsnConfigList.indexOf(dsnConfig);
            if (index != -1) {
                dsnConfigList.remove(index);
                dsnConfigList.add(index, newDsnConfig);
                fireTableRowsUpdated(index, index);
            }
        }
    }
}
