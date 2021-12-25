package cn.xmirror.sca.common.util;

import cn.xmirror.sca.common.exception.ErrorEnum;
import cn.xmirror.sca.common.exception.SCAException;
import org.jsoup.internal.StringUtil;

import java.util.regex.Pattern;

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

    public static void verifyCertification(String url, String token) {
        verifyUrl(url);
        if (StringUtil.isBlank(token)) {
            throw new SCAException(ErrorEnum.SETTING_TOKEN_EMPTY_ERROR);
        }
    }
}
