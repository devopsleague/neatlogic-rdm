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

package neatlogic.module.rdm.dao.mapper;

import neatlogic.framework.rdm.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppMapper {
    AppStatusRelVo getAppStatusRel(AppStatusRelVo appStatusRelVo);


    List<AppStatusRelVo> getStatusRelByAppId(Long appId);


    AppVo getAppById(Long id);

    AppUserSettingVo getAppUserSetting(@Param("userId") String userId, @Param("appId") Long appId);

    List<AppVo> getAppDetailByProjectId(Long projectId);

    AppStatusVo getStatusById(Long id);

    List<AppStatusVo> getStatusByAppId(@Param("appId") Long appId, @Param("status") Long status);


    void updateAppStatus(AppStatusVo appStatusVo);


    void updateAppStatusRelConfig(AppStatusRelVo appStatusRelVo);

    void resetAppStatusIsStart(Long appId);

    void updateAppStatusType(AppStatusVo appStatusVo);


    void insertAppStatusRel(AppStatusRelVo appStatusRelVo);

    void insertApp(AppVo appVo);

    void insertAppStatus(AppStatusVo appStatusVo);

    void insertAttrUserSetting(AppUserSettingVo appUserSettingVo);


    void deleteAppById(Long id);

    void deleteAppStatusRel(AppStatusRelVo appStatusRelVo);


    void deleteAppStatusById(Long id);

}
