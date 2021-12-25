package cn.xmirror.sca.service;

import cn.xmirror.sca.common.CheckListener;
import cn.xmirror.sca.common.ResultParser;
import cn.xmirror.sca.common.SCAThreadPool;
import cn.xmirror.sca.common.SettingStateSafe;
import cn.xmirror.sca.common.dto.Overview;
import cn.xmirror.sca.common.util.VerifyUtils;
import cn.xmirror.sca.engine.EngineAssistant;
import cn.xmirror.sca.engine.EngineDownloader;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;

import javax.swing.tree.MutableTreeNode;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * 检测服务类
 *
 * @author Yuan Shengjun
 */
public class CheckService {
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

    /**
     * 开始检测
     *
     * @param project  当前项目
     * @param listener 检测监听器
     */
    public static void run(Project project, CheckListener listener) {
        status.put(project, running);
        Overview overview = new Overview();
        overview.setStartTime(new Date());

        Future<?> future = SCAThreadPool.getInstance().submit(() -> {
            try {
                listener.progress(true);
                clean(project, listener);
                // 检查引擎
                EngineDownloader.checkAndDownload(project);
                String url = SettingStateSafe.getUrl(SettingStateSafe.KEY);
                String token = SettingStateSafe.getToken(SettingStateSafe.KEY);
                VerifyUtils.verifyCertification(url, token);
                // 提交任务
                String engineCliPath = EngineAssistant.getEngineCliPath();
                String inputPath = project.getBasePath();
                String outputPath = EngineAssistant.getCheckResultPath(project);
                String[] cmd = {engineCliPath, "-ip", url, "-token", token, "-path", inputPath, "-out", outputPath, "-vuln", "-cache"};
                Runtime.getRuntime().exec(cmd).waitFor();
                // 解析结果
                overview.setEndTime(new Date());
                MutableTreeNode resultTree = ResultParser.parseResult(outputPath, overview);
                listener.onSuccess(resultTree);
            } catch (Exception e) {
                listener.onError(e);
            } finally {
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
}
