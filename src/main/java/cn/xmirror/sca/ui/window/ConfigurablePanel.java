package cn.xmirror.sca.ui.window;

import cn.xmirror.sca.service.HttpService;
import cn.xmirror.sca.ui.Notification;
import com.intellij.icons.AllIcons;
import com.intellij.ide.HelpTooltip;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.AnimatedIcon;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;

public class ConfigurablePanel extends JPanel {

    private JTextField urlText, tokenText;
    private JLabel progressLabel;

    public ConfigurablePanel(String url, String token) {
        super(new BorderLayout());
        add(getCertificatePanel(url, token), BorderLayout.NORTH);
    }

    public JPanel getCertificatePanel(String url, String token) {
        JPanel panel = new JPanel(new GridLayoutManager(3, 2));

        JLabel urlLabel = new JLabel("URL");
        urlLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel tokenLabel = new JLabel("Token");
        tokenLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        urlText = new JTextField(url);
        JLabel urlHelp = new JLabel(AllIcons.General.ContextHelp);
        new HelpTooltip().setDescription("OpenSCA平台Url，如：https://opensca.xmirror.cn").installOn(urlHelp);
        tokenText = new JTextField(token);
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
