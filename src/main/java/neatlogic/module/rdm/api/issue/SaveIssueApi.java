/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.module.rdm.api.issue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.auth.core.AuthAction;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.rdm.auth.label.RDM_BASE;
import neatlogic.framework.rdm.dto.AppAttrVo;
import neatlogic.framework.rdm.dto.AppStatusRelVo;
import neatlogic.framework.rdm.dto.IssueAttrVo;
import neatlogic.framework.rdm.dto.IssueVo;
import neatlogic.framework.rdm.enums.IssueGroupSearch;
import neatlogic.framework.rdm.enums.IssueRelType;
import neatlogic.framework.rdm.enums.ProjectUserType;
import neatlogic.framework.rdm.exception.ProjectNotAuthIssueException;
import neatlogic.framework.restful.annotation.*;
import neatlogic.framework.restful.constvalue.OperationTypeEnum;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentBase;
import neatlogic.module.rdm.auth.ProjectAuthManager;
import neatlogic.module.rdm.dao.mapper.AppMapper;
import neatlogic.module.rdm.dao.mapper.AttrMapper;
import neatlogic.module.rdm.dao.mapper.IssueMapper;
import neatlogic.module.rdm.service.IssueService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@AuthAction(action = RDM_BASE.class)
@OperationType(type = OperationTypeEnum.UPDATE)
@Transactional
public class SaveIssueApi extends PrivateApiComponentBase {

    @Resource
    private AttrMapper attrMapper;
    @Resource
    private IssueMapper issueMapper;

    @Resource
    private AppMapper appMapper;

    @Resource
    private IssueService issueService;


    @Override
    public String getName() {
        return "nmrai.saveissueapi.getname";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({
            @Param(name = "id", type = ApiParamType.LONG, desc = "nmrai.saveissueapi.input.param.desc.id"),
            @Param(name = "fromId", type = ApiParamType.LONG, desc = "nmrai.searchissueapi.input.param.desc.fromid"),
            @Param(name = "toId", type = ApiParamType.LONG, desc = "nmrai.searchissueapi.input.param.desc.toid"),
            @Param(name = "relType", type = ApiParamType.ENUM, member = IssueRelType.class, desc = "common.reltype"),
            @Param(name = "parentId", type = ApiParamType.LONG, desc = "term.rdm.parenttaskid"),
            @Param(name = "appId", type = ApiParamType.LONG, desc = "nmraa.getappapi.input.param.desc", isRequired = true),
            @Param(name = "name", type = ApiParamType.STRING, isRequired = true, maxLength = 50, desc = "nmrai.saveissueapi.input.param.desc.name"),
            @Param(name = "priority", type = ApiParamType.LONG, desc = "common.priority"),
            @Param(name = "iteration", type = ApiParamType.LONG, desc = "common.iteration"),
            @Param(name = "catalog", type = ApiParamType.LONG, desc = "common.catalog"),
            @Param(name = "tagList", type = ApiParamType.JSONARRAY, desc = "common.tag"),
            @Param(name = "status", type = ApiParamType.LONG, desc = "common.status"),
            @Param(name = "attrList", type = ApiParamType.JSONARRAY, desc = "nmrai.saveissueapi.input.param.desc.attrlist"),
            @Param(name = "userIdList", type = ApiParamType.JSONARRAY, desc = "common.userlist"),
            @Param(name = "comment", type = ApiParamType.STRING, desc = "common.comment")})
    @Output({@Param(name = "id", type = ApiParamType.LONG, desc = "term.rdm.issueid")})
    @ResubmitInterval
    @Description(desc = "nmrai.saveissueapi.getname")
    @Override
    public Object myDoService(JSONObject paramObj) {
        Long appId = paramObj.getLong("appId");
        if (!ProjectAuthManager.checkAppAuth(appId, ProjectUserType.MEMBER, ProjectUserType.OWNER, ProjectUserType.LEADER)) {
            throw new ProjectNotAuthIssueException();
        }
        IssueVo issueVo = JSON.toJavaObject(paramObj, IssueVo.class);
        issueVo.setCreateUser(UserContext.get().getUserUuid(true));
        issueVo.formatAttr();
        Long id = paramObj.getLong("id");
        List<AppAttrVo> appAttrList = attrMapper.getAttrByAppId(issueVo.getAppId());
        //补充页面没有提供的自定义属性
        for (AppAttrVo appAttrVo : appAttrList) {
            if (appAttrVo.getIsPrivate().equals(0)) {
                if (issueVo.getAttr(appAttrVo.getId()) == null) {
                    issueVo.addAttr(new IssueAttrVo(appAttrVo.getId(), issueVo.getId(), appAttrVo.getType(), appAttrVo.getConfig()));
                } else {
                    issueVo.getAttr(appAttrVo.getId()).setAttrType(appAttrVo.getType()).setConfig(appAttrVo.getConfig());
                }
            }
        }

        //自动替换关系配置中的处理人
        if (issueVo.getStatus() != null) {
            Long oldIssueId = 0L;
            if (id != null) {
                oldIssueId = issueMapper.getIssueStatusById(id);
            }
            oldIssueId = oldIssueId == null ? 0L : oldIssueId;
            if (!issueVo.getStatus().equals(oldIssueId)) {
                AppStatusRelVo appStatusRelVo = new AppStatusRelVo();
                appStatusRelVo.setFromStatusId(oldIssueId);
                appStatusRelVo.setToStatusId(issueVo.getStatus());
                appStatusRelVo.setAppId(issueVo.getAppId());
                AppStatusRelVo rel = appMapper.getAppStatusRel(appStatusRelVo);
                if (rel != null && MapUtils.isNotEmpty(rel.getConfig()) && rel.getConfig().containsKey("userList")) {
                    List<String> userIdList = new ArrayList<>();
                    for (int i = 0; i < rel.getConfig().getJSONArray("userList").size(); i++) {
                        JSONObject userObj = rel.getConfig().getJSONArray("userList").getJSONObject(i);
                        userIdList.add(userObj.getString("value").replace(IssueGroupSearch.PROJECTUSERTYPE.getValue() + "#", ""));
                    }
                    issueVo.setUserIdList(userIdList);
                }
            }
        }

        issueService.saveIssue(issueVo);
        return issueVo.getId();
    }

    @Override
    public String getToken() {
        return "/rdm/issue/save";
    }

}
