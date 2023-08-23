package cn.xmirror.sca.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author xingluheng
 * @date 2023/08/14 10:11
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DsnConfig {
    /**
     * 是否使用
     */
    private Boolean select;
    /**
     * 数据源名称
     */
    private String type;
    /**
     * 数据源地址
     */
    private String dsn;
    /**
     * 表名称
     */
    private String tableName;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DsnConfig)) {
            return false;
        }
        DsnConfig other = (DsnConfig) o;
        return Objects.equals(select, other.select) &&
                Objects.equals(type, other.type) &&
                Objects.equals(tableName,tableName) &&
                Objects.equals(dsn, other.dsn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(select, type, dsn);
    }
}
