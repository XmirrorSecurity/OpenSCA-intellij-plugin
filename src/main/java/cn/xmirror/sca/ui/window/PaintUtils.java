package cn.xmirror.sca.ui.window;

import cn.xmirror.sca.common.OpenSCASettingState;
import cn.xmirror.sca.ui.Notification;
import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

public class PaintUtils {
    private PaintUtils() {
    }

    // ---------------------------------- JPanel ------------------------------------
    public static int PANEL_V_GAP = 5;
    public static int PANEL_H_GAP = 5;

    public static JPanel defaultGridPanel(int rowCount, int columnCount) {
        return defaultGridPanel(rowCount, columnCount, PANEL_H_GAP, PANEL_V_GAP);
    }

    public static JPanel defaultGridPanel(int rowCount, int columnCount, int hGap, int vGap) {
        return new JPanel(new GridLayoutManager(rowCount, columnCount, JBUI.emptyInsets(), hGap, vGap));
    }


    // ------------------------------- LayoutManager --------------------------------

    /**
     * 布局管理器
     *
     * @param rowCount    行数
     * @param columnCount 列数
     * @return 布局管理器
     */
    public static GridLayoutManager defaultMarginGridLayoutManager(int rowCount, int columnCount) {
        return new GridLayoutManager(rowCount, columnCount, JBUI.insets(10, 10, 10, 10), PANEL_H_GAP, PANEL_V_GAP);
    }

    // ------------------------------ GridConstraints --------------------------------
    public static GridConstraints spacerGridConstraints(int row) {
        return new MyGridConstraints(row)
                .setAnchor(GridConstraints.ANCHOR_CENTER)
                .setFill(GridConstraints.FILL_VERTICAL)
                .setHSizePolicy(GridConstraints.SIZEPOLICY_CAN_SHRINK)
                .setVSizePolicy(GridConstraints.SIZEPOLICY_CAN_GROW)
                .setIndent(0).build();
    }

    public static GridConstraints panelGridConstraints(int row) {
        return new MyGridConstraints(row)
                .setAnchor(GridConstraints.ANCHOR_CENTER)
                .setFill(GridConstraints.FILL_BOTH)
                .setHSizePolicy(GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW)
                .setVSizePolicy(GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK)
                .setIndent(0).build();
    }

    // ------------------------------ Separator Label --------------------------------
    public static int addRowOfItemsToPanel(
            JPanel panel,
            int startingColumn,
            String item,
            UnaryOperator<String> buildUrl) {
        return addRowOfItemsToPanel(panel, startingColumn, item, true, buildUrl);
    }

    public static int addRowOfItemsToPanel(
            JPanel panel,
            int startingColumn,
            String item,
            boolean firstSeparator,
            UnaryOperator<String> buildUrl) {
        List<String> items = StringUtil.isNotEmpty(item) ? Collections.singletonList(item) : Collections.emptyList();
        return addRowOfItemsToPanel(panel, startingColumn, items, firstSeparator, buildUrl);
    }

    public static int addRowOfItemsToPanel(
            JPanel panel,
            int startingColumn,
            List<String> items,
            UnaryOperator<String> buildUrl) {
        return addRowOfItemsToPanel(panel, startingColumn, items, true, buildUrl);
    }

    public static int addRowOfItemsToPanel(
            JPanel panel,
            int startingColumn,
            List<String> items,
            boolean firstSeparator,
            UnaryOperator<String> buildUrl) {
        return addRowOfItemsToPanel(panel, startingColumn, 0, items, "   |   ", firstSeparator, true, buildUrl);
    }

    public static int addRowOfItemsToPanel(
            JPanel panel,
            int startingColumn,
            int row,
            List<String> items,
            String separator,
            boolean firstSeparator,
            boolean opaqueSeparator,
            UnaryOperator<String> buildUrl) {
        int currentColumn = startingColumn;
        for (String item : items) {
            final String text = item.trim();
            if (StringUtil.isNotEmpty(item)) {
                JLabel positionPanel;
                if (buildUrl != null) {
                    positionPanel = LinkLabel.create(item, () -> {
                        String url = buildUrl.apply(text);
                        BrowserUtil.open(url);
                    });
                } else {
                    positionPanel = new JLabel(text);
                }
                if (currentColumn != startingColumn || (firstSeparator && currentColumn != -1)) {
                    currentColumn++;
                    JLabel label = new JLabel(separator);
                    if (opaqueSeparator) makeOpaque(label, 50);
                    panel.add(label, MyGridConstraints.gridBuilder(row).setColumn(currentColumn).setIndent(0).build());
                }
                currentColumn++;
                panel.add(positionPanel, MyGridConstraints.gridBuilder(row).setColumn(currentColumn).setIndent(0).build());
            }
        }
        return currentColumn;
    }

    public static int addRowOfItemsToPanel(
            JPanel panel,
            int startingColumn,
            int row,
            JLabel itemLabel,
            String separator,
            boolean firstSeparator,
            boolean opaqueSeparator) {
        if (itemLabel == null) return startingColumn;
        if (firstSeparator) {
            startingColumn++;
            JLabel label = new JLabel(separator);
            if (opaqueSeparator) makeOpaque(label, 50);
            panel.add(label, MyGridConstraints.gridBuilder(row).setColumn(startingColumn).setIndent(0).build());
        }
        startingColumn++;
        panel.add(itemLabel, MyGridConstraints.gridBuilder(row).setColumn(startingColumn).setIndent(0).build());
        return startingColumn;
    }

    private static void makeOpaque(JComponent component, int alpha) {
        component.setForeground(new Color(
                component.getForeground().getRed(),
                component.getForeground().getGreen(),
                component.getForeground().getBlue(),
                alpha));
    }

    // ------------------------------ More Information --------------------------------
    public static JPanel moreInfoPanel() {
        JPanel panel = defaultGridPanel(1, 1);
        String url = OpenSCASettingState.getInstance().getOpenSCASetting().getServerAddress();
        JLabel linkLabel = new JLabel("<html>更多信息请<u><a href='" + url + "'>登录平台</a></u>查看</html>");
        linkLabel.addMouseListener(linkLabelMouseListener(linkLabel, url));
        linkLabel.setIcon(AllIcons.General.Information);
        panel.add(linkLabel, MyGridConstraints.gridBuilder(0).build());
        return panel;
    }

    private static MouseListener linkLabelMouseListener(JLabel linkLabel, String url) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (StringUtil.isNotEmpty(url)) {
                    BrowserUtil.open(url);
                } else {
                    Notification.balloonNotify("请先配置服务器地址", MessageType.WARNING);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        };
    }

    // --------------------------------- Font ----------------------------------------
    public static Font primaryTitleFont() {
        return titleFont(20);
    }

    public static Font secondaryTitleFont() {
        return titleFont(16);
    }

    public static Font titleFont(int size) {
        return new Font(Font.DIALOG, Font.BOLD, size);
    }

    // ----------------------------- JScrollPane -------------------------------------
    public static JScrollPane wrapWithScrollPanel(Component component) {
        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(
                component,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        return scrollPane;
    }
}
