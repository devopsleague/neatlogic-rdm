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

package neatlogic.module.rdm.api.project;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.auth.core.AuthAction;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.rdm.attrhandler.code.AttrHandlerFactory;
import neatlogic.framework.rdm.attrhandler.code.IAttrValueHandler;
import neatlogic.framework.rdm.auth.label.RDM_BASE;
import neatlogic.framework.rdm.dto.*;
import neatlogic.framework.rdm.enums.AttrType;
import neatlogic.framework.rdm.enums.ProjectUserType;
import neatlogic.framework.rdm.enums.core.AppTypeManager;
import neatlogic.framework.rdm.exception.CreateObjectSchemaException;
import neatlogic.framework.rdm.exception.ProjectNameIsExistsException;
import neatlogic.framework.rdm.exception.ProjectNotAuthException;
import neatlogic.framework.rdm.exception.ProjectTemplateNotFoundException;
import neatlogic.framework.restful.annotation.*;
import neatlogic.framework.restful.constvalue.OperationTypeEnum;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentBase;
import neatlogic.framework.transaction.core.EscapeTransactionJob;
import neatlogic.module.rdm.auth.ProjectAuthManager;
import neatlogic.module.rdm.dao.mapper.AppMapper;
import neatlogic.module.rdm.dao.mapper.AttrMapper;
import neatlogic.module.rdm.dao.mapper.ProjectMapper;
import neatlogic.module.rdm.dao.mapper.ProjectTemplateMapper;
import neatlogic.module.rdm.service.ProjectService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AuthAction(action = RDM_BASE.class)
@OperationType(type = OperationTypeEnum.UPDATE)
@Transactional
public class SaveProjectApi extends PrivateApiComponentBase {
    @Resource
    private AttrMapper attrMapper;
    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private ProjectTemplateMapper projectTemplateMapper;

    @Resource
    private AppMapper appMapper;

    @Resource
    private ProjectService projectService;

