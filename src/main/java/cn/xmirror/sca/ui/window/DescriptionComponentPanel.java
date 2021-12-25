package cn.xmirror.sca.ui.window;

import cn.xmirror.sca.common.dto.Component;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.UIUtil;
import icons.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class DescriptionComponentPanel extends JPanel {

    private final Component component;
    private final Project project;

    public DescriptionComponentPanel(Component component, Project project) {
        super(new BorderLayout());
        JPanel panel = new JPanel(PaintUtils.defaultMarginGridLayoutManager(10, 1));
        this.component = component;
        this.project = project;
        panel.add(getTitlePanel(), PaintUtils.panelGridConstraints(0));
        panel.add(getOriginTextPanel(), PaintUtils.panelGridConstraints(1));
        JPanel licensePanel = getLicenseInfo();
        if (licensePanel != null) {
            panel.add(licensePanel, PaintUtils.panelGridConstraints(2));
        }

        JPanel compressPanel = new JPanel();
        compressPanel.add(panel);
        add(compressPanel, BorderLayout.WEST);
    }

    private JPanel getTitlePanel() {
        JPanel panel = PaintUtils.defaultGridPanel(2, 1);
        JLabel titleLabel = new JLabel(component.getName());
        titleLabel.setFont(PaintUtils.primaryTitleFont());
        titleLabel.setIcon(Icons.getIconFromResources(component.getSecurityLevelId(), Icons.IconSize.SIZE24));
        panel.add(titleLabel, new MyGridConstraints(0).build());
        panel.add(vendorAndVersion(), MyGridConstraints.gridBuilder(1).build());
        return panel;
    }

    private JPanel vendorAndVersion() {
        JPanel panel = PaintUtils.defaultGridPanel(1, 5);
        String vendorTag = "发布厂商：";
        String versionTag = "版本号：";
        String vendor = StringUtil.isNotEmpty(component.getVendor()) ? component.getVendor() : "--";
        String version = StringUtil.isNotEmpty(component.getVersion()) ? component.getVersion() : "--";
        int columnCount = PaintUtils.addRowOfItemsToPanel(panel, -1, vendorTag, false, null);
        columnCount = PaintUtils.addRowOfItemsToPanel(panel, columnCount, vendor, false, null);
        columnCount = PaintUtils.addRowOfItemsToPanel(panel, columnCount, versionTag, null);
        PaintUtils.addRowOfItemsToPanel(panel, columnCount, version, false, null);
        return panel;
    }

    private JPanel getOriginTextPanel() {
        JPanel panel = PaintUtils.defaultGridPanel(1, 1);
        JPanel originTextPanel = originTextPanel();
        if (originTextPanel != null) {
            panel.add(originTextPanel, MyGridConstraints.gridBuilder(0).build());
        }
        return panel;
    }

    private JPanel originTextPanel() {
        List<String> paths = component.getPaths();
        if (paths.isEmpty()) {
            return null;
        }
        JPanel panel = PaintUtils.defaultGridPanel(paths.size() * 2, 2);
        JLabel filePosition = new JLabel("文件位置：");
        JLabel componentPosition = new JLabel("组件位置：");

        FileEditorManager editorManager = FileEditorManager.getInstance(project);
        LocalFileSystem fileSystem = LocalFileSystem.getInstance();

        for (int i = 0; i < paths.size(); i++) {
            panel.add(filePosition, MyGridConstraints.gridBuilder(i * 2).setIndent(0).build());
            panel.add(componentPosition, MyGridConstraints.gridBuilder(i * 2 + 1).setIndent(0).build());

            String path = paths.get(i);
            String p = path.substring(0, path.indexOf("/["));

            ActionLink actionLink = new ActionLink(p);
            VirtualFile file = fileSystem.findFileByPath(project.getBasePath() + File.separator + p);
            actionLink.addActionListener(getActionLinkListener(editorManager, file));

            panel.add(actionLink, MyGridConstraints.gridBuilder(i * 2).setIndent(0).setColumn(1).build());
            panel.add(new JBLabel(getDependencies(path), UIUtil.ComponentStyle.LARGE), MyGridConstraints.gridBuilder(i * 2 + 1).setIndent(0).setColumn(1).build());
        }
        return panel;
    }

    private ActionListener getActionLinkListener(FileEditorManager manager, VirtualFile file) {
        if (file == null) return null;
        return e -> manager.openFile(file, true, true);
    }

    private String getDependencies(String path) {
        if (path == null) {
            return "";
        }
        String tmpDependencies = path.substring(path.indexOf("/[") + 1);
        String[] dependencies = tmpDependencies.split("/");
        return String.join(" -> ", dependencies);
    }

    private JPanel getLicenseInfo() {
        List<String> licenses = component.getLicenses();
        if (licenses == null) return null;
        JPanel panel = PaintUtils.defaultGridPanel(2, 1);
        JLabel titleLabel = new JLabel("许可证");
        titleLabel.setFont(PaintUtils.secondaryTitleFont());
        titleLabel.setIcon(Icons.LICENSE);

        panel.add(titleLabel, new MyGridConstraints(0).build());
        panel.add(LicenseInfo(), new MyGridConstraints(1).build());
        return panel;
    }

    private JPanel LicenseInfo() {
        JPanel licensePanel = new JPanel();
        for (String license : component.getLicenses()) {
            licensePanel.add(new Label(license));
        }
        return licensePanel;
    }
}
