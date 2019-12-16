package codedriver.module.rdm.api.projectstatus;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.core.ApiComponentBase;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

/**
 * @ClassName ProjectStatusDeleteApi
 * @Description
 * @Auther
 * @Date 2019/12/4 9:52
 **/
@Service
public class ProjectStatusDeleteApi extends ApiComponentBase {

    @Override
    public String getToken() {
        return "module/rdm/projectstatus/delete";
    }

    @Override
    public String getName() {
        return "删除项目状态接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({
            @Param(name = "uuid", type = ApiParamType.STRING, desc = "状态uuid", isRequired = true) })
    @Description(desc="删除项目状态接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        return null;
    }


}
