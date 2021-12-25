package cn.xmirror.sca.ui.action;

import cn.xmirror.sca.common.SettingStateSafe;
import cn.xmirror.sca.ui.window.ConfigurablePanel;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Configurable implements com.intellij.openapi.options.Configurable {

    private ConfigurablePanel configurablePanel;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "OpenSCA Setting";
    }

    @Override
    public @Nullable JComponent createComponent() {
        configurablePanel = new ConfigurablePanel(SettingStateSafe.getUrl(SettingStateSafe.KEY), SettingStateSafe.getToken(SettingStateSafe.KEY));
        return configurablePanel;
    }

    @Override
    public boolean isModified() {
        return StringUtil.compare(configurablePanel.getUrl(), SettingStateSafe.getUrl(SettingStateSafe.KEY), false) != 0
                || StringUtil.compare(configurablePanel.getToken(), SettingStateSafe.getToken(SettingStateSafe.KEY), false) != 0;
    }

    @Override
    public void apply() {
        SettingStateSafe.storeCredentials(configurablePanel.getUrl(), configurablePanel.getToken());
    }
}
