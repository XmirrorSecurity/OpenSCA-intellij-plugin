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
        //指示是否修改了Swing表单。这个方法经常被调用，所以不会花很长时间。如果设置被修改，返回:true，否则返回false
        return StringUtil.compare(configurablePanel.getUrl(), SettingStateSafe.getUrl(SettingStateSafe.KEY), false) != 0
                || StringUtil.compare(configurablePanel.getToken(), SettingStateSafe.getToken(SettingStateSafe.KEY), false) != 0;
    }

    @Override
    public void apply() {
        SettingStateSafe.storeCredentials(configurablePanel.getUrl(), configurablePanel.getToken());
    }
}
