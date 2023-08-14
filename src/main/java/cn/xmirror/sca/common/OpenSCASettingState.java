package cn.xmirror.sca.common;

import cn.xmirror.sca.common.pojo.OpenSCASetting;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 持久化配置数据
 * @author xingluheng
 * @date 2023/07/19 14:27
 **/
@State(name = "cn.xmirror.sca.common.OpenSCASettingState",storages = @Storage("OpenSCAConfig.xml"))
public class OpenSCASettingState implements PersistentStateComponent<OpenSCASettingState> {

    @Setter
    @Getter
    private OpenSCASetting openSCASetting;

    public static OpenSCASettingState getInstance(){
        return ApplicationManager.getApplication().getService(OpenSCASettingState.class);
    }

    @Override
    public @Nullable OpenSCASettingState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull OpenSCASettingState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
