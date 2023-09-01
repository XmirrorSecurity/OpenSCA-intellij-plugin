package cn.xmirror.sca.common.util;

import cn.xmirror.sca.common.exception.ErrorEnum;
import cn.xmirror.sca.common.exception.SCAException;
import cn.xmirror.sca.service.HttpService;
import org.apache.commons.lang.StringUtils;
import org.jsoup.internal.StringUtil;

import java.util.regex.Pattern;

import static cn.xmirror.sca.service.HttpService.testConnectionUri;

/**
 * 验证工具类
 *
 * @author Yuan Shengjun
 */
public class VerifyUtils {

    private static final String urlPattern = "(http|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";

    public static void verifyUrl(String url) {
        if (StringUtil.isBlank(url)) {
            throw new SCAException(ErrorEnum.SETTING_URL_EMPTY_ERROR);
        }
        Pattern pattern = Pattern.compile(urlPattern);
        if (!pattern.matcher(url).matches()) {
            throw new SCAException(ErrorEnum.SETTING_URL_PATTERN_ERROR);
        }
    }

    public static void verifyToken(String url,String token) {
        if (StringUtils.isEmpty(token)) return;
        try {
            HttpService.getRequest(testConnectionUri, url, token, null, 5000);
        } catch (Exception e) {
            throw new SCAException(ErrorEnum.SETTING_TOKEN_EXPIRE);
        }

    }
}
