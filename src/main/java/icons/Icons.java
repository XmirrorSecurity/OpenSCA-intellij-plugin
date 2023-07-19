package icons;

import cn.xmirror.sca.common.constant.SecurityLevelEnum;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class Icons {
    // 主题
    public static final Icon XMIRROR_LOGO = IconLoader.getIcon("/icons/xmirror_logo.png");
    public static final Icon XMIRROR_LOGO_24 = IconLoader.getIcon("/icons/xmirror_logo_24.png");
    public static final Icon OSS_LOGO = IconLoader.getIcon("/icons/oss_logo.png");

    // 展示面板
    public static final Icon USE_SUGGESTION = IconLoader.getIcon("/icons/use_suggestion_20.svg");
    public static final Icon LICENSE = IconLoader.getIcon("/icons/license_20.svg");
    public static final Icon VUL_DESCRIPTION = IconLoader.getIcon("/icons/vul_description_20.svg");
    public static final Icon FIX_SUGGESTION = IconLoader.getIcon("/icons/fix_suggestion_20.svg");
    public static final Icon COMPONENT_STATISTICS = IconLoader.getIcon("/icons/fix_suggestion_20.svg");
    public static final Icon VUL_STATISTICS = IconLoader.getIcon("/icons/vul_statistics_20.svg");
    public static final Icon LICENSE_STATISTICS = IconLoader.getIcon("/icons/license_statistics_20.svg");

    // 危险级别
    public static final Icon CRITICAL = IconLoader.getIcon("/icons/severity_critical.svg");
    public static final Icon CRITICAL_24 = IconLoader.getIcon("/icons/severity_critical_24.svg");
    public static final Icon HIGH = IconLoader.getIcon("/icons/severity_high.svg");
    public static final Icon HIGH_24 = IconLoader.getIcon("/icons/severity_high_24.svg");
    public static final Icon LOW = IconLoader.getIcon("/icons/severity_low.svg");
    public static final Icon LOW_24 = IconLoader.getIcon("/icons/severity_low_24.svg");
    public static final Icon MEDIUM = IconLoader.getIcon("/icons/severity_medium.svg");
    public static final Icon MEDIUM_24 = IconLoader.getIcon("/icons/severity_medium_24.svg");
    private static final Icon NO_RATING = IconLoader.getIcon("/icons/severity_no_rating.svg");
    private static final Icon NO_RATING_24 = IconLoader.getIcon("/icons/severity_no_rating_24.svg");

    // 黑名单
    public static final Icon BLACKLIST = IconLoader.getIcon("/icons/severity_blacklist.svg");
    public static final Icon BLACKLIST_24 = IconLoader.getIcon("/icons/severity_blacklist_24.svg");

    // 检测
    public static final Icon SUCCEEDED = IconLoader.getIcon("/icons/succeeded.svg");
    public static final Icon FAILED = IconLoader.getIcon("/icons/failed.svg");

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
