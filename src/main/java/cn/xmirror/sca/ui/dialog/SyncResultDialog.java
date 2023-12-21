package cn.xmirror.sca.ui.dialog;

import cn.xmirror.sca.common.OpenSCASettingState;
import cn.xmirror.sca.common.dto.GeneralSelectDTO;
import cn.xmirror.sca.common.dto.ProjectSelectDTO;
import cn.xmirror.sca.common.pojo.OpenSCASetting;
import cn.xmirror.sca.engine.EngineAssistant;
import cn.xmirror.sca.service.HttpService;
import cn.xmirror.sca.ui.NotificationUtils;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xingluheng
 * @date 2023/11/06 11:17
 **/
public class SyncResultDialog extends DialogWrapper {
    private JLabel selectProjectLabel;
    private String selectProjectUid;
    private Map<String, String> projectMap;
    private Project project;
    private String token;

    public SyncResultDialog(Project project) {
        super(project);
        this.project = project;

        OpenSCASetting scaSetting = OpenSCASettingState.getInstance().getOpenSCASetting();
        token = scaSetting.getToken();
        List<ProjectSelectDTO> teamProjectSelectList = HttpService.getTeamProjectSelectList(token);
        projectMap = getProjectSelectMap(teamProjectSelectList);

        init();
        setTitle("Upload Detect Result To OpenSCA");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        // 创建对话框面板，将树组件添加到面板中
        JPanel panel = new JPanel(new BorderLayout());
        selectProjectLabel = new JLabel("请选择漏洞上传项目");
        panel.add(selectProjectLabel, BorderLayout.NORTH);
        panel.add(createTreePanel(), BorderLayout.CENTER);
        return panel;
    }


    /**
     * 创建树形Panel
     *
     * @return
     */
    @NotNull
    private JBScrollPane createTreePanel() {

        ButtonGroup buttonGroup = new ButtonGroup();
        JPanel projectSelectPanel = new JPanel();
        projectSelectPanel.setLayout(new BoxLayout(projectSelectPanel, BoxLayout.Y_AXIS));
        JRadioButton quickDetectButton = new JRadioButton("快速检测");
        buttonGroup.add(quickDetectButton);
        projectSelectPanel.add(quickDetectButton);


        for (Map.Entry<String, String> entry : projectMap.entrySet()) {
            JRadioButton radioButton = new JRadioButton(entry.getKey());
            radioButton.addActionListener(e -> {
                selectProjectUid = projectMap.get(radioButton.getText());
                setOKActionEnabled(true);
            });
            buttonGroup.add(radioButton);
            projectSelectPanel.add(radioButton);
        }

        JBScrollPane treeScrollPanel = new JBScrollPane(projectSelectPanel);
        treeScrollPanel.setPreferredSize(new Dimension(300, 300));

        return treeScrollPanel;
    }

    /**
     * 从团队项目选择列表中提取项目名称和对应的唯一标识符（UID），并将其映射到一个 Map 中。
     *
     * @param teamProjectSelectList 团队项目选择列表
     * @return 包含项目名称作为键和对应的 UID 作为值的映射
     */
    private Map<String, String> getProjectSelectMap(List<ProjectSelectDTO> teamProjectSelectList) {
        Map<String, String> projectMap = new HashMap<>();
        for (ProjectSelectDTO projectSelectDTO : teamProjectSelectList) {
            for (GeneralSelectDTO generalSelectDTO : projectSelectDTO.getProjectList()) {
                projectMap.put(projectSelectDTO.getTeamName() + "/" + generalSelectDTO.getName(), generalSelectDTO.getProjectUid());
            }
        }
        return projectMap;
    }


    @Override
    protected void doOKAction() {
        super.doOKAction();
        Map<String, String> requestParamMap = new HashMap<>();
        requestParamMap.put("token", token);
        requestParamMap.put("projectUid", selectProjectUid == null ? "" : selectProjectUid);
        requestParamMap.put("version", "-");
        requestParamMap.put("detectOrigin", "4");
        File jsonFile = new File(EngineAssistant.getCheckResultJsonPath(project));
        File dsdxFile = new File(EngineAssistant.getCheckResultDsdxPath(project));
        try {
            String recordUrl = HttpService.syncDetectResult(requestParamMap, jsonFile, dsdxFile, 10 * 1000);
            String url = "https://opensca.xmirror.cn/" + recordUrl;
            String notificationText = "漏洞上传SaaS成功!";
            NotificationUtils.balloonNotifyWithAction(notificationText,NotificationType.INFORMATION, new NotificationAction("See Details In OpenSCA SaaS") {
                @SneakyThrows
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent, @NotNull Notification notification) {
                    Desktop.getDesktop().browse(new URI(url));
                }
            },project);
        } catch (Exception e) {
            NotificationUtils.balloonNotify("同步上传漏洞失败", NotificationType.ERROR,project);
        } finally {
            selectProjectUid = null;
            projectMap.clear();
        }


    }

    @Override
    public void setOKActionEnabled(boolean isEnabled) {
        super.setOKActionEnabled(isEnabled);
    }
}
