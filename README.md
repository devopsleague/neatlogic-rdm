R & D Management   研发管理

## Tips
1. 需求，任务，缺陷，测试计划....统称为过程域
2. 过程域是可以添加的，建项目之后需选择或添加过程域,或者使用模板创建,模板相当于一套规则（定义了使用哪些属性，使用哪些过程域）  
2. 用户创建的需求，任务，缺陷... 统一放入task表，用过程域区分
3. 所有的优先级统一定义，用过程域区分，每个项目定义自己的优先级
4. 所有的类别(需求分类，任务分类，缺陷分类)统一定义，用过程域区分，每个项目定义自己的分类
1. 数据库表结构定义
5. 自定义属性分为（系统字段和项目自定义字段，类别使用枚举类定义的，数据库不再用枚举字段--加类别要修改数据库）
6. 工作流定义（状态流转-需求，任务，缺陷）
7. task关联(需求关联任务，需求关联缺陷.... ??任务是否需要关联缺陷??)
8. 报表需求(结合现有需求看下需要出哪些报表)
9. changelog(研发管理模块各项内容的变更记录)
10. 通知     


## Develop
1. 表结构设计保留自增主键，关联查询使用 uuid 关联, 方便导入导出     
2. 研发管理模块表以 rdm 开头，字段要有备注信息，新建索引以idx_开头     
3. 异常定义清晰，如 过程域名称已存在：需求（说明是什么异常及关键信息）    
4. 命名规范，尽量在关键字段，关键方法上添加注释

## 接口规划
### 项目相关    
项目模板查询    
项目模板创建(关联过程域及过程域属性)   
项目模板修改    
根据已有项目模板创建项目(或新创建模板后直接创建项目) --复制模板的属性，过程域，优先级，类别， 工作流
模板删除(管理员)    

创建项目（包含父子关系）  
项目信息修改  
项目删除（怎么删除，或者不删除）
项目复制

项目迭代增删改(接口)  
迭代与任务关联接口

项目成员管理   
项目成员权限管理   
权限校验接口  

过程域关联属性(包含排序)   
过程域新增   
过程域查询   
过程域修改   

项目过程域属性（增减，排序）  
添加自定义属性

项目优先级创建   
项目优先级查询   
项目优先级修改   
项目优先级删除   

项目工作流创建   
项目工作流修改   
项目状态新增(区分开始态，运行态，结束态，及可流转的状态)   
项目状态删除   
项目状态修改    

### 任务相关

创建任务(包含父子关系)  
任务关联 （需求关联任务，需求关联缺陷）
任务修改    
任务查询  

任务查询过滤器创建（查询的模板，跟原来的工单中心相似）    
修改 （可只修改某一项的值）
删除  

任务附件（或做成统一的附件管理）  
任务评论（包含@某人之后的提醒） 
任务修改记录changelog







