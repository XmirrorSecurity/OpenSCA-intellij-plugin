package cn.xmirror.sca.ui.dialog;

import cn.xmirror.sca.common.exception.ErrorEnum;
import cn.xmirror.sca.common.exception.SCAException;
import cn.xmirror.sca.service.HttpService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.scale.JBUIScale;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * 认证对话框
 * @author xingluheng
 * @date 2023/08/15 10:25
 **/
public class AuthDialog extends DialogWrapper {

    private boolean authCancel = false;
    public CopyUrlAction copyUrlAction = new CopyUrlAction();
    public JEditorPane viewer = new JEditorPane();

    public AuthDialog() {
        super(true);
        init();
        copyUrlAction.setEnabled(false);
        setTitle("Authenticating OpenSCA Plugin");
    }

    public void updateHtmlText(String htmlText) {
        viewer.setText(htmlText);
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[] {getCancelAction(),getOKAction()};
    }

    @Override
    protected Action @NotNull [] createLeftSideActions() {
        return new Action[] {copyUrlAction};
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
        authCancel = true;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(JBUIScale.scale(5), JBUIScale.scale(5)));
        viewer.setContentType("text/html");
        viewer.setEditable(false);
        String htmlContent = "<html><body>Initializing authentication...</body></html>";
        viewer.setText(htmlContent);
        viewer.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        JBScrollPane scrollPane = new JBScrollPane(viewer, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        centerPanel.add(progressBar, BorderLayout.SOUTH);

        centerPanel.setPreferredSize(new Dimension(500, 150));
        return centerPanel;
    }

    /**
     * 进行服务器授权认证
     * @param htmlLink 服务器根地址
     * @return
     */
    public String getAuth(String htmlLink,String param) {
        String authToken = "";
        while (!authCancel){
            try {
                authToken = HttpService.getAuthToken(htmlLink, param);
                if (StringUtils.isNotEmpty(authToken)){
                    authCancel = true;
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException | SCAException e) {
                throw new SCAException(ErrorEnum.SERVER_REQUEST_FAILURE_ERROR);
            }
        }
        return authToken;
    }


    public class CopyUrlAction extends AbstractAction {
        private String url = "";

        public void setUrl(String url) {
            this.url = url;
        }

        public CopyUrlAction() {
            super("&Copy URL", IconLoader.getIcon("/actions/copy.png",CopyUrlAction.class));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(url), null);
            Balloon balloon = JBPopupFactory.getInstance().createHtmlTextBalloonBuilder("URL copied",
                    AllIcons.General.BalloonInformation,
                    MessageType.INFO.getPopupBackground()
                    , null).setHideOnClickOutside(true).setFadeoutTime(3000).createBalloon();
            showBalloonForComponent(balloon, getButton(this), getButton(this) != null,null);

        }

        public void showBalloonForComponent(
                Balloon balloon,
                Component component,
                boolean showAbove,
                Point point) {
            Point targetPoint = point != null ? point :
                    new Point(component.getWidth() / 2, (showAbove) ? 0 : component.getHeight());
            RelativePoint relativePoint = new RelativePoint(component, targetPoint);
            Balloon.Position position = (showAbove) ? Balloon.Position.above : Balloon.Position.below;
            balloon.show(relativePoint, position);
        }
    }
}
