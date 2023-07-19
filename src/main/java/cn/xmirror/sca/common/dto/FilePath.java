package cn.xmirror.sca.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author xingluheng
 * @date 2023/07/18 16:19
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilePath implements Serializable {
    private String path;

    @Override
    public String toString() {
        return path;
    }
}
