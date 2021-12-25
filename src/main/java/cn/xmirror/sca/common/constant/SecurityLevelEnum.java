package cn.xmirror.sca.common.constant;

import java.util.Set;

/**
 * 风险等级枚举类
 *
 * @author Yuan Shengjun
 */
public enum SecurityLevelEnum {
    CRITICAL(1, "严重"),
    HIGH(2, "高危"),
    MEDIUM(3, "中危"),
    LOW(4, "低危"),
    ;

    private final int level;
    private final String tag;

    SecurityLevelEnum(int level, String tag) {
        this.level = level;
        this.tag = tag;
    }

    public int getLevel() {
        return level;
    }

    public String getTag() {
        return tag;
    }

    public static void statistics(int level, int[] statistics) {
        if (level == CRITICAL.level) {
            statistics[0] += 1;
        } else if (level == HIGH.level) {
            statistics[1] += 1;
        } else if (level == MEDIUM.level) {
            statistics[2] += 1;
        } else if (level == LOW.level) {
            statistics[3] += 1;
        }
    }
}
