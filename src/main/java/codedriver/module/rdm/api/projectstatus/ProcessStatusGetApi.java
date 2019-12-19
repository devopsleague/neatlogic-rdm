package codedriver.module.rdm.api.projectstatus;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.core.ApiComponentBase;
import codedriver.module.rdm.dao.mapper.ProjectWorkflowMapper;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName ProcessStatusGetApi
 * @Description 根据uuid查询状态接口
 * @Auther
 * @Date 2019/12/4 9:52
 **/
@Service
public class ProcessStatusGetApi extends ApiComponentBase {

    @Resource
    private ProjectWorkflowMapper projectWorkflowMapper;

    @Override
    public String getToken() {
        return "module/rdm/projectstatus/get";
    }

    @Override
    public String getName() {
        return "根据uuid查询状态接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({ @Param(name = "uuid", type = ApiParamType.STRING, desc = "优先级uuid", isRequired = true)})
    @Description(desc = "根据uuid查询状态接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        JSONObject result = new JSONObject();
        String uuid = jsonObj.getString("uuid");
        result.put("projectStatus", projectWorkflowMapper.getProjectWorkflowStatusByUuid(uuid));
        return result;
    }


}
