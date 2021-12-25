package cn.xmirror.sca.common.dto;

import lombok.Data;

import java.util.Date;

/**
 * 概述实体类
 *
 * @author Yuan Shengjun
 */
@Data
public class Overview {
    /**
     * 状态码
     */
    private Integer status;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束之间
     */
    private Date endTime;

    /**
     * 组件风险统计(Component Security Statistics)
     * <ui>
     * <li>css[0]：严重</li>
     * <li>css[1]：高危</li>
     * <li>css[2]：中危</li>
     * <li>css[3]：低危</li>
     * </ui>
     */
    private int[] css = new int[4];

    /**
     * 漏洞风险统计(Vulnerability Security Statistics)
     * <ui>
     * <li>vss[0]：严重</li>
     * <li>vss[1]：高危</li>
     * <li>vss[2]：中危</li>
     * <li>vss[3]：低危</li>
     * </ui>
     */
    private int[] vss = new int[4];
}
