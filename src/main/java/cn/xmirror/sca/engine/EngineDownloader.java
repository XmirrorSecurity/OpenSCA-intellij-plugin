package cn.xmirror.sca.engine;

import cn.xmirror.sca.service.HttpService;
import cn.xmirror.sca.common.exception.ErrorEnum;
import cn.xmirror.sca.common.exception.SCAException;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;

import java.io.File;

/**
 * 下载引擎
 */
public class EngineDownloader {

    /**
     * 检测本地引擎，如果不存在则下载
     *
     * @param project 当前项目
     */
    public static void checkAndDownload(Project project) {
        // 检测引擎是否存在
        String engineCliPath = EngineAssistant.getEngineCliPath();
        File engineCli = new File(engineCliPath);
        if (HttpService.needUpdateEngine(EngineAssistant.getEngineVersionPath()) || !engineCli.isFile()) {
            // 创建目录
            String engineCliDirectory = EngineAssistant.getDefaultEngineCliDirectory();
            if (!FileUtil.createDirectory(new File(engineCliDirectory))) {
                throw new SCAException(ErrorEnum.CREATE_DIR_ERROR, engineCliDirectory);
            }
            // 下载引擎
            if (engineCli.isDirectory()) {
                throw new SCAException(ErrorEnum.ENGINE_DOWNLOAD_ERROR);
            }
            ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> HttpService.downloadEngine(engineCli.getAbsolutePath()), "更新/下载OpenSCA命令行工具", false, project);
        }
        // 设置引擎执行权限
        if (!engineCli.canExecute() && !engineCli.setExecutable(true)) {
            throw new SCAException(ErrorEnum.ENGINE_SET_EXECUTABLE_ERROR, engineCliPath);
        }
    }
}
