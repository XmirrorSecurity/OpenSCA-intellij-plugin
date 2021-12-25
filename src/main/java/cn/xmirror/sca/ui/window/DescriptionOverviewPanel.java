package cn.xmirror.sca.ui.window;

import cn.xmirror.sca.common.constant.SecurityLevelEnum;
import cn.xmirror.sca.common.dto.Overview;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.uiDesigner.core.GridConstraints;
import icons.Icons;
import lombok.Data;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class DescriptionOverviewPanel extends JPanel {
    private final Tree tree;
    private final DescriptionPanel descriptionPanel;
    private final Overview rootUserObject;

    public DescriptionOverviewPanel(Tree tree, DescriptionPanel descriptionPanel, OverviewPanel overviewPanel, Overview overview, Project project) {
        this.tree = tree;
        this.descriptionPanel = descriptionPanel;
        this.rootUserObject = overview;
        setLayout(PaintUtils.defaultMarginGridLayoutManager(10, 1));
        add(getTitlePanel(project, overviewPanel), PaintUtils.panelGridConstraints(0));
        add(getStatisticsPanel(), PaintUtils.panelGridConstraints(1));
        add(PaintUtils.moreInfoPanel(), PaintUtils.panelGridConstraints(9));
    }


    private JPanel getTitlePanel(Project project, OverviewPanel overviewPanel) {
        JPanel panel = PaintUtils.defaultGridPanel(1, 2);
        JButton toolButton = new JButton("工具引导");
        toolButton.addActionListener(e -> {
            tree.getSelectionModel().clearSelection();
            descriptionPanel.setDetailDescriptionPanel(new GuidePanel(project, overviewPanel));
        });
        panel.add(checkInfoPanel(), MyGridConstraints.gridBuilder(0).build());
        panel.add(toolButton, MyGridConstraints.gridBuilder(0).setColumn(1).setAnchor(GridConstraints.ANCHOR_EAST).build());
        return panel;
    }

    private JPanel checkInfoPanel() {
        JPanel panel = PaintUtils.defaultGridPanel(1, 8);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
        String start = rootUserObject.getStartTime() != null ? format.format(rootUserObject.getStartTime()) : "--";
        String end = rootUserObject.getEndTime() != null ? format.format(rootUserObject.getEndTime()) : "--";
        int columnCount = PaintUtils.addRowOfItemsToPanel(panel, -1, "开始时间：", false, null);
        columnCount = PaintUtils.addRowOfItemsToPanel(panel, columnCount, start, false, null);
        columnCount = PaintUtils.addRowOfItemsToPanel(panel, columnCount, "结束时间：", null);
        columnCount = PaintUtils.addRowOfItemsToPanel(panel, columnCount, end, false, null);
        return panel;
    }

    public JPanel getStatisticsPanel() {
        JPanel panel = PaintUtils.defaultGridPanel(6, 6);
        addTitle("组件统计", Icons.COMPONENT_STATISTICS, panel, 0);
        addTitle("漏洞统计", Icons.VUL_STATISTICS, panel, 2);
        addStatistics(getComponentStatisticsList(), panel, 1);
        addStatistics(getVulnerabilityStatisticsList(), panel, 3);
        return panel;
    }

    private void addTitle(String title, Icon icon, JPanel panel, int row) {
        panel.add(getTitle(title, icon), MyGridConstraints.gridBuilder(row).setIndent(0).setColSpan(6).setAnchor(GridConstraints.ANCHOR_WEST).build());
    }

    private JPanel getTitle(String title, Icon icon) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(title);
        label.setFont(PaintUtils.secondaryTitleFont());
        label.setIcon(icon);
        panel.add(label);
        return panel;
    }

    private void addStatistics(List<Statistics> statisticsList, JPanel panel, int row) {
        if (statisticsList != null) {
            String separator = "：   ";
            int currentColumn = -1;
            statisticsList.sort(Comparator.comparingInt(Statistics::getSort));
            for (Statistics statistics : statisticsList) {
                JLabel label = new JLabel(statistics.getSecurityAlias() + separator + statistics.getSecurityNum());
                label.setIcon(Icons.getIconFromResources(statistics.getSecurity()));
                panel.add(label, MyGridConstraints.gridBuilder(row).setColumn(++currentColumn).setAnchor(GridConstraints.ANCHOR_WEST).build());
            }
        }
    }

    private List<Statistics> getComponentStatisticsList() {
        List<Statistics> statisticsList = new ArrayList<>();
        int sort = -1;
        statisticsList.add(generateStatistics(++sort, SecurityLevelEnum.CRITICAL, rootUserObject.getCss()[0]));
        statisticsList.add(generateStatistics(++sort, SecurityLevelEnum.HIGH, rootUserObject.getCss()[1]));
        statisticsList.add(generateStatistics(++sort, SecurityLevelEnum.MEDIUM, rootUserObject.getCss()[2]));
        statisticsList.add(generateStatistics(++sort, SecurityLevelEnum.LOW, rootUserObject.getCss()[3]));
        return statisticsList;
    }

    private List<Statistics> getVulnerabilityStatisticsList() {
        List<Statistics> statisticsList = new ArrayList<>();
        int sort = -1;
        statisticsList.add(generateStatistics(++sort, SecurityLevelEnum.CRITICAL, rootUserObject.getVss()[0]));
        statisticsList.add(generateStatistics(++sort, SecurityLevelEnum.HIGH, rootUserObject.getVss()[1]));
        statisticsList.add(generateStatistics(++sort, SecurityLevelEnum.MEDIUM, rootUserObject.getVss()[2]));
        statisticsList.add(generateStatistics(++sort, SecurityLevelEnum.LOW, rootUserObject.getVss()[3]));
        return statisticsList;
    }

    private Statistics generateStatistics(int sort, SecurityLevelEnum security, int securityNum) {
        Statistics statistics = new Statistics();
        statistics.setSort(sort);
        statistics.setSecurity(security.getLevel());
        statistics.setSecurityAlias(security.getTag());
        statistics.setSecurityNum(securityNum);
        return statistics;
    }

    @Data
    static class Statistics {
        private int sort;
        private int security;
        private String securityAlias;
        private int securityNum;
    }
}
