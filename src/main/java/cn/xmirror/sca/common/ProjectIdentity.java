package cn.xmirror.sca.common;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 项目标识，在.idea文件下生成uuid标识当前项目
 *
 * @author Yuan Shengjun
 */
public class ProjectIdentity {
    private static final String filename = "OpenSCAIdentity";

    /**
     * 返回当前项目标识，若没有标识则生成一个返回
     *
     * @param project 当前项目
     * @return 标识
     */
    public static String checkOrSignature(Project project) {
        // 保存在当前项目下的.idea目录中
        String path = getIdentityFilename(project);
        File file = new File(path);
        String projectSignature = null;
        try {
            projectSignature = FileUtil.loadFile(file);
        } catch (FileNotFoundException e) {
            projectSignature = sign(file, project);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return projectSignature;
    }

    /**
     * 签名
     *
     * @param file    签名保存位置
     * @param project 当前项目
     * @return
     */
    public static String sign(File file, Project project) {
        // 默认UUID，可以改变规则
        String signature = UUID.randomUUID().toString();
        try {
            FileUtil.writeToFile(file, signature);
            file.setReadOnly();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return signature;
    }

    /**
     * 获取签名保存位置，默认放在当前项目下.idea目录中
     *
     * @param project 当前项目
     * @return
     */
    public static String getIdentityFilename(Project project) {
        return project.getBasePath() + File.separator + ".idea" + File.separator + filename;
    }
}
