## Authorization 授权

### [Get List ](https://docs.camunda.org/manual/7.15/reference/rest/authorization/get-query/#query-parameters) 获取授权信息列表



> GET `/authorization`

参数

| Name         | Description                                                  |
| :----------- | :----------------------------------------------------------- |
| id           | 通过授权id查询                                               |
| type         | 授权类型. (0=global, 1=grant, 2=revoke).[User Guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/authorization-service/#authorization-type) |
| userIdIn     | 逗号分隔userIds.                                             |
| groupIdIn    | 逗号分隔groupIds.                                            |
| resourceType | 整型的资源类型.[User Guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/authorization-service/#resources). |
| resourceId   | 资源id.                                                      |
| sortBy       | 排序 `resourceType` and `resourceId`.                        |
| sortOrder    | 排序`asc` `desc`                                             |
| firstResult  | 分页-索引.                                                   |
| maxResults   | 分页-返回结果数量.                                           |

返回

| Name         | Value   | Description                                                  |
| :----------- | :------ | :----------------------------------------------------------- |
| id           | String  | 授权id.                                                      |
| type         | Integer | 授权类型. (0=global, 1=grant, 2=revoke). [User Guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/authorization-service/#authorization-type) |
| permissions  | String  | 用户权限 [“ALL”,"CREATE", "READ"],                           |
| userId       | String  | 授权对象用户id. 可以是*                                      |
| groupId      | String  | 授权对象分组id                                               |
| resourceType | Integer | 资源类型.[User Guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/authorization-service/#resources). |
| resourceId   | String  | 资源id。可以是*                                              |
| removalTime  | String  | 历史实例授权移除的时间,默认可以是null，格式是 `yyyy-MM-dd'T'HH:mm:ss.SSSZ`. |



### [Get List Count](https://docs.camunda.org/manual/7.15/reference/rest/authorization/get-query-count/) 获取授权信息个数

> GET `/authorization/count`



### [Get](https://docs.camunda.org/manual/7.15/reference/rest/authorization/get/) 获取授权（通过id）

> GET `/authorization/{id}`



### [Check](https://docs.camunda.org/manual/7.15/reference/rest/authorization/get-check/) 授权检查

> GET `/authorization/check`

Query Parameters

| Name           | Description                                                  | Required? |
| :------------- | :----------------------------------------------------------- | :-------- |
| permissionName | 许可名称                                                     | Yes       |
| resourceName   | 资源名称.                                                    | Yes       |
| resourceType   | 整型的资源类型.  [User Guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/authorization-service/#resources). | Yes       |
| resourceId     | 资源id.                                                      | No        |
| userId         | 待检查许可的用户id（当前用户必须对Authorization resource.有读权限）。如果是空, 检查当前授权用户. | No        |

结果

JSON对象

| Name           | Value   | Description          |
| :------------- | :------ | :------------------- |
| permissionName | String  | 权限名称.            |
| resourceName   | String  | 资源名称             |
| resourceId     | String  | 资源id.              |
| isAuthorized   | Boolean | 是否授权True / false |

### Options 选项

支持两种自定义的OPTIONS请求，一个用于资源本身，另一个用于单个授权实例。OPTIONS请求允许您检查当前经过身份验证的用户可以对/authorization资源执行的可用操作集。用户是否可以执行操作可能取决于各种因素，包括用户与该资源交互的授权和流程引擎的内部配置。

> /authorization 资源上的可用交互的选项
>
> /authorization/{id} 资源实例上可用交互的选项



结果

一个JSON对象包含单一元素 `links`, 提供资源link列表.每个link 的属性:

| Name   | Value  | Description                                            |
| :----- | :----- | :----------------------------------------------------- |
| method | String | 用于交互的HTTP方法                                     |
| href   | String | 交互地址 URL.                                          |
| rel    | String | 交互的关系（即交互的性质）例如: `create`, `delete` ... |

### Create 创建授权

> POST /authorization/create

**Request Body**

JSON对象:

| Name         | Value   | Description                                                  |
| :----------- | :------ | :----------------------------------------------------------- |
| type         | Integer | 授权类型. (0=global, 1=grant, 2=revoke).  [User Guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/authorization-service/#authorization-type) |
| permissions  | String  | 字符串数组。该授权包含的权限。                               |
| userId       | String  | 授权对象用户id. 可以是*                                      |
| groupId      | String  | 授权对象分组id                                               |
| resourceType | Integer | 资源类型.[User Guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/authorization-service/#resources). |
| resourceId   | String  | 资源id。可以是*                                              |

JSON数组

| Name                  | Value   | Description                                                  |
| :-------------------- | :------ | :----------------------------------------------------------- |
| id                    | String  | 授权id.                                                      |
| type                  | Integer | 授权类型. (0=global, 1=grant, 2=revoke). [User Guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/authorization-service/#authorization-type) |
| permissions           | String  | 用户权限 [“ALL”,"CREATE", "READ"],                           |
| userId                | String  | 授权对象用户id. 可以是*                                      |
| groupId               | String  | 授权对象分组id                                               |
| resourceType          | Integer | 资源类型.[User Guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/authorization-service/#resources). |
| resourceId            | String  | 资源id。可以是*                                              |
| removalTime           | String  | 历史实例授权移除的时间,默认可以是null，格式是 `yyyy-MM-dd'T'HH:mm:ss.SSSZ`. |
| links                 | Object  | 包含与资源交互的链接（Link）的JSON数组。链接仅包含当前已验证用户将被授权执行的操作。 |
| rootProcessInstanceId | String  | 与历史实例授权（historic instance authorization）相关的根流程（ root process）实例的流程实例id。如果与历史实例资源不相关，则可以为“null”。 |



### [Update](https://docs.camunda.org/manual/7.15/reference/rest/authorization/put-update/) 更新授权

>PUT `/authorization/{id}`

Request Body

| permissions  | String  | 用户权限 [“ALL”,"CREATE", "READ"],                           |
| :----------- | :------ | :----------------------------------------------------------- |
| userId       | String  | 授权对象用户id. 可以是*                                      |
| groupId      | String  | 授权对象分组id                                               |
| resourceType | Integer | 资源类型.[User Guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/authorization-service/#resources). |
| resourceId   | String  | 资源id。可以是*                                              |

### [Delete](https://docs.camunda.org/manual/7.15/reference/rest/authorization/delete/) 删除授权

> DELETE `/authorization/{id}`



## User 用户管理

###  [Delete](https://docs.camunda.org/manual/7.15/reference/rest/user/delete/) 删除用户

> DELETE `/user/{id}`



### [Get Users](https://docs.camunda.org/manual/7.15/reference/rest/user/get-query/) 获取用户列表

>GET `/user`

Query Parameters

| Name             | Description                                                  |
| :--------------- | :----------------------------------------------------------- |
| id               | 根据用户id过滤.                                              |
| idIn             | 根据用户id列表过滤                                           |
| firstName        | 根据fisrtName过滤                                            |
| firstNameLike    | 根据fisrtName子字符串的过滤.                                 |
| lastName         | 根据lastName过滤r.                                           |
| lastNameLike     | 根据lastName子字符串过滤.                                    |
| email            | 根据email过滤.                                               |
| emailLike        | 根据email子字符串过滤.                                       |
| memberOfGroup    | 根据用户分组过滤.                                            |
| memberOfTenant   | 根据用户租户过滤                                             |
| potentialStarter | 仅选择可能启动给定流程定义的用户.                            |
| sortBy           | 按指定项字典顺序排序，包括 `userId`, `firstName`, `lastName` and `email`. . |
| sortOrder        | `asc` `desc`                                                 |
| firstResult      | 分页-索引.                                                   |
| maxResults       | 分页-返回结果数量.                                           |

Result

JSON数组

| Name      | Value  | Description     |
| :-------- | :----- | :-------------- |
| id        | String | 用户id.         |
| firstName | String | 用户first name. |
| lastName  | String | 用户last name.  |
| email     | String | 用户email.      |



### [Get Users Count](https://docs.camunda.org/manual/7.15/reference/rest/user/get-query-count/) 获取用户列表个数

>GET `/user/count`

参数同上 (id~potentialStarter)



### [Get profile](https://docs.camunda.org/manual/7.15/reference/rest/user/get/) 用户配置

> GET `/user/{id}/profile`

Result

| Name      | Value  | Description     |
| :-------- | :----- | :-------------- |
| id        | String | 用户id.         |
| firstName | String | 用户first name. |
| lastName  | String | 用户last name.  |
| email     | String | 用户email.      |



### [Options](https://docs.camunda.org/manual/7.15/reference/rest/user/options/) 用户选项

支持两种自定义的OPTIONS请求，一个用于资源本身，另一个用于单个授权实例。OPTIONS请求允许您检查当前经过身份验证的用户可以对/user资源执行的可用操作集。用户是否可以执行操作可能取决于各种因素，包括用户与该资源交互的授权和流程引擎的内部配置。

>  `/user` 资源上可用交互选项
>
>  `/user/{id}` 资源实例上可用交互选项

结果

一个JSON对象包含单一元素 `links`, 提供资源link列表.每个link 的属性:

| Name   | Value  | Description                                            |
| :----- | :----- | :----------------------------------------------------- |
| method | String | 用于交互的HTTP方法                                     |
| href   | String | 交互地址 URL.                                          |
| rel    | String | 交互的关系（即交互的性质）例如: `create`, `delete` ... |

### [Create](https://docs.camunda.org/manual/7.15/reference/rest/user/post-create/) 创建用户

> POST `/user/create`

Request Body

| Name        | Type  | Description                                                  |
| :---------- | :---- | :----------------------------------------------------------- |
| profile     | Array | 一个JSON对象包含: `id (String)`, `firstName (String)`, `lastName (String)` and `email (String)`. |
| credentials | Array | 一个JSON对象包含: : `password (String)`.                     |



### [Update Credentials](https://docs.camunda.org/manual/7.15/reference/rest/user/put-update-credentials/) 修改密码

> PUT `/user/{id}/credentials`

Request Body：JSON对象

| Name                      | Type   | Description               |
| :------------------------ | :----- | :------------------------ |
| password                  | String | 新密码.                   |
| authenticatedUserPassword | String | 授权用户的密码（原密码）. |

### [Update Profile](https://docs.camunda.org/manual/7.15/reference/rest/user/put-update-profile/) 更新配置

> PUT `/user/{id}/profile`

Request Body

JSON对象

| Name      | Value  | Description     |
| :-------- | :----- | :-------------- |
| id        | String | 用户id.         |
| firstName | String | 用户first name. |
| lastName  | String | 用户last name.  |
| email     | String | 用户email.      |



### [Unlock User](https://docs.camunda.org/manual/7.15/reference/rest/user/unlock/) 解锁用户

> POST `/user/{id}/unlock`



## Deployments 部署

### [Get Deployments](https://docs.camunda.org/manual/7.15/reference/rest/deployment/get-query/) 获取部署

> GET `/deployment`

Query Params

| Name                              | Description                                                  |
| :-------------------------------- | :----------------------------------------------------------- |
| id                                | 通过部署id过滤.                                              |
| name                              | 通过部署名称过滤                                             |
| nameLike                          | 部署名称%匹配                                                |
| source                            | 通过部署资源匹配                                             |
| withoutSource                     | 资源（source）为空的部署                                     |
| tenantIdIn                        | 根据逗号分隔的租户id过滤                                     |
| withoutTenantId                   | 不属于任何租户的部署                                         |
| includeDeploymentsWithoutTenantId | 同上，可以和tenantIdIn同用                                   |
| after                             | 给定日期之后.  `yyyy-MM-dd'T'HH:mm:ss.SSSZ`, e.g., `2013-01-23T14:42:45.000+0200`. |
| before                            | 给定日期之前 `yyyy-MM-dd'T'HH:mm:ss.SSSZ`, e.g., `2013-01-23T14:42:45.000+0200`. |
| sortBy                            | `id`, `name`, `deploymentTime` and `tenantId`.               |
| sortOrder                         | `asc` `desc`                                                 |
| firstResult                       | 分页                                                         |
| maxResults                        | 分页                                                         |

Result

| Name           | Value  | Description  |
| :------------- | :----- | :----------- |
| id             | String | 部署ID       |
| name           | String | 部署名称     |
| source         | String | 部署资源     |
| tenantId       | String | 部署的租户id |
| deploymentTime | Date   | 部署时间     |

e.g.

```json
[
    {
        "links": [],
        "id": "20f07887-d97a-11eb-b1d6-8c89a5ee82af",
        "name": "exampleApplication",
        "source": "process application",
        "deploymentTime": "2021-06-30T16:06:52.662+0800",
        "tenantId": null
    }
]
```



### [Get Deployments Count](https://docs.camunda.org/manual/7.15/reference/rest/deployment/get-query-count/) 获取部署个数

> GET `/deployment/count`



### [Get](https://docs.camunda.org/manual/7.15/reference/rest/deployment/get/) 获取具体部署信息

根据部署id获取具体部署

> GET `/deployment/{id}`

Result

| Name           | Value  | Description  |
| :------------- | :----- | :----------- |
| id             | String | 部署ID       |
| name           | String | 部署名称     |
| source         | String | 部署资源     |
| tenantId       | String | 部署的租户id |
| deploymentTime | Date   | 部署时间     |

e.g.

```json
{
    "links": [],
    "id": "20f07887-d97a-11eb-b1d6-8c89a5ee82af",
    "name": "exampleApplication",
    "source": "process application",
    "deploymentTime": "2021-06-30T16:06:52.662+0800",
    "tenantId": null
}
```



### [Create](https://docs.camunda.org/manual/7.15/reference/rest/deployment/post-deployment/) 创建一个部署

> POST `/deployment/create`

表单参数

| Form Part Name             | Content Type             | Description                                                  |
| :------------------------- | :----------------------- | :----------------------------------------------------------- |
| deployment-name            | text/plain               | 部署名称                                                     |
| enable-duplicate-filtering | text/plain               | 重名检查。指示流程引擎是否应对部署执行重复检查的标志。这允许您检查是否已经存在具有相同名称和相同资源的部署，如果为true，则不创建新部署，而是返回现有部署。默认值为“false”` |
| deploy-changed-only        | text/plain               | 是否对每个资源进行重复检查，设置为true时，只有改变的资源才会被部署。对包含相同名称的以前部署的资源进行检查，并且只对这些资源的最新版本进行检查。如果设置为“true”，则“enable-duplicate-filtering”选项将被覆盖并设置为“true”。 |
| deployment-source          | text/plain               | 待部署的资源.                                                |
| tenant-id                  | text/plain               | 租户id.                                                      |
| *                          | application/octet-stream | 创建部署资源的二进制数据                                     |

Result:

| Name                                    | Value  | Description                                                  |
| :-------------------------------------- | :----- | :----------------------------------------------------------- |
| links                                   | List   | 新创建部署的链接（Link）包含 `method`, `href` 和`rel`.       |
| id                                      | String | 部署id.                                                      |
| name                                    | String | 部署名称                                                     |
| source                                  | String | 部署源                                                       |
| tenantId                                | String | 部署租户id                                                   |
| deploymentTime                          | String | 创建时间                                                     |
| deployedProcessDefinitions              | Object | 成功部署的每个流程的属性. key是流程定义id, value 流程定义对应的JSON对象, 见 [Process Definition resource](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/get/). |
| deployedCaseDefinitions                 | Object | 成功部署的每个案例的属性。key是案例定义id，value是case定义对应的JSON对象，见[Case Definition resource](https://docs.camunda.org/manual/7.15/reference/rest/case-definition/get/). |
| deployedDecisionDefinitions             | Object | 成功部署的每个决策（decision）的属性。key是决策定义id，value是决策定义对应的JSON对象，见 [Decision Definition resource](https://docs.camunda.org/manual/7.15/reference/rest/decision-definition/get/). |
| deployedDecisionRequirementsDefinitions | Object | 成功部署的每个决策需求（decision requirements）的属性。key是决策需求定义id，value是决策需求定义对应的JSON对象，见 [Decision Requirements Definition resource](https://docs.camunda.org/manual/7.15/reference/rest/decision-requirements-definition/get/). |

### [Redeploy](https://docs.camunda.org/manual/7.15/reference/rest/deployment/post-redeploy-deployment/) 重新部署

> POST `/deployment/{id}/redeploy`

Request Body （不包含任何资源时，重新部署所有现有资源）

| Name          | Description                |
| :------------ | :------------------------- |
| resourceIds   | 资源id列表.                |
| resourceNames | 资源名称列表               |
| source        | 设置部署的源.例如：cockpit |

Result

| Name                                    | Value  | Description                                                  |
| :-------------------------------------- | :----- | :----------------------------------------------------------- |
| links                                   | List   | 新创建部署的链接（Link）包含 `method`, `href` 和`rel`.       |
| id                                      | String | 部署id.                                                      |
| name                                    | String | 部署名称                                                     |
| source                                  | String | 部署源                                                       |
| tenantId                                | String | 部署租户id                                                   |
| deploymentTime                          | String | 创建时间                                                     |
| deployedProcessDefinitions              | Object | 成功部署的每个流程的属性. key是流程定义id, value 流程定义对应的JSON对象, 见 [Process Definition resource](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/get/). |
| deployedCaseDefinitions                 | Object | 成功部署的每个案例的属性。key是案例定义id，value是case定义对应的JSON对象，见[Case Definition resource](https://docs.camunda.org/manual/7.15/reference/rest/case-definition/get/). |
| deployedDecisionDefinitions             | Object | 成功部署的每个决策（decision）的属性。key是决策定义id，value是决策定义对应的JSON对象，见 [Decision Definition resource](https://docs.camunda.org/manual/7.15/reference/rest/decision-definition/get/). |
| deployedDecisionRequirementsDefinitions | Object | 成功部署的每个决策需求（decision requirements）的属性。key是决策需求定义id，value是决策需求定义对应的JSON对象，见 [Decision Requirements Definition resource](https://docs.camunda.org/manual/7.15/reference/rest/decision-requirements-definition/get/). |

### [Get Resources ](https://docs.camunda.org/manual/7.15/reference/rest/deployment/get-resources/) 返回所有部署资源

> GET `/deployment/{id}/resources`

Result：JSON列表

| Name         | Value  | Description |
| :----------- | :----- | :---------- |
| id           | String | 资源id.     |
| name         | String | 资源名称    |
| deploymentId | String | 部署id      |

### [Get Resource](https://docs.camunda.org/manual/7.15/reference/rest/deployment/get-resource/) 根据资源id获取

> GET `/deployment/{id}/resources/{resourceId}` 

Result:

| Name         | Value  | Description |
| :----------- | :----- | :---------- |
| id           | String | 资源id.     |
| name         | String | 资源名称    |
| deploymentId | String | 部署id      |

### [Get Resource (Binary)](https://docs.camunda.org/manual/7.15/reference/rest/deployment/get-resource-binary/) 获取资源的二进制内容

> GET `/deployment/{id}/resources/{resourceId}/data`

Result: 二进制

### [Delete](https://docs.camunda.org/manual/7.15/reference/rest/deployment/delete-deployment/) 删除部署

> DELETE `/deployment/{id}`

Query Parameters：

| Name                | Description                                                 |
| :------------------ | :---------------------------------------------------------- |
| cascade             | `true`, 删除部署的所有流程实例、历史流程实例和任务(Jobs).   |
| skipCustomListeners | `true`,如果只有内置的ExecutionListeners应该被通知结束事件。 |
| skipIoMappings      | `true`,所有input/output mappings 不被执行.                  |

## Process Definition 流程定义

### [Get List](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/get-query/) 获取流程定义列表

> GET `/process-definition`

Query Parameters

| Name                                     | Description                                                  |
| :--------------------------------------- | :----------------------------------------------------------- |
| processDefinitionId                      | 流程定义id                                                   |
| processDefinitionIdIn                    | 流程定义id列表                                               |
| name                                     | 流程定义名称                                                 |
| nameLike                                 | 流程定义名称匹配（子字符串）                                 |
| deploymentId                             | 归属的部署id                                                 |
| deployedAfter                            | 在这个时间之后被部署的流程定义.                              |
| deployedAt                               | 指定时间被部署的流程定义                                     |
| key                                      | 流程定义key                                                  |
| keysIn                                   | 流程定义key列表                                              |
| keyLike                                  | 流程定义key匹配                                              |
| category                                 | 流程定义分类                                                 |
| categoryLike                             | 流程定义分类子字符串匹配                                     |
| version                                  | 流程定义版本                                                 |
| latestVersion                            | `true` 仅返回最新版本的流程定义                              |
| resourceName                             | 流程定义的资源名称                                           |
| resourceNameLike                         | 流程定义的资源名称子字符串匹配                               |
| startableBy                              | 可以执行该流程的用户名称                                     |
| active                                   | true 激活的流程定义                                          |
| suspended                                | true 挂起的流程定义                                          |
| incidentId                               | incident id.                                                 |
| incidentType                             | incident type. [User Guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/incidents/#incident-types) |
| incidentMessage                          | incident message.                                            |
| incidentMessageLike                      | incident message子字符串匹配.                                |
| tenantIdIn                               | 逗号分隔的租户id.                                            |
| withoutTenantId                          | true 不属于任何租户.                                         |
| includeProcessDefinitionsWithoutTenantId | true 不属于任何租户，可以和tenantIdIn一起使用.               |
| versionTag                               | 版本标识                                                     |
| versionTagLike                           | 版本标识匹配                                                 |
| withoutVersionTag                        | 不包含版本标志                                               |
| startableInTasklist                      | Tasklist中可被启动的.                                        |
| notStartableInTasklist                   | Tasklist中不可被启动的.                                      |
| startablePermissionCheck                 | Tasklist中可被用户启动的.                                    |
| sortBy                                   | `category`, `key`, `id`, `name`, `version`, `deploymentId`, `deployTime`, `tenantId` and `versionTag`. |
| sortOrder                                | `asc`  `desc`.                                               |
| firstResult                              | 分页                                                         |
| maxResults                               | 分页                                                         |

Result:

JSON列表

| Name                | Value   | Description                                                  |
| :------------------ | :------ | :----------------------------------------------------------- |
| id                  | String  | 流程定义id.                                                  |
| key                 | String  | 流程定义key, 例如 BPMN 2.0 XML 流程定义的ID.                 |
| category            | String  | 流程定义分类 例如，http://bpmn.io/schema/bpmn                |
| description         | String  | 描述                                                         |
| name                | String  | 名称                                                         |
| version             | Number  | 版本                                                         |
| resource            | String  | 该流程定义的文件名称                                         |
| deploymentId        | String  | 流程定义对应的部署id                                         |
| diagram             | String  | 流程定义的简图                                               |
| suspended           | Boolean | 是否被挂起                                                   |
| tenantId            | String  | 租户                                                         |
| versionTag          | String  | 版本tag                                                      |
| historyTimeToLive   | Number  | 历史数据中保留的时间 [History cleanup](https://docs.camunda.org/manual/7.15/user-guide/process-engine/history/#history-cleanup). |
| startableInTasklist | Boolean | 是否可在Tasklist中被执行.                                    |

e.g.

```json
[
    {
        "id": "Process_1laooas:1:254287c8-d975-11eb-bd8e-8c89a5ee82af",
        "key": "Process_1laooas",
        "category": "http://bpmn.io/schema/bpmn",
        "description": null,
        "name": null,
        "version": 1,
        "resource": "请假.bpmn",
        "deploymentId": "2529a896-d975-11eb-bd8e-8c89a5ee82af",
        "diagram": null,
        "suspended": false,
        "tenantId": null,
        "versionTag": null,
        "historyTimeToLive": null,
        "startableInTasklist": true
    },
    {
        "id": "Process_1laooas:2:210ade59-d97a-11eb-b1d6-8c89a5ee82af",
        "key": "Process_1laooas",
        "category": "http://bpmn.io/schema/bpmn",
        "description": null,
        "name": null,
        "version": 2,
        "resource": "请假.bpmn",
        "deploymentId": "20f07887-d97a-11eb-b1d6-8c89a5ee82af",
        "diagram": null,
        "suspended": false,
        "tenantId": null,
        "versionTag": null,
        "historyTimeToLive": null,
        "startableInTasklist": true
    },
    {
        "id": "SealUsingApplicationTest:1:2a68c0c4-c8eb-11eb-bcce-aa3926b2452d",
        "key": "SealUsingApplicationTest",
        "category": "http://bpmn.io/schema/bpmn/fs-seal",
        "description": null,
        "name": "用印申请",
        "version": 1,
        "resource": "SealUsingApplication-Activiti.bpmn",
        "deploymentId": "2a65da92-c8eb-11eb-bcce-aa3926b2452d",
        "diagram": null,
        "suspended": false,
        "tenantId": null,
        "versionTag": null,
        "historyTimeToLive": null,
        "startableInTasklist": true
    }
]
```



### [Get Activity Instance Statistics](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/get-activity-statistics/) 获取Activity实例统计信息

GET `/process-definition/{id}/statistics`
GET `/process-definition/key/{key}/statistics`
GET `/process-definition/key/{key}/tenant-id/{tenant-id}/statistics`

### [Get Diagram](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/get-diagram/) 获取简图

GET `/process-definition/{id}/diagram`
GET `/process-definition/key/{key}/diagram` 根据流程定义的key返回最新的简图（不属于任何租户）
GET `/process-definition/key/{key}/tenant-id/{tenant-id}/diagram`

获取文件的后缀包括`svg`, `png`, `jpg，` `gif`

### [Get Start Form Variables](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/get-form-variables/) 获取开始表单变量

检索流程定义的开始表单变量（仅当它们是通过生成的任务表单方法定义的）。开始窗体变量考虑到在开始事件中指定的表单数据。如果定义了表单字段，则会考虑表单字段的变量类型和默认值。

GET `/process-definition/{id}/form-variables`
GET `/process-definition/key/{key}/form-variables`
GET `/process-definition/key/{key}/tenant-id/{tenant-id}/form-variables`

### [Get List Count](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/get-query-count/) 获取流程定义个数

GET `/process-definition/count

| Name                                     | Description                                                  |
| :--------------------------------------- | :----------------------------------------------------------- |
| processDefinitionId                      | 流程定义id                                                   |
| processDefinitionIdIn                    | 流程定义id列表                                               |
| name                                     | 流程定义名称                                                 |
| nameLike                                 | 流程定义名称匹配（子字符串）                                 |
| deploymentId                             | 归属的部署id                                                 |
| deployedAfter                            | 在这个时间之后被部署的流程定义.                              |
| deployedAt                               | 指定时间被部署的流程定义                                     |
| key                                      | 流程定义key                                                  |
| keysIn                                   | 流程定义key列表                                              |
| keyLike                                  | 流程定义key匹配                                              |
| category                                 | 流程定义分类                                                 |
| categoryLike                             | 流程定义分类子字符串匹配                                     |
| version                                  | 流程定义版本                                                 |
| latestVersion                            | `true` 仅返回最新版本的流程定义                              |
| resourceName                             | 流程定义的资源名称                                           |
| resourceNameLike                         | 流程定义的资源名称子字符串匹配                               |
| startableBy                              | 可以执行该流程的用户名称                                     |
| active                                   | true 激活的流程定义                                          |
| suspended                                | true 挂起的流程定义                                          |
| incidentId                               | incident id.                                                 |
| incidentType                             | incident type. [User Guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/incidents/#incident-types) |
| incidentMessage                          | incident message.                                            |
| incidentMessageLike                      | incident message子字符串匹配.                                |
| tenantIdIn                               | 逗号分隔的租户id.                                            |
| withoutTenantId                          | true 不属于任何租户.                                         |
| includeProcessDefinitionsWithoutTenantId | true 不属于任何租户，可以和tenantIdIn一起使用.               |
| versionTag                               | 版本标识                                                     |
| versionTagLike                           | 版本标识匹配                                                 |
| withoutVersionTag                        | 不包含版本标志                                               |
| startableInTasklist                      | Tasklist中可被启动的.                                        |
| notStartableInTasklist                   | Tasklist中不可被启动的.                                      |
| startablePermissionCheck                 | Tasklist中可被用户启动的.                                    |

### [Get Rendered Start Form](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/get-rendered-form/) 获取HTML窗体

GET `/process-definition/{id}/rendered-form`
GET `/process-definition/key/{key}/rendered-form`
GET `/process-definition/key/{key}/tenant-id/{tenant-id}/rendered-form`

i.e.

```html
<form class="form-horizontal">
  <div class="control-group">
    <label class="control-label">Customer ID</label>
    <div class="controls">
      <input form-field type="string" name="customerId"></input>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">Amount</label>
    <div class="controls">
      <input form-field type="number" name="amount"></input>
    </div>
  </div>
</form>
```



### [Get Start Form Key](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/get-start-form-key/) 获取开始表单的键

GET `/process-definition/{id}/startForm`
GET `/process-definition/key/{key}/startForm`
GET `/process-definition/key/{key}/tenant-id/{tenant-id}/startForm`

### [Get Process Instance Statistics](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/get-statistics/) 运行时信息统计

GET `/process-definition/statistics`

### [Get XML](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/get-xml/) 获取流程定义的 BPMN 2.0 XML

GET `/process-definition/{id}/xml`
GET `/process-definition/key/{key}/xml`
GET `/process-definition/key/{key}/tenant-id/{tenant-id}/xml`

Result:

| Name      | Value  | Description                        |
| :-------- | :----- | :--------------------------------- |
| id        | String | 流程定义ID.                        |
| bpmn20Xml | String | 流程定义部署时的XML（HTML转义后）. |

### [Get](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/get/) 获取流程定义

GET `/process-definition/{id}`
GET `/process-definition/key/{key}`
GET `/process-definition/key/{key}/tenant-id/{tenant-id}`

Result：

| Name                | Value   | Description                                                  |
| :------------------ | :------ | :----------------------------------------------------------- |
| id                  | String  | ID                                                           |
| key                 | String  | KEY                                                          |
| category            | String  | 分类                                                         |
| description         | String  | 描述                                                         |
| name                | String  | 名称                                                         |
| version             | Number  | 版本号                                                       |
| resource            | String  | 对应的文件名                                                 |
| deploymentId        | String  | 部署ID                                                       |
| diagram             | String  | 简图文件名                                                   |
| suspended           | Boolean | 是否挂起                                                     |
| tenantId            | String  | 租户                                                         |
| versionTag          | String  | 版本TAG                                                      |
| historyTimeToLive   | Number  | 历史保留时间.  [History cleanup](https://docs.camunda.org/manual/7.15/user-guide/process-engine/history/#history-cleanup)中使用. |
| startableInTasklist | Boolean | TaskList中是否可开始                                         |

### [Start Instance](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/post-start-process-instance/) 开始一个流程实例	

POST `/process-definition/{id}/start`
POST `/process-definition/key/{key}/start`
POST `/process-definition/key/{key}/tenant-id/{tenant-id}/start`

Request Body:

| Name                  | Description                                                  |
| :-------------------- | :----------------------------------------------------------- |
| variables             | 初始化流程的参数。JSON对象                                   |
| businessKey           | BusinessKey.                                                 |
| caseInstanceId        | 案例ID.                                                      |
| startInstructions     | 流程启动的activities 实例参数                                |
| skipCustomListeners   | 如果只有内置的ExecutionListeners应该被通知结束事件。**注意：***当前仅当通过“startInstructions”属性提交启动指令时才考虑此选项。 |
| skipIoMappings        | 跳过执行 [input/output variable mappings](https://docs.camunda.org/manual/7.15/user-guide/process-engine/variables/#input-output-variable-mapping) |
| withVariablesInReturn | 指示是否应返回流程实例在执行期间使用的变量。默认值：false    |

Result: JSON Object

| Name           | Value   | Description                    |
| :------------- | :------ | :----------------------------- |
| id             | String  | 流程实例ID                     |
| definitionId   | String  | 流程定义ID                     |
| businessKey    | String  | 流程实例的业务key              |
| caseInstanceId | String  | 流程实例对应的案例实例ID       |
| tenantId       | String  | 租户                           |
| ended          | Boolean | 实例是否正在运行（runing)      |
| suspended      | Boolean | 实例是否挂起                   |
| links          | Object  | 和这个实例交互的Links JSON数组 |
| variables      | Object  | 流程实例的变量                 |

### [Submit Start Form](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/post-submit-form/) 提交表单启动

POST `/process-definition/{id}/submit-form`
POST `/process-definition/key/{key}/submit-form`
POST `/process-definition/key/{key}/tenant-id/{tenant-id}/submit-form`

使用一组流程变量和业务键启动流程实例。如果start事件定义了表单字段元数据，流程引擎将对定义了验证器的任何表单字段执行后端验证。请参阅有关生成的任务表单的文档。

Request Body：

| Name        | Description                |
| :---------- | :------------------------- |
| variables   | 初始化流程的参数。JSON对象 |
| businessKey | BusinessKey.               |

Result: JSON Object

| Name           | Value   | Description                    |
| :------------- | :------ | :----------------------------- |
| id             | String  | 流程实例ID                     |
| definitionId   | String  | 流程定义ID                     |
| businessKey    | String  | 流程实例的业务key              |
| caseInstanceId | String  | 流程实例对应的案例实例ID       |
| tenantId       | String  | 租户                           |
| ended          | Boolean | 实例是否正在运行（runing)      |
| suspended      | Boolean | 实例是否挂起                   |
| links          | Object  | 和这个实例交互的Links JSON数组 |



### [Activate/Suspend By Id](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/put-activate-suspend-by-id/) 激活/挂起

PUT `/process-definition/{id}/suspended`
PUT `/process-definition/key/{key}/suspended`
PUT `/process-definition/key/{key}/tenant-id/{tenant-id}/suspended`

Request Body：

| Name                    | Description                                                  |
| :---------------------- | :----------------------------------------------------------- |
| suspended               | true-挂起，false-激活                                        |
| includeProcessInstances | true-挂起该流程定义下的所有流程实例。false-该流程定义下的所有流程实例的挂起状态不会被更新. |
| executionDate           | 挂起或激活的时间，否则立即执行。时间格式 `yyyy-MM-dd'T'HH:mm:ss`, e.g., `2013-01-23T14:42:45`. |

Result: None



### [Activate/Suspend By Key](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/put-activate-suspend-by-key/) 激活/挂起（通过KEY）

PUT `/process-definition/suspended`

Request Body：

| Name                    | Description                                                  |
| :---------------------- | :----------------------------------------------------------- |
| processDefinitionKey    | 流程定义的KEY                                                |
| suspended               | true-挂起，false-激活                                        |
| includeProcessInstances | true-挂起该流程定义下的所有流程实例。false-该流程定义下的所有流程实例的挂起状态不会被更新. |
| executionDate           | 挂起或激活的时间，否则立即执行。时间格式 `yyyy-MM-dd'T'HH:mm:ss`, e.g., `2013-01-23T14:42:45`. |



### [Update history time to live](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/put-history-time-to-live/) 历史存活时间

PUT `/process-definition/{id}/history-time-to-live`
PUT `/process-definition/key/{key}/history-time-to-live`
PUT `/process-definition/key/{key}/tenant-id/{tenant-id}/history-time-to-live`

更新流程定义的历史存活时间. 在 [History cleanup](https://docs.camunda.org/manual/7.15/user-guide/process-engine/history/#history-cleanup)中使用.

Request Body：

| Name              | Description                         |
| :---------------- | :---------------------------------- |
| historyTimeToLive | 新的历史存活时间.可为空，不能为负数 |



### [Delete](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/delete-process-definition/) 删除

从一个部署中通过id删除一个流程定义

DELETE `/process-definition/{id}`

Query Parameters：

| Name                | Description                                                  |
| :------------------ | :----------------------------------------------------------- |
| cascade             | `true` 级联删除所有实例、历史实例和任务                      |
| skipCustomListeners | `true`,如果只有内置的ExecutionListeners应该被通知结束事件。  |
| skipIoMappings      | 一个布尔值，用于控制在删除过程中是否应执行输入/输出映射，如果不应调用输入/输出映射，则为`true`. |



### [Delete By Key](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/delete-by-key/) 删除

通过Key删除流程定义

DELETE `/process-definition/key/{key}/delete`
DELETE `/process-definition/key/{key}/tenant-id/{tenant-id}/delete`

Query Parameters：

| Name                | Description                                                  |
| :------------------ | :----------------------------------------------------------- |
| cascade             | `true` 级联删除所有实例、历史实例和任务                      |
| skipCustomListeners | `true`,如果只有内置的ExecutionListeners应该被通知结束事件。  |
| skipIoMappings      | 一个布尔值，用于控制在删除过程中是否应执行输入/输出映射，如果不应调用输入/输出映射，则为`true`. |



### [Get Deployed Start Form](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/get-deployed-start-form/) 获取开始表单

获取从开始事件（StartEvent）中可被引用的部署表单

GET `/process-definition/{id}/deployed-start-form`
GET `/process-definition/key/{key}/deployed-start-form`
GET `/process-definition/key/{key}/tenant-id/{tenant-id}/deployed-start-form`

返回表单内容 e.g

```xml
<form role="form" name="invoiceForm"
      class="form-horizontal">

  <div class="form-group">
    <label class="control-label col-md-4"
           for="creditor">Creditor</label>
    <div class="col-md-8">
      <input cam-variable-name="creditor"
             cam-variable-type="String"
             id="creditor"
             class="form-control"
             type="text"
             required />
      <div class="help">
        (e.g. &quot;Great Pizza for Everyone Inc.&quot;)
      </div>
    </div>
  </div>

</form>
```



### [Restart Process Instance](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/post-restart-process-instance-sync/) 重启流程实例

重启被取消的、终止的或完成的流程实例。它将创建一个全新的实例。

POST `/process-definition/{id}/restart`

Request Body：

| Name                         | Description                                                  |
| :--------------------------- | :----------------------------------------------------------- |
| processInstanceIds           | 待重启的流程实例id列表                                       |
| historicProcessInstanceQuery | 历史流程查询语句，定义：POST /history/process-instance` ](https://docs.camunda.org/manual/7.15/reference/rest/history/process-instance/post-process-instance-query/#request-body). |
| skipCustomListeners          | 跳过执行监听器调用                                           |
| skipIoMappings               | 跳过输入输出变量映射                                         |
| initialVariables             | 使用初始的变量集（默认使用上次的变量集）                     |
| withoutBusinessKey           | 不替换原来的业务键.                                          |
| instructions                 | 指令数组。                                                   |

其中指令：

| Name         | Description                                                  |
| :----------- | :----------------------------------------------------------- |
| type         | **强制.**包括: `startBeforeActivity`, `startAfterActivity`, `startTransition`. 其中`startBeforeActivity`请求进入一个Activity. `startAfterActivity`请求执行一个Activity的单输出序列流. `startTransition` 请求执行指定的序列流. |
| activityId   | Activity ID.                                                 |
| transitionId | 指定开始的序列流                                             |

### [Restart Process Instance Async](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/post-restart-process-instance-async/) 异步重启流程实例

POST `/process-definition/{id}/restart-async`

Request Body：[同上](#[Restart Process Instance](https://docs.camunda.org/manual/7.15/reference/rest/process-definition/post-restart-process-instance-sync/) 重启流程实例)

Result:

| Name                   | Value   | Description                                                  |
| :--------------------- | :------ | :----------------------------------------------------------- |
| id                     | String  | 批次(Batch)ID                                                |
| type                   | String  | 批次类型. 见 [User Guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/batch/#creating-a-batch) |
| totalJobs              | Number  | 批次中包含多少作业.                                          |
| jobsCreated            | Number  | 种子作业已经创建的批执行作业的数量。                         |
| batchJobsPerSeed       | Number  | 每次种子作业调用创建的批处理执行作业数。将调用批处理种子作业，直到它创建了批处理所需的所有批处理执行作业（请参见“totalJobs”属性）。 |
| invocationsPerBatchJob | Number  | 每个批处理执行作业都会多次调用批处理“invocasperbatchjob”执行的命令。例如，对于流程实例迁移批处理，这指定了每个批处理执行作业迁移的流程实例数。 |
| seedJobDefinitionId    | String  | 种子作业的定义ID.                                            |
| monitorJobDefinitionId | String  | 监控作业的定义ID                                             |
| batchJobDefinitionId   | String  | 批执行作业的定义ID                                           |
| suspended              | Boolean | 该批次是否被挂起                                             |
| tenantId               | String  | 租户                                                         |
| createUserId           | String  | 创建该批次任务的用户ID                                       |



## Task 任务管理

### [Get](https://docs.camunda.org/manual/7.15/reference/rest/task/get/) 获取任务信息

GET `/task/{id}`

| Name                | Value   | Description                                                  |
| :------------------ | :------ | :----------------------------------------------------------- |
| id                  | String  | 任务ID                                                       |
| name                | String  | 任务名称                                                     |
| assignee            | String  | 被指派的用户                                                 |
| created             | String  | 创建时间                                                     |
| due                 | String  | 截止日期                                                     |
| followUp            | String  | 后续日期                                                     |
| delegationState     | String  | 任务的委派状态。对应于引擎中的“DelegationState”枚举。可能的值是'RESOLVED'和'PENDING'。 |
| description         | String  | 描述                                                         |
| executionId         | String  | 任务归属的执行ID（execution ）                               |
| owner               | String  | 拥有者                                                       |
| parentTaskId        | String  | 父任务的id（如果此任务是子任务）。                           |
| priority            | Number  | 优先级                                                       |
| processDefinitionId | String  | 归属的流程定义ID                                             |
| processInstanceId   | String  | 归属的流程实例ID                                             |
| caseExecutionId     | String  | 归属的案例执行ID                                             |
| caseDefinitionId    | String  | 归属的案例定义ID                                             |
| caseInstanceId      | String  | 归属的案例实例ID                                             |
| taskDefinitionKey   | String  | 任务定义KEY                                                  |
| suspended           | Boolean | 归属的流程是否被挂起                                         |
| formKey             | String  | 该任务对应的表单键（可空）                                   |
| tenantId            | String  | 该任务对应的租户ID（可空）                                   |

[更多信息](https://docs.camunda.org/manual/7.15/reference/rest/overview/date-format/).

### [Get List](https://docs.camunda.org/manual/7.15/reference/rest/task/get-query/) 获取任务列表

GET `/task`

| Name                                     | Description                                                  |
| :--------------------------------------- | :----------------------------------------------------------- |
| processInstanceId                        | 所属流程实例ID.                                              |
| processInstanceIdIn                      | 所属流程实例ID列表                                           |
| taskId                                   | 任务ID                                                       |
| processInstanceIdIn                      | 任务ID列表                                                   |
| processInstanceBusinessKey               | 所属流程实例的业务键（BusinessKey）                          |
| processInstanceBusinessKeyExpression     | 所属流程实例的业务键描述信息. 见 [user guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/expression-language/#internal-context-functions) . |
| processInstanceBusinessKeyIn             | 所属流程实例的业务键列表（逗号分割）.                        |
| processInstanceBusinessKeyLike           | 所属流程实例的业务键子字符串匹配                             |
| processInstanceBusinessKeyLikeExpression | 所属流程实例的业务键描述子字符串匹配.                        |
| processDefinitionId                      | 所属流程实例定义ID                                           |
| processDefinitionKey                     | 所属流程实例定义KEY                                          |
| processDefinitionKeyIn                   | 所属流程实例定义KEY列表（逗号分隔）.                         |
| processDefinitionName                    | 所属流程实例定义名称                                         |
| processDefinitionNameLike                | 所属流程实例定义名称子字符串匹配                             |
| executionId                              | 所属执行器（execution ）ID.                                  |
| caseInstanceId                           | 所属案例实例ID                                               |
| caseInstanceBusinessKey                  | 所属案例实例业务键                                           |
| caseInstanceBusinessKeyLike              | 所属案例实例业务键子字符串匹配                               |
| caseDefinitionId                         | 所属案例定义ID                                               |
| caseDefinitionKey                        | 所属案例实例KEY                                              |
| caseDefinitionName                       | 所属案例实例名称                                             |
| caseDefinitionNameLike                   | 所属案例实例名称子字符串匹配.                                |
| caseExecutionId                          | 所属案例执行ID                                               |
| activityInstanceIdIn                     | 所属Activity实例IDs                                          |
| tenantIdIn                               | 所属租户IDs                                                  |
| withoutTenantId                          | 不属于任何租户                                               |
| assignee                                 | 被指派人                                                     |
| assigneeExpression                       | 被指派人表达式                                               |
| assigneeLike                             | 被指派人                                                     |
| assigneeLikeExpression                   | Restrict to tasks that have an assignee that has the parameter value described by the given expression as a substring. See the [user guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/expression-language/#internal-context-functions) for more information on available functions. |
| assigneeIn                               | Only include tasks which are assigned to one of the passed and comma-separated user ids. |
| owner                                    | Restrict to tasks that the given user owns.                  |
| ownerExpression                          | Restrict to tasks that the user described by the given expression owns. See the [user guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/expression-language/#internal-context-functions) for more information on available functions. |
| candidateGroup                           | Only include tasks that are offered to the given group.      |
| candidateGroupExpression                 | Only include tasks that are offered to the group described by the given expression. See the [user guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/expression-language/#internal-context-functions) for more information on available functions. |
| candidateUser                            | Only include tasks that are offered to the given user or to one of his groups. |
| candidateUserExpression                  | Only include tasks that are offered to the user described by the given expression. See the [user guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/expression-language/#internal-context-functions) for more information on available functions. |
| includeAssignedTasks                     | Also include tasks that are assigned to users in candidate queries. Default is to only include tasks that are not assigned to any user if you query by candidate user or group(s). |
| involvedUser                             | Only include tasks that the given user is involved in. A user is involved in a task if an identity link exists between task and user (e.g., the user is the assignee). |
| involvedUserExpression                   | Only include tasks that the user described by the given expression is involved in. A user is involved in a task if an identity link exists between task and user (e.g., the user is the assignee). See the [user guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/expression-language/#internal-context-functions) for more information on available functions. |
| assigned                                 | If set to `true`, restricts the query to all tasks that are assigned. |
| unassigned                               | If set to `true`, restricts the query to all tasks that are unassigned. |
| taskDefinitionKey                        | Restrict to tasks that have the given key.                   |
| taskDefinitionKeyIn                      | Restrict to tasks that have one of the given keys. The keys need to be in a comma-separated list. |
| taskDefinitionKeyLike                    | Restrict to tasks that have a key that has the parameter value as a substring. |
| name                                     | Restrict to tasks that have the given name.                  |
| nameNotEqual                             | Restrict to tasks that do not have the given name.           |
| nameLike                                 | Restrict to tasks that have a name with the given parameter value as substring. |
| nameNotLike                              | Restrict to tasks that do not have a name with the given parameter value as substring. |
| description                              | Restrict to tasks that have the given description.           |
| descriptionLike                          | Restrict to tasks that have a description that has the parameter value as a substring. |
| priority                                 | Restrict to tasks that have the given priority.              |
| maxPriority                              | Restrict to tasks that have a lower or equal priority.       |
| minPriority                              | Restrict to tasks that have a higher or equal priority.      |
| dueDate                                  | Restrict to tasks that are due on the given date. By default*, the date must have the format `yyyy-MM-dd'T'HH:mm:ss.SSSZ`, e.g., `2013-01-23T14:42:45.546+0200`. |
| dueDateExpression                        | Restrict to tasks that are due on the date described by the given expression. See the [user guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/expression-language/#internal-context-functions) for more information on available functions. The expression must evaluate to a `java.util.Date` or `org.joda.time.DateTime` object. |
| dueAfter                                 | Restrict to tasks that are due after the given date. By default*, the date must have the format `yyyy-MM-dd'T'HH:mm:ss.SSSZ`, e.g., `2013-01-23T14:42:45.435+0200`. |
| dueAfterExpression                       | Restrict to tasks that are due after the date described by the given expression. See the [user guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/expression-language/#internal-context-functions) for more information on available functions. The expression must evaluate to a `java.util.Date` or `org.joda.time.DateTime` object. |
| dueBefore                                | Restrict to tasks that are due before the given date. By default*, the date must have the format `yyyy-MM-dd'T'HH:mm:ss.SSSZ`, e.g., `2013-01-23T14:42:45.243+0200`. |
| dueBeforeExpression                      | Restrict to tasks that are due before the date described by the given expression. See the [user guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/expression-language/#internal-context-functions) for more information on available functions. The expression must evaluate to a `java.util.Date` or `org.joda.time.DateTime` object. |
| withoutDueDate                           | Only include tasks which have no due date. Value may only be `true`, as `false` is the default behavior. |
| followUpDate                             | Restrict to tasks that have a followUp date on the given date. By default*, the date must have the format `yyyy-MM-dd'T'HH:mm:ss.SSSZ`, e.g., `2013-01-23T14:42:45.342+0200`. |
| followUpDateExpression                   | Restrict to tasks that have a followUp date on the date described by the given expression. See the [user guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/expression-language/#internal-context-functions) for more information on available functions. The expression must evaluate to a `java.util.Date` or `org.joda.time.DateTime` object. |
| followUpAfter                            | Restrict to tasks that have a followUp date after the given date. By default*, the date must have the format `yyyy-MM-dd'T'HH:mm:ss.SSSZ`, e.g., `2013-01-23T14:42:45.542+0200`. |
| followUpAfterExpression                  | Restrict to tasks that have a followUp date after the date described by the given expression. See the [user guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/expression-language/#internal-context-functions) for more information on available functions. The expression must evaluate to a `java.util.Date` or `org.joda.time.DateTime` object. |
| followUpBefore                           | Restrict to tasks that have a followUp date before the given date. By default*, the date must have the format `yyyy-MM-dd'T'HH:mm:ss.SSSZ`, e.g., `2013-01-23T14:42:45.234+0200`. |
| followUpBeforeExpression                 | Restrict to tasks that have a followUp date before the date described by the given expression. See the [user guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/expression-language/#internal-context-functions) for more information on available functions. The expression must evaluate to a `java.util.Date` or `org.joda.time.DateTime` object. |
| followUpBeforeOrNotExistent              | Restrict to tasks that have no followUp date or a followUp date before the given date. By default*, the date must have the format `yyyy-MM-dd'T'HH:mm:ss.SSSZ`, e.g., `2013-01-23T14:42:45.432+0200`. The typical use case is to query all "active" tasks for a user for a given date. |
| followUpBeforeOrNotExistentExpression    | Restrict to tasks that have no followUp date or a followUp date before the date described by the given expression. See the [user guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/expression-language/#internal-context-functions) for more information on available functions. The expression must evaluate to a `java.util.Date` or `org.joda.time.DateTime` object. |
| createdOn                                | Restrict to tasks that were created on the given date. By default*, the date must have the format `yyyy-MM-dd'T'HH:mm:ss.SSSZ`, e.g., `2013-01-23T14:42:45.324+0200`. |
| createdOnExpression                      | Restrict to tasks that were created on the date described by the given expression. See the [user guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/expression-language/#internal-context-functions) for more information on available functions. The expression must evaluate to a `java.util.Date` or `org.joda.time.DateTime` object. |
| createdAfter                             | Restrict to tasks that were created after the given date. By default*, the date must have the format `yyyy-MM-dd'T'HH:mm:ss.SSSZ`, e.g., `2013-01-23T14:42:45.342+0200`. |
| createdAfterExpression                   | Restrict to tasks that were created after the date described by the given expression. See the [user guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/expression-language/#internal-context-functions) for more information on available functions. The expression must evaluate to a `java.util.Date` or `org.joda.time.DateTime` object. |
| createdBefore                            | Restrict to tasks that were created before the given date. By default*, the date must have the format `yyyy-MM-dd'T'HH:mm:ss.SSSZ`, e.g., `2013-01-23T14:42:45.332+0200`. |
| createdBeforeExpression                  | Restrict to tasks that were created before the date described by the given expression. See the [user guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/expression-language/#internal-context-functions) for more information on available functions. The expression must evaluate to a `java.util.Date` or `org.joda.time.DateTime` object. |
| delegationState                          | Restrict to tasks that are in the given delegation state. Valid values are `PENDING` and `RESOLVED`. |
| candidateGroups                          | Restrict to tasks that are offered to any of the given candidate groups. Takes a comma-separated list of group names, so for example `developers,support,sales`. |
| candidateGroupsExpression                | Restrict to tasks that are offered to any of the candidate groups described by the given expression. See the [user guide](https://docs.camunda.org/manual/7.15/user-guide/process-engine/expression-language/#internal-context-functions) for more information on available functions. The expression must evaluate to `java.util.List` of Strings. |
| withCandidateGroups                      | Only include tasks which have a candidate group. Value may only be `true`, as `false` is the default behavior. |
| withoutCandidateGroups                   | Only include tasks which have no candidate group. Value may only be `true`, as `false` is the default behavior. |
| withCandidateUsers                       | Only include tasks which have a candidate user. Value may only be `true`, as `false` is the default behavior. |
| withoutCandidateUsers                    | Only include tasks which have no candidate users. Value may only be `true`, as `false` is the default behavior. |
| active                                   | Only include active tasks. Value may only be `true`, as `false` is the default behavior. |
| suspended                                | Only include suspended tasks. Value may only be `true`, as `false` is the default behavior. |
| taskVariables                            | Only include tasks that have variables with certain values. Variable filtering expressions are comma-separated and are structured as follows: A valid parameter value has the form `key_operator_value`. `key` is the variable name, `operator` is the comparison operator to be used and `value` the variable value. **Note:** Values are always treated as `String` objects on server side.  Valid operator values are: `eq` - equal to; `neq` - not equal to; `gt` - greater than; `gteq` - greater than or equal to; `lt` - lower than; `lteq` - lower than or equal to; `like`. `key` and `value` may not contain underscore or comma characters. |
| processVariables                         | Only include tasks that belong to process instances that have variables with certain values. Variable filtering expressions are comma-separated and are structured as follows: A valid parameter value has the form `key_operator_value`. `key` is the variable name, `operator` is the comparison operator to be used and `value` the variable value. **Note:** Values are always treated as `String` objects on server side.  Valid operator values are: `eq` - equal to; `neq` - not equal to; `gt` - greater than; `gteq` - greater than or equal to; `lt` - lower than; `lteq` - lower than or equal to; `like`;`notLike`. `key` and `value` may not contain underscore or comma characters. |
| caseInstanceVariables                    | Only include tasks that belong to case instances that have variables with certain values. Variable filtering expressions are comma-separated and are structured as follows: A valid parameter value has the form `key_operator_value`. `key` is the variable name, `operator` is the comparison operator to be used and `value` the variable value. **Note:** Values are always treated as `String` objects on server side.  Valid operator values are: `eq` - equal to; `neq` - not equal to; `gt` - greater than; `gteq` - greater than or equal to; `lt` - lower than; `lteq` - lower than or equal to; `like`. `key` and `value` may not contain underscore or comma characters. |
| variableNamesIgnoreCase                  | Match all variable names in this query case-insensitively. If set `variableName` and `variablename` are treated as equal. |
| variableValuesIgnoreCase                 | Match all variable values in this query case-insensitively. If set `variableValue` and `variablevalue` are treated as equal. |
| parentTaskId                             | Restrict query to all tasks that are sub tasks of the given task. Takes a task id. |
| sortBy                                   | Sort the results lexicographically by a given criterion. Valid values are `instanceId`, `caseInstanceId`, `dueDate`, `executionId`, `caseExecutionId`,`assignee`, `created`, `description`, `id`, `name`, `nameCaseInsensitive` and `priority`. Must be used in conjunction with the `sortOrder` parameter. |
| sortOrder                                | Sort the results in a given order. Values may be `asc` for ascending order or `desc` for descending order. Must be used in conjunction with the `sortBy` parameter. |
| firstResult                              | Pagination of results. Specifies the index of the first result to return. |
| maxResults                               | Pagination of results. Specifies the maximum number of results to return. Will return less results if there are no more results left. |



[Get List (POST)](https://docs.camunda.org/manual/7.15/reference/rest/task/post-query/)

POST `/task`

[Get List Count](https://docs.camunda.org/manual/7.15/reference/rest/task/get-query-count/)

GET `/task/count`

[Get List Count (POST)](https://docs.camunda.org/manual/7.15/reference/rest/task/post-query-count/)

POST `/task/count`

[Get Form Key](https://docs.camunda.org/manual/7.15/reference/rest/task/get-form-key/)

GET `/task/{id}/form`

[Claim](https://docs.camunda.org/manual/7.15/reference/rest/task/post-claim/)

POST `/task/{id}/claim`

[Unclaim](https://docs.camunda.org/manual/7.15/reference/rest/task/post-unclaim/)

POST `/task/{id}/unclaim`

[Complete](https://docs.camunda.org/manual/7.15/reference/rest/task/post-complete/)

POST `/task/{id}/complete`

[Submit Form](https://docs.camunda.org/manual/7.15/reference/rest/task/post-submit-form/)

POST `/task/{id}/submit-form`

[Resolve](https://docs.camunda.org/manual/7.15/reference/rest/task/post-resolve/)

POST `/task/{id}/resolve`

[Set Assignee](https://docs.camunda.org/manual/7.15/reference/rest/task/post-assignee/)

POST `/task/{id}/assignee`

[Delegate](https://docs.camunda.org/manual/7.15/reference/rest/task/post-delegate/)

POST `/task/{id}/delegate`

[Get Deployed Form](https://docs.camunda.org/manual/7.15/reference/rest/task/get-deployed-form/)

GET `/task/{id}/deployed-form`

[Get Rendered Form](https://docs.camunda.org/manual/7.15/reference/rest/task/get-rendered-form/)

GET `/task/{id}/rendered-form`

[Get Task Form Variables](https://docs.camunda.org/manual/7.15/reference/rest/task/get-form-variables/)

GET `/task/{id}/form-variables`

[Create](https://docs.camunda.org/manual/7.15/reference/rest/task/post-create/)

POST `/task/create`

[Update](https://docs.camunda.org/manual/7.15/reference/rest/task/put-update/)

PUT `/task/{id}/`

[Handle BPMN Error](https://docs.camunda.org/manual/7.15/reference/rest/task/post-bpmn-error/)

POST `/task/{id}/bpmnError`

[Handle BPMN Escalation](https://docs.camunda.org/manual/7.15/reference/rest/task/post-bpmn-escalation/)

POST `/task/{id}/bpmnEscalation`

[Delete](https://docs.camunda.org/manual/7.15/reference/rest/task/delete/)

DELETE `/task/{id}`
