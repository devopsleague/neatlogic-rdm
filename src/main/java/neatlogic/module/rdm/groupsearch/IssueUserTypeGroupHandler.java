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

package neatlogic.module.rdm.groupsearch;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.rdm.enums.IssueGroupSearch;
import neatlogic.framework.rdm.enums.IssueUserType;
import neatlogic.framework.restful.groupsearch.core.IGroupSearchHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class IssueUserTypeGroupHandler implements IGroupSearchHandler {
    @Override
    public String getName() {
        return IssueGroupSearch.ISSUEUSERTYPE.getValue();
    }

    @Override
    public String getHeader() {
        return getName() + "#";
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> search(JSONObject jsonObj) {
        List<Object> includeList = jsonObj.getJSONArray("includeList");
        List<Object> excludeList = jsonObj.getJSONArray("excludeList");
        if (CollectionUtils.isEmpty(includeList)) {
            includeList = new ArrayList<Object>();
        }
        List<String> includeStrList = includeList.stream().map(Object::toString).collect(Collectors.toList());
        List<String> valuelist = new ArrayList<>();
        List<ValueTextVo> userTypeList = new ArrayList<>();
        for (IssueUserType s : IssueUserType.values()) {
            if (s.getIsShow() && s.getText().contains(jsonObj.getString("keyword"))) {
                String value = getHeader() + s.getValue();
                if (!valuelist.contains(value)) {
                    valuelist.add(value);
                    userTypeList.add(new ValueTextVo(value, s.getText()));
                }
            }
            if (includeStrList.contains(getHeader() + s.getValue())) {
                if (userTypeList.stream().noneMatch(o -> Objects.equals(o.getValue(), s.getValue()))) {
                    String value = getHeader() + s.getValue();
                    if (!valuelist.contains(value)) {
                        valuelist.add(value);
                        userTypeList.add(new ValueTextVo(value, s.getText()));
                    }
                }
            }
        }
        return (List<T>) userTypeList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> reload(JSONObject jsonObj) {
        List<ValueTextVo> userTypeList = new ArrayList<>();
        List<String> valueList = jsonObj.getJSONArray("valueList").toJavaList(String.class);
        if (CollectionUtils.isNotEmpty(valueList)) {
            for (String value : valueList) {
                if (value.startsWith(getHeader())) {
                    String realValue = value.replace(getHeader(), "");
                    String text = IssueUserType.getText(realValue);
                    if (StringUtils.isNotBlank(text)) {
                        userTypeList.add(new ValueTextVo(value, text));
                    }
                }
            }
        }
        return (List<T>) userTypeList;
    }

    @Override
    public <T> JSONObject repack(List<T> userTypeList) {
        JSONObject userTypeObj = new JSONObject();
        userTypeObj.put("value", "issueUserType");
        userTypeObj.put("text", "任务干系人");
        userTypeObj.put("sort", getSort());
        userTypeObj.put("dataList", userTypeList);
        return userTypeObj;
    }

    @Override
    public int getSort() {
        return 1;
    }

    @Override
    public Boolean isLimit() {
        return false;
    }
}
