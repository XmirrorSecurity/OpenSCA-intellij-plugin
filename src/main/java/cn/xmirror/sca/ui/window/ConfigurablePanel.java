package cn.xmirror.sca.ui.window;

import cn.xmirror.sca.common.exception.SCAException;
import cn.xmirror.sca.engine.EngineAssistant;
import cn.xmirror.sca.engine.EngineDownloader;
import cn.xmirror.sca.service.HttpService;
import cn.xmirror.sca.ui.Notification;
import com.intellij.icons.AllIcons;
import com.intellij.ide.HelpTooltip;
import com.intellij.notification.Notifications;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.FontInfo;
import icons.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import static cn.xmirror.sca.ui.window.PaintUtils.secondaryTitleFont;
import static cn.xmirror.sca.ui.window.PaintUtils.titleFont;

public class ConfigurablePanel extends JPanel {

    private static final String DOWNLOADS = "Downloads";
    private static final String CHECK_FRO_UPDATE = "Check For Update";
    private static final String UPDATE = "Update";

    private JBTextField urlText;
    private JBPasswordField tokenText;
    private TextFieldWithBrowseButton customerCliPath;
    private JBRadioButton defaultCli, customerCli;
    private JLabel progressLabel, versionLabel;
    private ActionLink actionLink;

    public ConfigurablePanel() {
        this("", "", false, "");
    }

    public ConfigurablePanel(String url, String token, boolean useCustomerCli, String cliPath) {
        super(new BorderLayout());
        add(getCertificatePanel(url, token), BorderLayout.NORTH);
        add(executableSettingPanel(useCustomerCli, cliPath), BorderLayout.SOUTH);
    }

    public JPanel getCertificatePanel(String url, String token) {
        JPanel panel = new JPanel(new GridLayoutManager(3, 2));

        JLabel urlLabel = new JLabel("URL");
        urlLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel tokenLabel = new JLabel("Token");
        tokenLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        urlText = new JBTextField(url);
        JLabel urlHelp = new JLabel(AllIcons.General.ContextHelp);
        new HelpTooltip().setDescription("OpenSCA平台Url，如：https://opensca.xmirror.cn").installOn(urlHelp);
        tokenText = new JBPasswordField();
        tokenText.setText(token);
        JLabel tokenHelp = new JLabel(AllIcons.General.ContextHelp);
        new HelpTooltip().setDescription("OpenSCA平台Token，如：xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx").installOn(tokenHelp);
        JPanel urlPanel = new JPanel(new GridLayoutManager(1, 2));
        urlPanel.add(urlText, new MyGridConstraints(0).setIndent(0).setFill(GridConstraints.FILL_HORIZONTAL).setHSizePolicy(GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK).build());
        urlPanel.add(urlHelp, new MyGridConstraints(0).setIndent(0).setColumn(1).setAnchor(GridConstraints.ANCHOR_WEST).build());
        JPanel tokenPanel = new JPanel(new GridLayoutManager(1, 2));
        tokenPanel.add(tokenText, new MyGridConstraints(0).setIndent(0).setFill(GridConstraints.FILL_HORIZONTAL).setHSizePolicy(GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK).build());
        tokenPanel.add(tokenHelp, new MyGridConstraints(0).setIndent(0).setColumn(1).setAnchor(GridConstraints.ANCHOR_WEST).build());

        panel.add(urlLabel, new MyGridConstraints(0).setAnchor(GridConstraints.ANCHOR_WEST).build());
        panel.add(tokenLabel, new MyGridConstraints(1).setAnchor(GridConstraints.ANCHOR_WEST).build());
        panel.add(urlPanel, new MyGridConstraints(0).setColumn(1).setFill(GridConstraints.FILL_HORIZONTAL).setHSizePolicy(GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK).build());
        panel.add(tokenPanel, new MyGridConstraints(1).setColumn(1).setFill(GridConstraints.FILL_HORIZONTAL).setHSizePolicy(GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK).build());
        panel.add(connButton(), new MyGridConstraints(2).setColumn(1).setAnchor(GridConstraints.ANCHOR_EAST).build());
        return panel;
    }

