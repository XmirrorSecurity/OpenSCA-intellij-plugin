package cn.xmirror.sca.common.dto;

import cn.xmirror.sca.common.pojo.DsnConfig;
import cn.xmirror.sca.common.pojo.OpenSCASetting;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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
     * @param openSCASetting
     * @return
     */
    public static String buildDsnJson(File configJsonFile, OpenSCASetting openSCASetting) throws IOException {
        List<DsnConfig> dsnConfigList = openSCASetting.getDsnConfigList();
        // 收集已勾选的配置文件 写入JSON
        dsnConfigList = dsnConfigList.stream().filter(item -> item.getSelect().equals(Boolean.TRUE)).collect(Collectors.toList());

        byte[] bytes = Files.readAllBytes(Paths.get(configJsonFile.getAbsolutePath()));
        String content = new String(bytes, StandardCharsets.UTF_8);
        JSONObject configObj= JSONObject.parseObject(content);

        // 设置进度展示为false
        JSONObject optional = configObj.getJSONObject("optional");
        optional.put("progress",false);


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
        if (openSCASetting.getRemoteDataSourceSelected()){
            origin.put("url",openSCASetting.getServerAddress());
            origin.put("token",openSCASetting.getToken());
        }
        return configObj.toString();
    }

}
