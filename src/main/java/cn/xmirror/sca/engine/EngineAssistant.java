package cn.xmirror.sca.engine;

import cn.xmirror.sca.common.ProjectIdentity;
import cn.xmirror.sca.common.constant.EngineOsEnum;
import cn.xmirror.sca.common.exception.ErrorEnum;
import cn.xmirror.sca.common.exception.SCAException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;

import java.io.File;

/**
 * 引擎辅助类
 *
 * @author Yuan Shengjun
 */
public class EngineAssistant {
    private static final String defaultSCADirectory = System.getProperty("user.home") + File.separator + "OpenSCA";
    private static final String defaultEngineDirectory = defaultSCADirectory + File.separator + "engine";
    private static final String defaultEngineCliDirectory = defaultEngineDirectory + File.separator + "cli";
    private static final String defaultResultDataDirectory = defaultEngineDirectory + File.separator + "data";

    /**
     * 获取引擎脚本所在路径。默认路径是~/OpenSCA/engine/cli/xxx
     *
     * @return 引擎脚本所在路径。如果不持支当前操作系统，返回null
     */
    public static String getEngineCliPath() {
        String engineCliName = getEngineCliName();
        return defaultEngineCliDirectory + File.separator + engineCliName;
    }

    /**
     * 获取支持当前操作系统的引擎名称
     *
     * @return 引擎名称。如果不持支当前操作系统，抛出异常
     */
    public static String getEngineCliName() {
        EngineOsEnum currentEngineOs = EngineOsEnum.getEngineOsEnum();
        if (currentEngineOs == null) {
            throw new SCAException(ErrorEnum.ENGINE_UNSUPPORTED_SYSTEM_ERROR);
        }
        return currentEngineOs.getCliName();
    }

    /**
     * 获取当前操作系统简洁名称
     *
     * @return
     */
    public static String getCurrentOsName() {
        EngineOsEnum currentEngineOs = EngineOsEnum.getEngineOsEnum();
        if (currentEngineOs == null) {
            throw new SCAException(ErrorEnum.ENGINE_UNSUPPORTED_SYSTEM_ERROR);
        }
        return currentEngineOs.getOsName();
    }

    /**
     * 获取当前操作系统架构
     *
     * @return
     */
    public static String getCurrentSystemArch() {
        return System.getProperty("os.arch").toLowerCase();
    }

    /**
     * 获取检测结果保存路径
     *
     * @return
     */
    public static String getCheckResultPath(Project project) {
        String dirPath = EngineAssistant.getDefaultResultDataDirectory();
        if (!FileUtil.createDirectory(new File(dirPath))) {
            throw new SCAException(ErrorEnum.CREATE_DIR_ERROR, dirPath);
        }
        return dirPath + File.separator + ProjectIdentity.checkOrSignature(project) + ".json";
    }

    /**
     * 获取引擎版本路径
     *
     * @return
     */
    public static String getEngineVersionPath() {
        String versionPath = defaultEngineCliDirectory + File.separator + "version";
        FileUtil.createParentDirs(new File(versionPath));
        return versionPath;
    }

    /**
     * 获取存放引擎的默认目录路径
     *
     * @return 存放引擎的默认目录路径
     */
    public static String getDefaultEngineCliDirectory() {
        return defaultEngineCliDirectory;
    }

    /**
     * 获取存放用户数据的默认目录路径
     *
     * @return 存放用户数据的默认目录路径
     */
    public static String getDefaultResultDataDirectory() {
        return defaultResultDataDirectory;
    }

    /**
     * 获取检测结果保存路径
     * @param engineCliPath 自定义cli路径
     * @return
     */
    public static String getCustomerCheckResultPath(String engineCliPath) {
        String basePath =  new File(engineCliPath).getParent();
        return basePath + File.separator + "output.json";
    }
}
