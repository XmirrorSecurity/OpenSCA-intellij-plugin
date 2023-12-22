package cn.xmirror.sca.ui.window;

import cn.xmirror.sca.common.OpenSCASettingState;
import cn.xmirror.sca.common.exception.ErrorEnum;
import cn.xmirror.sca.common.exception.SCAException;
import cn.xmirror.sca.common.pojo.DsnConfig;
import cn.xmirror.sca.common.pojo.OpenSCASetting;
import cn.xmirror.sca.engine.EngineAssistant;
import cn.xmirror.sca.engine.EngineDownloader;
import cn.xmirror.sca.service.HttpService;
import cn.xmirror.sca.ui.NotificationUtils;
import cn.xmirror.sca.ui.dialog.AuthDialog;
import com.intellij.icons.AllIcons;
import com.intellij.ide.HelpTooltip;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import icons.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static cn.xmirror.sca.ui.window.PaintUtils.titleFont;

public class ConfigurablePanel extends JPanel {
    private static final Logger LOG = Logger.getInstance(ConfigurablePanel.class);
    public static final String BASE_URL = "https://opensca.xmirror.cn";

    private static final String DOWNLOADS = "Downloads";
    private static final String CHECK_FOR_UPDATE = "Check For Update";
    private static final String UPDATE = "Update";
    private static final String AUTH_URL = "/auth";

    private JBTextField urlText;
    private JBPasswordField tokenText;
    private TextFieldWithBrowseButton customerCliPath;
    private JBRadioButton defaultCli, customerCli;
    private JLabel progressLabel, versionLabel;
    private ActionLink actionLink;
    private DsnListPanel dsnListPanel;
    private JCheckBox remoteDataSourceCheckBox;


    public ConfigurablePanel() {
        this(BASE_URL, "", false, "", true, false, Collections.emptyList());
    }

    public ConfigurablePanel(String url, String token, boolean useCustomerCli, String cliPath, Boolean remoteDataSourceSelected, Boolean localDataSourceSelected, List<DsnConfig> dsnConfigList) {
        super(new BorderLayout());
        add(getCertificatePanel(url, token, remoteDataSourceSelected), BorderLayout.NORTH);
        Project currentProject = ProjectManager.getInstance().getDefaultProject();
        dsnListPanel = new DsnListPanel(currentProject, localDataSourceSelected, dsnConfigList);
        add(dsnListPanel, BorderLayout.CENTER);
        add(executableSettingPanel(useCustomerCli, cliPath), BorderLayout.SOUTH);
    }

    public JPanel getCertificatePanel(String url, String token, Boolean remoteDataSourceSelected) {
        JPanel panel = new JPanel(new GridLayoutManager(4, 2));

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

        remoteDataSourceCheckBox = new JCheckBox("Remote Data Source");
        remoteDataSourceCheckBox.setSelected(remoteDataSourceSelected);
        if (!remoteDataSourceSelected) {
            urlText.setEnabled(false);
            tokenText.setEnabled(false);
        }
        JButton connectOpenSCAButton = connectOpenSCA();
        remoteDataSourceCheckBox.addItemListener(e -> {
            connectOpenSCAButton.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
            urlText.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
            tokenText.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        });
        connectOpenSCAButton.setEnabled(remoteDataSourceSelected);
        panel.add(remoteDataSourceCheckBox, new MyGridConstraints(0).setColumn(0).setAnchor(GridConstraints.ANCHOR_WEST).build());
        panel.add(connectOpenSCAButton, new MyGridConstraints(0).setColumn(1).setAnchor(GridConstraints.ANCHOR_WEST).build());
        panel.add(urlLabel, new MyGridConstraints(1).setAnchor(GridConstraints.ANCHOR_CENTER).build());
        panel.add(tokenLabel, new MyGridConstraints(2).setAnchor(GridConstraints.ALIGN_CENTER).build());
        panel.add(urlPanel, new MyGridConstraints(1).setColumn(1).setFill(GridConstraints.FILL_HORIZONTAL).setHSizePolicy(GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK).build());
        panel.add(tokenPanel, new MyGridConstraints(2).setColumn(1).setFill(GridConstraints.FILL_HORIZONTAL).setHSizePolicy(GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK).build());
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
        } else {
            defaultCli.setSelected(true);
        }
        customerCliPath.setText(path);
        JPanel downloadAndVersionPanel = new JPanel(new GridLayout(1, 4));
        if (!FileUtil.exists(EngineAssistant.getEngineCliPath())) {
            versionLabel = new JLabel();
            versionLabel.setText("OpenSCA CLI could not be find");
            versionLabel.setIcon(Icons.FAILED);
            actionLink = new ActionLink(DOWNLOADS);
        } else {
            versionLabel = new JLabel();
            versionLabel.setText(getCliVersion());
            actionLink = new ActionLink(CHECK_FOR_UPDATE);
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

    private JButton connectOpenSCA() {
        JButton connectOpenSCA = new JButton("Quick Authorization");
        connectOpenSCA.setToolTipText("Click to quickly obtain the token for server connection");
        connectOpenSCA.addActionListener(e -> {
            AuthDialog authDialog = new AuthDialog();
            // 运行后台任务 更改文本 进程轮训获取token 修改按钮 取消停止进程
            Task.Backgroundable authenticationTask = new Task.Backgroundable(null, "Authenticating OpenSCA Plugin", true) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {
                        Thread.sleep(500);
                        String uuId = UUID.randomUUID().toString();
                        String serverAddress = BASE_URL;
                        OpenSCASetting openSCASetting = OpenSCASettingState.getInstance().getOpenSCASetting();
                        if (openSCASetting != null) {
                            serverAddress = openSCASetting.getServerAddress();
                        }
                        String htmlLink = serverAddress + AUTH_URL + "?tokenid=" + uuId + "&from=idea";
                        String htmlText =
                                "<html><body>\n" +
                                        "We are now redirecting you to our auth page, go ahead and log in.<br><br>\n" +
                                        "Once the authentication is complete, return to the IDE and you'll be ready to start using OpenSCA.<br><br>\n" +
                                        "If a browser window doesn't open after a few seconds, please <a href=\"" + htmlLink + "\">click here</a>\n" +
                                        "or copy the url using the button below and manually paste it in a browser.\n" +
                                        "</body></html>";
                        authDialog.updateHtmlText(htmlText);
                        authDialog.copyUrlAction.setUrl(htmlLink);
                        authDialog.copyUrlAction.setEnabled(true);
                        authDialog.setOKActionEnabled(false);
                        Desktop.getDesktop().browse(URI.create(htmlLink));
                        String token = authDialog.getAuth(serverAddress, uuId);
                        if ("error".equals(token)) {
                            authDialog.updateHtmlText("<h3>Authenticating failure,Please generate token first</h3>");
                        } else {
                            authDialog.updateHtmlText("<h2>Authenticating Success</h2>");
                            tokenText.setText(token);
                        }
                        authDialog.setOKActionEnabled(true);
                        urlText.setText("https://opensca.xmirror.cn");
                    } catch (InterruptedException | IOException | SCAException ex) {
                        ex.printStackTrace();
                        LOG.error(ex);
                        NotificationUtils.balloonNotify("Authentication Failed", NotificationType.ERROR);
                    }
                }
            };
            authenticationTask.queue();
            authDialog.show();
        });

