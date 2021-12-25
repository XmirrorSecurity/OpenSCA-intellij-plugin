package cn.xmirror.sca.common;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;

/**
 * 存储配置信息
 *
 * @author Yuan Shengjun
 */
public class SettingStateSafe {

    public static final String KEY = "OpenSCASecurityKey";

    /**
     * 存储url和token信息
     *
     * @param url
     * @param token
     */
    public static void storeCredentials(String url, String token) {
        PasswordSafe.getInstance().set(createCredentialAttributes(KEY), new Credentials(url, token));
    }

    public static String getUrl(String key) {
        Credentials credentials = PasswordSafe.getInstance().get(createCredentialAttributes(key));
        return credentials != null ? credentials.getUserName() : "";
    }

    public static String getToken(String key) {
        return PasswordSafe.getInstance().getPassword(createCredentialAttributes(key));
    }

    private static CredentialAttributes createCredentialAttributes(String key) {
        return new CredentialAttributes(CredentialAttributesKt.generateServiceName("OpenSCASystem", key));
    }
}
