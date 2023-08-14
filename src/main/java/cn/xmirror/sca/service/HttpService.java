package cn.xmirror.sca.service;

import cn.xmirror.sca.common.OpenSCASettingState;
import cn.xmirror.sca.common.SCAThreadPool;
import cn.xmirror.sca.common.exception.ErrorEnum;
import cn.xmirror.sca.common.exception.SCAException;
import cn.xmirror.sca.common.util.HttpUtils;
import cn.xmirror.sca.common.util.VerifyUtils;
import cn.xmirror.sca.engine.EngineAssistant;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.util.io.FileUtil;
import org.apache.http.HttpStatus;
import org.jsoup.Connection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Http服务类
 *
 * @author Yuan Shengjun
 */
public class HttpService {

    private static final String testConnectionUri = "/oss-saas/api-v1/oss-token/test";
    private static final String downloadEngineUri = "/oss-saas/api-v1/ide-plugin/open-sca-cli/download";
    private static final String getEngineVersionUri = "/oss-saas/api-v1/ide-plugin/open-sca-cli/version";

    /**
     * 测试连接
     *
     * @param url      服务器地址
     * @param token    令牌
     * @param consumer 测试连接之后需要做的事
     */
    public static void testConnection(String url, String token, Consumer<String> consumer) {
        SCAThreadPool.getInstance().submit(() -> {
            String message = null;
            try {
                getRequest(testConnectionUri, url, token, null, 5000);
            } catch (Exception e) {
                message = e.getMessage();
            }
            consumer.accept(message != null ? message : "连接成功");
        });
    }

    /**
     * 下载引擎
     *
     * @param output 输出位置
     * @return
     */
    public static Void downloadEngine(String output) {
        Map<String, String> params = new HashMap<>();
        params.put("osName", EngineAssistant.getCurrentOsName());
        params.put("arch", EngineAssistant.getCurrentSystemArch());
        Connection.Response response = getRequest(downloadEngineUri, params, 300 * 1000);
        try {
            FileUtil.writeToFile(new File(output), response.bodyAsBytes());
        } catch (IOException e) {
            throw new SCAException(ErrorEnum.ENGINE_DOWNLOAD_ERROR, e.getMessage());
        }
        return null;
    }

    /**
     * 是否需要更新引擎
     *
     * @param versionPath 引擎版本保存路径
     * @return
     */
    public static boolean needUpdateEngine(String versionPath) {
        String version = getRemoteServerCliVersion();
        try {
            File versionFile = new File(versionPath);
            if (!versionFile.isFile() || !FileUtil.loadFile(versionFile).equals(version)) {
                FileUtil.writeToFile(versionFile, version);
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    public static String getRemoteServerCliVersion() {
        Connection.Response response = getRequest(getEngineVersionUri, null, 10000);
        JSONObject result = JSON.parseObject(response.body());
        return (String) result.get("data");
    }

    /**
     * Get请求
     *
     * @param uri    请求地址
     * @param params 请求参数
     * @return 响应体
     */
    private static Connection.Response getRequest(String uri, Map<String, String> params, Integer timeout) {
        String url = OpenSCASettingState.getInstance().getOpenSCASetting().getServerAddress();
        String token = OpenSCASettingState.getInstance().getOpenSCASetting().getToken();
        return getRequest(uri, url, token, params, timeout);
    }

    /**
     * Get请求
     *
     * @param uri    请求地址
     * @param url    服务器地址
     * @param token  令牌
     * @param params 其他参数
     * @return 响应体
     */
    private static Connection.Response getRequest(String uri, String url, String token, Map<String, String> params, Integer timeout) {
        try {
            VerifyUtils.verifyCertification(url, token);
            Connection.Response response = HttpUtils.get(url + uri + mergeParams(token, params), null, timeout);
            if (response.statusCode() != HttpStatus.SC_OK) {
                throw new SCAException(ErrorEnum.SERVER_REQUEST_FAILURE_ERROR, Integer.toString(response.statusCode()));
            }
            if (response.contentType() != null && response.contentType().contains("application/json")) {
                JSONObject result = JSON.parseObject(response.body());
                if ((int) result.get("code") != 0) {
                    throw new SCAException(ErrorEnum.SERVER_REQUEST_FAILURE_ERROR, result.get("message").toString());
                }
            }
            return response;
        } catch (IOException e) {
            throw new SCAException(ErrorEnum.SERVER_UNREACHABLE_ERROR, e.getMessage());
        }
    }

    /**
     * 合并参数。将参数拼接成get请求的参数
     *
     * @param token  令牌
     * @param params 参数
     * @return
     */
    public static String mergeParams(String token, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("?ossToken=").append(token);
        if (params != null && !params.isEmpty()) {
            params.forEach((k, v) -> sb.append("&").append(k).append("=").append(v));
        }
        return sb.toString();
    }
}
