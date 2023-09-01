package icons;

import cn.xmirror.sca.common.constant.SecurityLevelEnum;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class Icons {
    /**
     * 主题
     */
    public static final Icon XMIRROR_LOGO = IconLoader.getIcon("/icons/xmirror_logo.png",Icons.class);
    public static final Icon XMIRROR_LOGO_24 = IconLoader.getIcon("/icons/xmirror_logo_24.png",Icons.class);
    public static final Icon OPEN_SCA_LOGO = IconLoader.getIcon("/icons/open_sca.svg",Icons.class);

    /**
     * 展示面板
     */
    public static final Icon USE_SUGGESTION = IconLoader.getIcon("/icons/use_suggestion_20.svg",Icons.class);
    public static final Icon LICENSE = IconLoader.getIcon("/icons/license_20.svg",Icons.class);
    public static final Icon VUL_DESCRIPTION = IconLoader.getIcon("/icons/vul_description_20.svg",Icons.class);
    public static final Icon FIX_SUGGESTION = IconLoader.getIcon("/icons/fix_suggestion_20.svg",Icons.class);
    public static final Icon COMPONENT_STATISTICS = IconLoader.getIcon("/icons/fix_suggestion_20.svg",Icons.class);
    public static final Icon VUL_STATISTICS = IconLoader.getIcon("/icons/vul_statistics_20.svg",Icons.class);
    public static final Icon LICENSE_STATISTICS = IconLoader.getIcon("/icons/license_statistics_20.svg",Icons.class);

    /**
     * 危险级别
     */
    public static final Icon CRITICAL = IconLoader.getIcon("/icons/severity_critical.svg",Icons.class);
    public static final Icon CRITICAL_24 = IconLoader.getIcon("/icons/severity_critical_24.svg",Icons.class);
    public static final Icon HIGH = IconLoader.getIcon("/icons/severity_high.svg",Icons.class);
    public static final Icon HIGH_24 = IconLoader.getIcon("/icons/severity_high_24.svg",Icons.class);
    public static final Icon LOW = IconLoader.getIcon("/icons/severity_low.svg",Icons.class);
    public static final Icon LOW_24 = IconLoader.getIcon("/icons/severity_low_24.svg",Icons.class);
    public static final Icon MEDIUM = IconLoader.getIcon("/icons/severity_medium.svg",Icons.class);
    public static final Icon MEDIUM_24 = IconLoader.getIcon("/icons/severity_medium_24.svg",Icons.class);
    private static final Icon NO_RATING = IconLoader.getIcon("/icons/severity_no_rating.svg",Icons.class);
    private static final Icon NO_RATING_24 = IconLoader.getIcon("/icons/severity_no_rating_24.svg",Icons.class);

    /**
     * 黑名单
     */
    public static final Icon BLACKLIST = IconLoader.getIcon("/icons/severity_blacklist.svg",Icons.class);
    public static final Icon BLACKLIST_24 = IconLoader.getIcon("/icons/severity_blacklist_24.svg",Icons.class);

    /**
     * 检测相关
     */
    public static final Icon SUCCEEDED = IconLoader.getIcon("/icons/succeeded.svg",Icons.class);
    public static final Icon FAILED = IconLoader.getIcon("/icons/failed.svg",Icons.class);
    public static final Icon WARNING = IconLoader.getIcon("/icons/warning.svg",Icons.class);
    public static final Icon REFRESH = IconLoader.getIcon("/icons/refresh.svg",Icons.class);

    public static Icon getIconFromResources(int securityLevel) {
        return getIconFromResources(securityLevel, IconSize.SIZE16);
    }

    public static Icon getIconFromResources(int securityLevel, IconSize iconSize) {
        Icon icon = null;
        if (securityLevel == SecurityLevelEnum.CRITICAL.getLevel()) {
            if (iconSize == IconSize.SIZE16) icon = CRITICAL;
            if (iconSize == IconSize.SIZE24) icon = CRITICAL_24;
        } else if (securityLevel == SecurityLevelEnum.HIGH.getLevel()) {
            if (iconSize == IconSize.SIZE16) icon = HIGH;
            if (iconSize == IconSize.SIZE24) icon = HIGH_24;
        } else if (securityLevel == SecurityLevelEnum.MEDIUM.getLevel()) {
            if (iconSize == IconSize.SIZE16) icon = MEDIUM;
            if (iconSize == IconSize.SIZE24) icon = MEDIUM_24;
        } else if (securityLevel == SecurityLevelEnum.LOW.getLevel()) {
            if (iconSize == IconSize.SIZE16) icon = LOW;
            if (iconSize == IconSize.SIZE24) icon = LOW_24;
        }
        return icon;
    }

    public enum IconSize {
        SIZE16, SIZE24
    }
}
