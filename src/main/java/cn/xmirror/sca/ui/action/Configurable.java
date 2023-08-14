package cn.xmirror.sca.ui.action;

import cn.xmirror.sca.common.OpenSCASettingState;
import cn.xmirror.sca.common.pojo.OpenSCASetting;
import cn.xmirror.sca.ui.window.ConfigurablePanel;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Configurable implements com.intellij.openapi.options.Configurable {

    private ConfigurablePanel configurablePanel;
    private final OpenSCASettingState openSCASettingState = OpenSCASettingState.getInstance();

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "OpenSCA Setting";
    }

    @Override
    public @Nullable JComponent createComponent() {
        OpenSCASetting scaSetting = openSCASettingState.getOpenSCASetting();
        if (scaSetting!=null){
            configurablePanel = new ConfigurablePanel(scaSetting.getServerAddress(), scaSetting.getToken(), scaSetting.isUseCustomerCli(), scaSetting.getCustomerPath());
        }else {
            configurablePanel = new ConfigurablePanel();
        }
        return configurablePanel;
    }

    @Override
    public boolean isModified() {
        //指示是否修改了Swing表单。这个方法经常被调用，所以不会花很长时间。如果设置被修改，返回:true，否则返回false
        OpenSCASetting scaSetting = openSCASettingState.getOpenSCASetting();
        if (scaSetting == null) {
            return true;
        }
        return !configurablePanel.getUrl().equals(scaSetting.getServerAddress()) ||
                !configurablePanel.getToken().equals(scaSetting.getToken()) ||
                configurablePanel.getUseCustomerCli() != scaSetting.isUseCustomerCli() ||
                !configurablePanel.getCustomerCliPath().equals(scaSetting.getCustomerPath());
    }

    @Override
    public void apply() {
        String url = configurablePanel.getUrl();
        String token = configurablePanel.getToken();
        boolean useCustomerCli = configurablePanel.getUseCustomerCli();
        String customerCliPath = configurablePanel.getCustomerCliPath();
        OpenSCASetting scaSetting = new OpenSCASetting(url, token, useCustomerCli, customerCliPath);
        openSCASettingState.setOpenSCASetting(scaSetting);
    }
}