        return connectOpenSCA;
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
        // 下载 更新 检查更新
        if (DOWNLOADS.equals(type)) {
            return e -> downLoadCliAndUpdateText("下载");
        } else if (UPDATE.equals(type)) {
            return e -> downLoadCliAndUpdateText("更新");
        } else {
            return e -> {
                versionLabel.setIcon(Icons.REFRESH);
                String remoteServerCliVersion = HttpService.getRemoteServerCliVersion();
                if (versionLabel.getText().contains(remoteServerCliVersion)) {
                    versionLabel.setIcon(Icons.SUCCEEDED);
                    versionLabel.setText(remoteServerCliVersion + " is up to date");
                } else {
                    versionLabel.setText(remoteServerCliVersion + " need to update");
                    versionLabel.setIcon(Icons.WARNING);
                    actionLink.setText("Update");
                    actionLink.addActionListener(getActionLinkListener(actionLink.getText()));
                }
            };
        }
    }

    private void downLoadCliAndUpdateText(String type) {
        try {
            if (ProgressManager.getInstance().runProcessWithProgressSynchronously(
                    () -> {
                        HttpService.downloadEngine(EngineAssistant.getEngineCliPath());
                    }, type + "OpenSCA命令行工具", false,
                    ProjectManager.getInstance().getDefaultProject(), this.getRootPane())) {
                String version = HttpService.getRemoteServerCliVersion();
                FileUtil.writeToFile(new File(EngineAssistant.getEngineVersionPath()), version);
                // 设置引擎执行权限
                File engineCli = new File(EngineAssistant.getEngineCliPath());
                if (!engineCli.canExecute() && !engineCli.setExecutable(true)) {
                    throw new SCAException(ErrorEnum.ENGINE_SET_EXECUTABLE_ERROR, EngineAssistant.getEngineCliPath());
                }
                EngineDownloader.createCliConfig(engineCli.getPath());
                versionLabel.setIcon(Icons.SUCCEEDED);
                versionLabel.setText(version);
                actionLink.setText("");
            }
        } catch (SCAException | IOException exception) {
            versionLabel.setIcon(Icons.FAILED);
            versionLabel.setText("OpenSCA CLI Downloads Error");
            LOG.error("###引擎下载失败###" + exception);
        } catch (InterruptedException e) {
            LOG.error("###引擎创建配置文件失败###" );
        }
    }

    private String getCliVersion() {
        String engineCliPath = EngineAssistant.getEngineCliPath();
        try {
            return EngineDownloader.getLocalCliVersion(engineCliPath);
        } catch (IOException error) {
            NotificationUtils.balloonNotify(error.getMessage(), NotificationType.ERROR);
            return "";
        } catch (InterruptedException e) {
            return "";
        }
    }

    private void testConnection(JPanel parent) {
        if (parent.getComponentCount() > 1) {
            NotificationUtils.balloonNotify("操作太快了", NotificationType.WARNING);
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

    public DsnListPanel getDsnListPanel() {
        return dsnListPanel;
    }

    public JCheckBox getRemoteDataSourceCheckBox() {
        return remoteDataSourceCheckBox;
    }
}
