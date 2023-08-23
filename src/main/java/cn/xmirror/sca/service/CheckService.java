package cn.xmirror.sca.service;

import cn.xmirror.sca.common.CheckListener;
import cn.xmirror.sca.common.OpenSCASettingState;
import cn.xmirror.sca.common.ResultParser;
import cn.xmirror.sca.common.SCAThreadPool;
import cn.xmirror.sca.common.dto.Origin;
import cn.xmirror.sca.common.dto.Overview;
import cn.xmirror.sca.common.exception.ErrorEnum;
import cn.xmirror.sca.common.exception.SCAException;
import cn.xmirror.sca.common.pojo.DsnConfig;
import cn.xmirror.sca.common.pojo.OpenSCASetting;
import cn.xmirror.sca.common.util.VerifyUtils;
import cn.xmirror.sca.engine.EngineAssistant;
import cn.xmirror.sca.ui.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.tree.MutableTreeNode;
import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * 检测服务类
 *
 * @author Yuan Shengjun
 */
public class CheckService {
    private static final Logger LOG = Logger.getInstance(CheckService.class);

    /**
     * 任务状态
     * <ui>
     * <li>0：已停止</li>
     * <li>1：停止中</li>
     * <li>2：运行中</li>
     * </ui>
     */
    private static final Map<Project, Integer> status = new ConcurrentHashMap<>();
    private static final Integer stopped = 0;
    private static final Integer stopping = 1;
    private static final Integer running = 2;

    private static final Map<Project, Future<?>> futures = new ConcurrentHashMap<>();
    private static final Map<Project, Process> processes = new ConcurrentHashMap<>();

    public static String PROJECT_BASE_PATH;
    private static final OpenSCASettingState openSCASettingState = OpenSCASettingState.getInstance();

    /**
     * 开始检测
     *
     * @param project  当前项目
     * @param listener 检测监听器
     */
    public static void run(Project project, CheckListener listener) {
        PROJECT_BASE_PATH = project.getBasePath() + File.separator;
        status.put(project, running);
        Overview overview = new Overview();
        overview.setStartTime(new Date());

        Future<?> future = SCAThreadPool.getInstance().submit(() -> {
            try {
                listener.progress(true);
                clean(project, listener);
                // 检测前版本检查改为用户手动 EngineDownloader.checkAndDownload(project);
                if (openSCASettingState.getOpenSCASetting() ==null) {
                    Notification.balloonNotify("请配置OpenSCA后,再进行检测", NotificationType.ERROR);
                    return;
                }
                String url = openSCASettingState.getOpenSCASetting().getServerAddress();
                String token = openSCASettingState.getOpenSCASetting().getToken();
                if (!openSCASettingState.getOpenSCASetting().getRemoteDataSourceSelected()) token = "";
                VerifyUtils.verifyUrl(url);
                // 提交任务
                String engineCliPath = EngineAssistant.getEngineCliPath();
                String outputPath = EngineAssistant.getCheckResultPath(project);
                if (openSCASettingState.getOpenSCASetting().getUseCustomerCli()) {
                    engineCliPath = openSCASettingState.getOpenSCASetting().getCustomerPath();
                }
                File cli = new File(engineCliPath);
                if (!cli.exists() && !cli.isFile()) {
                    Notification.balloonNotify("请配置OpenSCA CLI后进行检测", NotificationType.ERROR);
                    return;
                }
                // 创建数据源配置文件
                makeConfigJsonFile(engineCliPath,openSCASettingState.getOpenSCASetting());

                String inputPath = project.getBasePath();

                String logFilePath = cli.getParent()+"/opensca.log";
                String[] cmd = {engineCliPath, "-url", url, "-token", token, "-path", inputPath, "-out", outputPath, "-vuln", "-cache","-log",logFilePath};
                Process process = Runtime.getRuntime().exec(cmd);
                processes.put(project, process);
                LOG.info("OpenSCA开始检测:------>"+ Arrays.toString(cmd));

                process.waitFor();
                // 记录检测日志
                InputStream inputStream = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder result = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                String executionResult = result.toString();
                LOG.info("OpenSCA检测结束:------>"+executionResult);


                // 解析结果
                overview.setEndTime(new Date());
                MutableTreeNode resultTree = ResultParser.parseResult(outputPath, overview);
                listener.onSuccess(resultTree);
            } catch (Exception e) {
                if(e instanceof InterruptedException){
                    Notification.balloonNotify("检测停止成功",NotificationType.INFORMATION);
                }else {
                    LOG.error(e);
                }
                listener.onError(e);
            } finally {
                processes.remove(project);
                listener.progress(false);
                status.put(project, stopped);
            }
        });
        if (isRunning(project)) {
            futures.put(project, future);
        }
    }

    /**
     * 停止检测
     *
     * @param project 当前项目
     */
    public static void stop(Project project) {
        futures.computeIfPresent(project, (p, f) -> {
            status.put(project, stopping);
            processes.get(project).destroyForcibly();
            f.cancel(true);
            return null;
        });
    }

    /**
     * 清除结果
     *
     * @param project
     * @param listener
     */
    public static void clean(Project project, CheckListener listener) {
        FileUtil.delete(new File(EngineAssistant.getCheckResultPath(project)));
        listener.clean();
    }

    /**
     * 当前项目是否有任务在运行
     *
     * @param project 当前项目
     * @return true：正在运行
     */
    public static boolean isRunning(Project project) {
        return running.equals(status.get(project));
    }

    public static boolean isStopped(Project project) {
        return stopped.equals(status.getOrDefault(project, stopped));
    }

    /**
     * 将数据源写入配置文件
     * 无配置文件下载配置文件 有配置文件更新配置文件
     * FileWriter很坑 创建对象默认清空文件(顺序不要发生改变)
     * @param engineCliPath
     */
    private static void makeConfigJsonFile(String engineCliPath, OpenSCASetting openSCASetting) throws IOException {
        try {
            String cliParentFilePath = new File(engineCliPath).getParentFile().getAbsolutePath();
            File configJsonFile = new File(cliParentFilePath + File.separator + "config.json");
            if (!configJsonFile.exists()) {
                HttpService.downloadCliConfig(configJsonFile);
            }
            List<DsnConfig> dsnConfigList = openSCASettingState.getOpenSCASetting().getDsnConfigList();
            if (CollectionUtils.isEmpty(dsnConfigList)) return;
            List<DsnConfig> collect = dsnConfigList.stream().filter(item -> item.getSelect().equals(Boolean.TRUE)).collect(Collectors.toList());
            String configJson = Origin.buildDsnJson(configJsonFile, collect);
            FileWriter fileWriter = new FileWriter(configJsonFile);
            fileWriter.write(configJson);
            fileWriter.close();
        } catch (Exception e) {
            LOG.error("创建配置文件失败:"+e);
            throw new SCAException(ErrorEnum.CREATE_FILE_ERROR);
        }

    }
}
