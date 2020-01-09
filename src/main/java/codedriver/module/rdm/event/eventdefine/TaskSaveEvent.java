package codedriver.module.rdm.event.eventdefine;

import codedriver.module.rdm.event.core.Belong;
import codedriver.module.rdm.event.core.EventTemplate;
import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName TaskSaveEvent
 * @Description
 * @Auther
 * @Date 2020/1/7 11:53
 **/

public class TaskSaveEvent extends EventTemplate {

    public TaskSaveEvent(String _uniqueKey, JSONObject _param, String _objectUuid, String _belong) {
        super( _uniqueKey,  _param,  _objectUuid,  _belong);
    }

    @Override
    public String getName() {
        return "task_save";
    }

    @Override
    public String getDescription() {
        return "任务保存";
    }

}