    public JPanel executableSettingPanel(boolean useCustomerCli, String path) {
        JPanel panel = new JPanel(new GridLayoutManager(3, 2));
        JLabel label = new JLabel("Executable Settings");
        label.setFont(titleFont(14));
        ButtonGroup buttonGroup = new ButtonGroup();
        defaultCli = new JBRadioButton("Use OpenSCA CLI: ");
        customerCli = new JBRadioButton("Custom OpenSCA CLI Path: ");
        buttonGroup.add(defaultCli);
        buttonGroup.add(customerCli);
        customerCliPath = new TextFieldWithBrowseButton();
        FileChooserDescriptor cliChooserDescriptor = new FileChooserDescriptor
                (true, false, false, false, false, false);
        TextBrowseFolderListener folderListener = new TextBrowseFolderListener(cliChooserDescriptor);
        customerCliPath.addBrowseFolderListener(folderListener);
        if (useCustomerCli) {
            customerCli.setSelected(true);
            customerCliPath.setText(path);
        } else {
            defaultCli.setSelected(true);
        }
        JPanel downloadAndVersionPanel = new JPanel(new GridLayout(1, 4));
        if (StringUtil.isEmpty(EngineAssistant.getEngineCliPath())) {
            versionLabel = new JLabel();
            versionLabel.setText("OpenSCA CLI executable could not be find");
            versionLabel.setIcon(Icons.FAILED);
            actionLink = new ActionLink(DOWNLOADS);
        } else {
            versionLabel = new JLabel();
            versionLabel.setText(getCliVersion());
            actionLink = new ActionLink(CHECK_FRO_UPDATE);
        }
        actionLink.addActionListener(getActionLinkListener(actionLink.getText()));
        downloadAndVersionPanel.add(versionLabel);
        downloadAndVersionPanel.add(actionLink);

        panel.add(label, new MyGridConstraints(0).setAnchor(GridConstraints.ANCHOR_WEST).build());
        panel.add(defaultCli, new MyGridConstraints(1).setAnchor(GridConstraints.ANCHOR_WEST).build());
        panel.add(downloadAndVersionPanel, new MyGridConstraints(1).setColumn(1).setFill(GridConstraints.FILL_HORIZONTAL).setHSizePolicy(GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK).build());
        panel.add(customerCli, new MyGridConstraints(2).setAnchor(GridConstraints.ANCHOR_WEST).build());
        panel.add(customerCliPath, new MyGridConstraints(2).setColumn(1).setFill(GridConstraints.FILL_HORIZONTAL).setHSizePolicy(GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK).build());
        return panel;
    }


    private JPanel connButton() {
        JPanel panel = new JPanel();
        JButton connButton = new JButton("测试连接");
        connButton.addActionListener(e -> testConnection(panel));
        panel.add(connButton, 0);
        return panel;
    }

    public String getUrl() {
        String url = urlText.getText().trim();
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public String getToken() {
        return tokenText.getText().trim();
    }

    public boolean getUseCustomerCli() {
        return customerCli.isSelected();
    }

    public String getCustomerCliPath() {
        return customerCliPath.getText();
    }

    private ActionListener getActionLinkListener(String type) {
        versionLabel.setIcon(Icons.REFRESH);
        if (DOWNLOADS.equals(type)) {
            return e -> downLoadCliAndUpdateText();
        } else if (UPDATE.equals(type)) {
            return e -> downLoadCliAndUpdateText();
        } else {
            return e -> {
                String remoteServerCliVersion = HttpService.getRemoteServerCliVersion();
                if (versionLabel.getText().contains(remoteServerCliVersion)) {
                    versionLabel.setIcon(Icons.SUCCEEDED);
                    versionLabel.setText(remoteServerCliVersion + " is up to date");
                } else {
                    versionLabel.setText(remoteServerCliVersion + " need to update");
                    versionLabel.setIcon(Icons.WARNING);
                    actionLink.setText("Update");
                }
            };
        }
    }

    private void downLoadCliAndUpdateText() {
        try {
            HttpService.downloadEngine(EngineAssistant.getEngineCliPath());
            versionLabel.setIcon(Icons.SUCCEEDED);
            versionLabel.setText(getCliVersion());
            actionLink = new ActionLink(CHECK_FRO_UPDATE);
        } catch (SCAException exception) {
            versionLabel.setIcon(Icons.FAILED);
            versionLabel.setText("OpenSCA CLI Downloads Error");
        }
    }

    private String getCliVersion() {
        File file = new File(EngineAssistant.getEngineVersionPath());
        try {
            return FileUtil.loadFile(file);
        } catch (IOException error) {
            Notification.balloonNotify(error.getMessage(), MessageType.ERROR);
            return "";
        }
    }

    private void testConnection(JPanel parent) {
        if (parent.getComponentCount() > 1) {
            Notification.balloonNotify("操作太快了", MessageType.WARNING);
            return;
        }
        if (progressLabel == null) {
            progressLabel = new JLabel("", new AnimatedIcon.Default(), SwingConstants.LEFT);
        }
        parent.add(progressLabel, 0);
        updateUI();

        HttpService.testConnection(getUrl(), getToken(), message -> {
            SwingUtilities.invokeLater(() -> Messages.showInfoMessage(message, "测试连接"));
            parent.remove(0);
            ConfigurablePanel.this.updateUI();
        });
    }
}
