package codedriver.module.rdm.api.project;

import codedriver.framework.restful.core.ApiComponentBase;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

/**
 * @ClassName ProjectDeleteApi
 * @Description
 * @Auther r2d2
 * @Date 2019/12/4 9:52
 **/
@Service
public class ProjectDeleteApi extends ApiComponentBase {

    @Override
    public String getToken() {
        return "module/rdm/project/delete";
    }

    @Override
    public String getName() {
        return "查询项目接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        return null;
    }


}