    @Override
    public String getName() {
        return "nmrap.saveprojectapi.getname";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({@Param(name = "id", type = ApiParamType.LONG, desc = "nmrap.saveprojectapi.input.param.desc.id"),
            @Param(name = "name", type = ApiParamType.STRING, xss = true, isRequired = true, maxLength = 50, desc = "term.rdm.projectname"),
            @Param(name = "templateId", type = ApiParamType.LONG, isRequired = true, desc = "term.rdm.projecttype"),
            @Param(name = "description", type = ApiParamType.STRING, desc = "common.description", maxLength = 5000),
            @Param(name = "dateRange", type = ApiParamType.JSONARRAY, desc = "term.rdm.startenddate"),
            @Param(name = "memberIdList", type = ApiParamType.JSONARRAY, desc = "nmrap.saveprojectapi.input.param.desc.memberidlist"),
            @Param(name = "leaderIdList", type = ApiParamType.JSONARRAY, desc = "term.rdm.project.manageridlist"),
            @Param(name = "color", type = ApiParamType.STRING, desc = "common.color")})
    @Output({@Param(name = "id", type = ApiParamType.STRING, desc = "term.cmdb.ciid")})
    @Description(desc = "nmrap.saveprojectapi.getname")
    @Override
    public Object myDoService(JSONObject paramObj) {
        Long id = paramObj.getLong("id");
        ProjectVo projectVo = JSON.toJavaObject(paramObj, ProjectVo.class);
        projectVo.setIsClose(0);
        if (projectMapper.checkProjectNameIsExists(projectVo) > 0) {
            throw new ProjectNameIsExistsException(projectVo.getName());
        }
        if (paramObj.getLong("id") == null) {
            ProjectTemplateVo projectTemplateVo = projectTemplateMapper.getProjectTemplateById(projectVo.getTemplateId());
            if (projectTemplateVo == null) {
                throw new ProjectTemplateNotFoundException(projectVo.getTemplateId());
            }
            projectVo.setType(projectTemplateVo.getName());
            projectMapper.insertProject(projectVo);
            List<AppVo> appList = new ArrayList<>();
            for (ProjectTemplateAppTypeVo appType : projectTemplateVo.getAppTypeList()) {
                AppVo appVo = new AppVo();
                appVo.setProjectId(projectVo.getId());
                appVo.setType(appType.getAppType());
                appVo.setSort(appType.getSort());
                appVo.setIsActive(1);
                appMapper.insertApp(appVo);
                AttrType[] attrTypeList = AppTypeManager.getAttrList(appType.getAppType());
                boolean hasAttr = false;
                if (appType.getConfig() != null) {
                    JSONArray attrObjList = appType.getConfig().getJSONArray("attrList");
                    if (CollectionUtils.isNotEmpty(attrObjList)) {
                        hasAttr = true;
                        for (int i = 0; i < attrObjList.size(); i++) {
                            JSONObject attrObj = attrObjList.getJSONObject(i);
                            AppAttrVo appAttrVo = JSON.toJavaObject(attrObj, AppAttrVo.class);
                            appAttrVo.setSort(i + 1);
                            if (appAttrVo.getIsRequired() == null) {
                                appAttrVo.setIsRequired(0);
                            }
                            if (appAttrVo.getIsActive() == null) {
                                appAttrVo.setIsActive(0);
                            }
                            if (appAttrVo.getIsPrivate() == null) {
                                appAttrVo.setIsPrivate(0);
                            }
                            //如果是内置属性检查是否还存在
                            if (appAttrVo.getIsPrivate().equals(0) || (appAttrVo.getIsPrivate().equals(1) && AttrType.get(appAttrVo.getType()) != null)) {
                                appAttrVo.setId(null);
                                appAttrVo.setAppId(appVo.getId());
                                appVo.addAppAttr(appAttrVo);
                                attrMapper.insertAppAttr(appAttrVo);
                            }
                        }
                    }
                    JSONArray statusObjList = appType.getConfig().getJSONArray("statusList");
                    JSONArray statusRelObjList = appType.getConfig().getJSONArray("statusRelList");
                    List<AppStatusVo> statusList = new ArrayList<>();
                    List<AppStatusRelVo> statusRelList = new ArrayList<>();

                    if (CollectionUtils.isNotEmpty(statusObjList)) {
                        for (int i = 0; i < statusObjList.size(); i++) {
                            AppStatusVo statusVo = JSON.toJavaObject(statusObjList.getJSONObject(i), AppStatusVo.class);
                            statusVo.setId(null);
                            statusVo.setAppId(appVo.getId());
                            statusList.add(statusVo);
                        }
                    }
                    if (CollectionUtils.isNotEmpty(statusRelObjList)) {
                        for (int i = 0; i < statusRelObjList.size(); i++) {
                            AppStatusRelVo statusRelVo = JSON.toJavaObject(statusRelObjList.getJSONObject(i), AppStatusRelVo.class);
                            statusRelVo.setFromStatusId(null);
                            statusRelVo.setToStatusId(null);
                            statusRelVo.setId(null);
                            Optional<AppStatusVo> opFrom = statusList.stream().filter(d -> d.getUuid().equals(statusRelVo.getFromStatusUuid())).findFirst();
                            if (opFrom.isPresent()) {
                                AppStatusVo statusVo = opFrom.get();
                                statusRelVo.setFromStatusId(statusVo.getId());
                            } else {
                                if (statusRelVo.getFromStatusUuid().equals("0")) {
                                    statusRelVo.setFromStatusId(0L);
                                } else {
                                    continue;
                                }
                            }
                            Optional<AppStatusVo> toOp = statusList.stream().filter(d -> d.getUuid().equals(statusRelVo.getToStatusUuid())).findFirst();
                            if (toOp.isPresent()) {
                                AppStatusVo statusVo = toOp.get();
                                statusRelVo.setToStatusId(statusVo.getId());
                            } else {
                                if (statusRelVo.getToStatusUuid().equals("0")) {
                                    statusRelVo.setToStatusId(0L);
                                } else {
                                    continue;
                                }
                            }
                            statusRelVo.setAppId(appVo.getId());
                            statusRelList.add(statusRelVo);
                        }
                    }
                    for (AppStatusVo status : statusList) {
                        appMapper.insertAppStatus(status);
                    }
                    for (AppStatusRelVo statusRel : statusRelList) {
                        appMapper.insertAppStatusRel(statusRel);
                    }
                }
                if (!hasAttr) {
                    if (attrTypeList != null) {
                        int sort = 1;
                        for (AttrType attrType : attrTypeList) {
                            IAttrValueHandler handler = AttrHandlerFactory.getHandler(attrType.getType());
                            if (handler != null && (handler.getBelong() == null || projectTemplateVo.getAppTypeList().stream().anyMatch(d -> d.getAppType().equalsIgnoreCase(handler.getBelong())))) {
                                AppAttrVo appAttrVo = new AppAttrVo();
                                appAttrVo.setName(attrType.getName());
                                appAttrVo.setLabel(attrType.getLabel());
                                appAttrVo.setType(attrType.getType());
                                appAttrVo.setSort(sort);
                                appAttrVo.setIsRequired(0);
                                appAttrVo.setIsPrivate(1);
                                appAttrVo.setIsActive(1);
                                appAttrVo.setAppId(appVo.getId());
                                appVo.addAppAttr(appAttrVo);
                                attrMapper.insertAppAttr(appAttrVo);
                                sort += 1;
                            }
                        }
                    }
                }

                appList.add(appVo);
            }
            for (AppVo appVo : appList) {
                if (appVo.getHasIssue()) {
                    EscapeTransactionJob.State s = projectService.buildObjectSchema(appVo);
                    if (!s.isSucceed()) {
                        throw new CreateObjectSchemaException(appVo.getName());
                    }
                }
            }

            ProjectUserVo projectUserVo = new ProjectUserVo();
            projectUserVo.setUserId(UserContext.get().getUserUuid(true));
            projectUserVo.setUserType(ProjectUserType.OWNER.getValue());
            projectUserVo.setProjectId(projectVo.getId());
            projectMapper.insertProjectUser(projectUserVo);
        } else {
            if (!ProjectAuthManager.checkProjectAuth(id, ProjectUserType.LEADER, ProjectUserType.OWNER)) {
                throw new ProjectNotAuthException(projectVo.getName());
            }
            projectMapper.updateProject(projectVo);
            //清除用户数据
            projectMapper.deleteProjectUserByProjectId(projectVo.getId(), new ArrayList<String>() {{
                this.add(ProjectUserType.OWNER.getValue());
                this.add(ProjectUserType.MEMBER.getValue());
                this.add(ProjectUserType.LEADER.getValue());
            }});
        }
        if (CollectionUtils.isNotEmpty(projectVo.getUserList())) {
            for (ProjectUserVo userVo : projectVo.getUserList()) {
                for (ProjectUserTypeVo userTypeVo : userVo.getUserTypeList()) {
                    ProjectUserVo projectUserVo = new ProjectUserVo();
                    projectUserVo.setUserId(userVo.getUserId().replace(GroupSearch.USER.getValuePlugin(), ""));
                    projectUserVo.setUserType(userTypeVo.getUserType());
                    projectUserVo.setProjectId(projectVo.getId());
                    projectMapper.insertProjectUser(projectUserVo);
                }
            }
        }

        return projectVo.getId();
    }

    @Override
    public String getToken() {
        return "/rdm/project/save";
    }
}
