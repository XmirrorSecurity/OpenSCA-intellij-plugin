package cn.xmirror.sca.service;

import cn.xmirror.sca.common.OpenSCASettingState;
import cn.xmirror.sca.common.SCAThreadPool;
import cn.xmirror.sca.common.exception.ErrorEnum;
import cn.xmirror.sca.common.exception.SCAException;
import cn.xmirror.sca.common.pojo.OpenSCASetting;
import cn.xmirror.sca.common.util.HttpUtils;
import cn.xmirror.sca.engine.EngineAssistant;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import org.apache.commons.lang.StringUtils;
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

    private static final Logger LOG = Logger.getInstance(HttpService.class);


    private static final String BaseUrlRequest = "https://opensca.xmirror.cn";
    private static final String testConnectionUri = "/oss-saas/api-v1/oss-token/test";
    private static final String authUri = "/oss-saas/api-v1/oss-token/get/auth";
    private static final String downloadEngineUri = "/oss-saas/api-v1/ide-plugin/open-sca-cli/download";
    private static final String getEngineVersionUri = "/oss-saas/api-v1/ide-plugin/open-sca-cli/version";
    private static final String downloadCliConfig = "/oss-saas/api-v1/ide-plugin/open-sca-cli/config/download";
    private static final Integer sleepTime = 1500;

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

    public static String getAuthToken(String url,String param) throws IOException {
        Connection.Response response = HttpUtils.get(url + authUri+"/"+param);
        JSONObject result = JSON.parseObject(response.body());
        if (!result.get("code").equals(0)){
            throw new SCAException(ErrorEnum.SERVER_REQUEST_FAILURE_ERROR);
        }
        return (String) result.get("data");
    }

    /**
     * 下载引擎
     *
     * @param output 输出位置
     * @return
     */
    public static void downloadEngine(String output) {
        long beginTime = System.currentTimeMillis();
        Map<String, String> params = new HashMap<>();
        params.put("osName", EngineAssistant.getCurrentOsName());
        params.put("arch", EngineAssistant.getCurrentSystemArch());
        Connection.Response response = getRequest(downloadEngineUri, params, 300 * 1000);
        try {
            FileUtil.writeToFile(new File(output), response.bodyAsBytes());
            // 太快了 让慢一点
            if (System.currentTimeMillis()-beginTime < sleepTime) {
                Thread.sleep(sleepTime-System.currentTimeMillis()-beginTime);
            }
        } catch (IOException | InterruptedException e) {
            LOG.error(e);
            throw new SCAException(ErrorEnum.ENGINE_DOWNLOAD_ERROR, e.getMessage());
        }
    }

    /**
     * 下载默认配置文件
     * @param configJsonFile
     */
    public static void downloadCliConfig(File configJsonFile) {
        Connection.Response response = getRequest(downloadCliConfig, null, 300 * 1000);
        try {
            FileUtil.writeToFile(configJsonFile, response.bodyAsBytes(),true);
        } catch (IOException e) {
            LOG.error(e);
            throw new SCAException(ErrorEnum.ENGINE_DOWNLOAD_ERROR, e.getMessage());
        }
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
     * Get请求(去除Token校验)
     *
     * @param uri    请求地址
     * @param params 请求参数
     * @return 响应体
     */
    private static Connection.Response getRequest(String uri, Map<String, String> params, Integer timeout) {
        OpenSCASetting openSCASetting = OpenSCASettingState.getInstance().getOpenSCASetting();
        String url = BaseUrlRequest;
        if (openSCASetting!=null){
            if (StringUtils.isEmpty(openSCASetting.getServerAddress())) {
                url = openSCASetting.getServerAddress();
            }
        }
        return getRequest(uri, url, null, params, timeout);
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

        if (StringUtils.isNotEmpty(token)) {
            sb.append("?ossToken=").append(token);
        }

        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append("&").append(key).append("=").append(value);
            }
        }

        if (StringUtils.isEmpty(token) && sb.length() > 0) {
            sb.replace(0, 1, "?");
        }

        return sb.toString();
    }
}
