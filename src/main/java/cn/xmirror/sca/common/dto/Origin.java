package cn.xmirror.sca.common.dto;

import cn.xmirror.sca.common.pojo.DsnConfig;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * 配置文件 config.json
 * @author xingluheng
 * @date 2023/08/14 19:00
 **/
@Data
public class Origin {

    public static List<String> originTypeList = new ArrayList<>() {{
        add("--请选择数据源类型--");
        add("MySQL");
        add("SQL Server");
        add("SQLite");
        add("PostgreSQL");
        add("JSON");
    }};

    /**
     * 替换数据源配置文件
     * @param dsnConfigList
     * @return
     */
    public static String buildDsnJson(File configJsonFile, List<DsnConfig> dsnConfigList) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(configJsonFile.getAbsolutePath()));
        String content = new String(bytes, StandardCharsets.UTF_8);
        JSONObject configObj= JSONObject.parseObject(content);
        JSONObject origin = configObj.getJSONObject("origin");
        origin.clear();
        for (DsnConfig dsnConfig : dsnConfigList) {
            JSONObject object = new JSONObject();
            object.put("dsn",dsnConfig.getDsn());
            if (StringUtils.isNotEmpty(dsnConfig.getTableName())){
                object.put("table",dsnConfig.getTableName());
            }
            origin.put(dsnConfig.getType().toLowerCase(Locale.ROOT),object);
        }
        return configObj.toString();
    }

}
