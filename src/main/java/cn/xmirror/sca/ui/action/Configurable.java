package cn.xmirror.sca.ui.action;

import cn.xmirror.sca.common.OpenSCASettingState;
import cn.xmirror.sca.common.pojo.DsnConfig;
import cn.xmirror.sca.common.pojo.OpenSCASetting;
import cn.xmirror.sca.ui.window.ConfigurablePanel;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsContexts;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

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
            List<DsnConfig> dsnConfigList = scaSetting.getDsnConfigList();
            configurablePanel = new ConfigurablePanel(scaSetting.getServerAddress(), scaSetting.getToken(),
                    scaSetting.getUseCustomerCli(), scaSetting.getCustomerPath(),
                    scaSetting.getRemoteDataSourceSelected(),scaSetting.getLocalDataSourceSelected(),dsnConfigList);
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
                configurablePanel.getUseCustomerCli() != scaSetting.getUseCustomerCli() ||
                !configurablePanel.getCustomerCliPath().equals(scaSetting.getCustomerPath()) ||
                configurablePanel.getRemoteDataSourceCheckBox().isSelected() != scaSetting.getRemoteDataSourceSelected() ||
                configurablePanel.getDsnListPanel().getLocalDataSourceCheckBox().isSelected() != scaSetting.getLocalDataSourceSelected();
    }

    @Override
    public void apply() {
        String url = configurablePanel.getUrl();
        String token = configurablePanel.getToken();
        boolean useCustomerCli = configurablePanel.getUseCustomerCli();
        String customerCliPath = configurablePanel.getCustomerCliPath();
        boolean remoteDataSourceSelected = configurablePanel.getRemoteDataSourceCheckBox().isSelected();
        boolean localDataSourceSelected = configurablePanel.getDsnListPanel().getLocalDataSourceCheckBox().isSelected();
        if (paramCheckNotPass(configurablePanel)) return;
        List<DsnConfig> dsnConfigList = configurablePanel.getDsnListPanel().getDsnTableModel().getDsnConfigList();
        OpenSCASetting scaSetting = new OpenSCASetting(url, token, useCustomerCli, customerCliPath,remoteDataSourceSelected,localDataSourceSelected,dsnConfigList);
        openSCASettingState.setOpenSCASetting(scaSetting);
    }

    /**
     * 参数检查是否不通过
     * @return true不通过 false通过
     */
    private boolean paramCheckNotPass(ConfigurablePanel configurablePanel) {
        boolean useCustomerCli = configurablePanel.getUseCustomerCli();
        String customerCliPath = configurablePanel.getCustomerCliPath();
        boolean remoteDataSourceSelected = configurablePanel.getRemoteDataSourceCheckBox().isSelected();
        boolean localDataSourceSelected = configurablePanel.getDsnListPanel().getLocalDataSourceCheckBox().isSelected();
        List<DsnConfig> dsnConfigList = configurablePanel.getDsnListPanel().getDsnTableModel().getDsnConfigList();

        if (useCustomerCli && StringUtils.isEmpty(customerCliPath)){
            Messages.showErrorDialog("OpenSCA Cli地址不能为空","OpenSCA");
            return true;
        }
        if (!remoteDataSourceSelected && !localDataSourceSelected){
            Messages.showErrorDialog("请配置漏洞库数据源","OpenSCA");
            return true;
        }
        if (remoteDataSourceSelected && configurablePanel.getToken().isEmpty()) {
            Messages.showErrorDialog("Token不能为空", "OpenSCA");
            return true;
        }
        List<DsnConfig> collect = dsnConfigList.stream().filter(item -> item.getSelect().equals(Boolean.TRUE)).collect(Collectors.toList());

        if (localDataSourceSelected && CollectionUtils.isEmpty(collect)) {
            Messages.showErrorDialog("请配置本地数据源", "OpenSCA");
            return true;
        }
        return false;
    }
}
