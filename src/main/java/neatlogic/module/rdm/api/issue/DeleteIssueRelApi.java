/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.module.rdm.api.issue;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.auth.core.AuthAction;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.rdm.auth.label.RDM_BASE;
import neatlogic.framework.restful.annotation.Description;
import neatlogic.framework.restful.annotation.Input;
import neatlogic.framework.restful.annotation.OperationType;
import neatlogic.framework.restful.annotation.Param;
import neatlogic.framework.restful.constvalue.OperationTypeEnum;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentBase;
import neatlogic.module.rdm.dao.mapper.IssueMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@AuthAction(action = RDM_BASE.class)
@OperationType(type = OperationTypeEnum.DELETE)
@Transactional
public class DeleteIssueRelApi extends PrivateApiComponentBase {
    @Resource
    private IssueMapper issueMapper;

    @Override
    public String getName() {
        return "删除任务关联关系";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({
            @Param(name = "fromId", type = ApiParamType.LONG, isRequired = true, desc = "来源任务id"),
            @Param(name = "toId", type = ApiParamType.LONG, isRequired = true, desc = "目标任务id")
    })
    @Description(desc = "删除任务关联关系接口")
    @Override
    public Object myDoService(JSONObject paramObj) {
        issueMapper.deleteIssueRel(paramObj.getLong("fromId"), paramObj.getLong("toId"));
        return null;
    }

    @Override
    public String getToken() {
        return "/rdm/issue/rel/delete";
    }

}
