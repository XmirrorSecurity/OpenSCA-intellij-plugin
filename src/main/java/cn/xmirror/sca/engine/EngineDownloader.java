package cn.xmirror.sca.engine;

import cn.xmirror.sca.common.exception.ErrorEnum;
import cn.xmirror.sca.common.exception.SCAException;
import cn.xmirror.sca.service.HttpService;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;

import java.io.*;

/**
 * 下载引擎
 */
public class EngineDownloader {


    /**
     * 检查更新最新版本的cli
     * @param engineCliPath
     * @param project
     * @throws IOException
     * @throws InterruptedException
     */
    public static void checkAndUpdateToLatestVersion(String engineCliPath,Project project) throws IOException, InterruptedException {
        String remoteServerCliVersion = HttpService.getRemoteServerCliVersion().trim();

        String localCliVersion = getLocalCliVersion(engineCliPath).trim();
        if (remoteServerCliVersion.equals(localCliVersion)) return;
        // 版本号不一致 更新至最新版
        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
            HttpService.downloadEngine(engineCliPath);
            File engineCli = new File(engineCliPath);
            if (!engineCli.canExecute() && !engineCli.setExecutable(true)) {
                throw new SCAException(ErrorEnum.ENGINE_SET_EXECUTABLE_ERROR, engineCliPath);
            }
            try {
                createCliConfig(engineCliPath);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "更新/下载OpenSCA命令行工具", false, project);
    }

    /**
     * 创建当前版本cli的 配置文件
     * @param engineCliPath
     * @throws IOException
     * @throws InterruptedException
     */
    public static String createCliConfig(String engineCliPath) throws IOException, InterruptedException {
        File cliFile = new File(engineCliPath);

        String configJsonFile = cliFile.getParent() +File.separator + "config.json";
        File file = new File(configJsonFile);
        if (file.exists()) file.delete();

        String[] cmd = {engineCliPath, "-config",configJsonFile};
        Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();
        return configJsonFile;
    }


    /**
     * 获取当前版本cli的版本号
     * @param engineCliPath
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static String getLocalCliVersion(String engineCliPath) throws IOException, InterruptedException {
        String[] cmd = {engineCliPath, "-version"};
        Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();

        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }
        return result.toString();
    }
}
