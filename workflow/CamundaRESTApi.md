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
| assigneeLikeExpression                   | 被指派人表达式子字符串.                                      |
| assigneeIn                               | 被指派人id列表.                                              |
| owner                                    | 拥有者                                                       |
| ownerExpression                          | 拥有者表达式                                                 |
| candidateGroup                           | 候选组。被提供给指定组的任务                                 |
| candidateGroupExpression                 | 候选组表达式                                                 |
| candidateUser                            | 任务候选人或其所在组.                                        |
| candidateUserExpression                  | 任务候选人或其所在组表达式.                                  |
| includeAssignedTasks                     | 在使用候选人/组查询时，包含已指派给用户的任务.               |
| involvedUser                             | 只包括给定用户参与的任务。如果任务和用户之间存在标识链接，则用户参与任务(e.g.,被指派的人). |
| involvedUserExpression                   | 参与用户表达式.                                              |
| assigned                                 | true：被指派的任务                                           |
| unassigned                               | true：未被指派的任务                                         |
| taskDefinitionKey                        | 任务定义KEY                                                  |
| taskDefinitionKeyIn                      | 任务定义KEY数组.                                             |
| taskDefinitionKeyLike                    | 任务定义KEY子字符串.                                         |
| name                                     | 任务名称                                                     |
| nameNotEqual                             | 任务名称不等于                                               |
| nameLike                                 | 任务名称子字符串匹配                                         |
| nameNotLike                              | 不包含指定名称子字符串匹配的任务                             |
| description                              | 描述                                                         |
| descriptionLike                          | 描述子字符串匹配                                             |
| priority                                 | 优先级                                                       |
| maxPriority                              | 返回低于或等于该优先级的任务                                 |
| minPriority                              | 返回高于或等于该优先级的任务                                 |
| dueDate                                  | 给定日期到期的任务.                                          |
| dueDateExpression                        | 给定到期时间表达式.                                          |
| dueAfter                                 | 给定日期之后过期的任务                                       |
| dueAfterExpression                       | 之后过期日期表达式                                           |
| dueBefore                                | 给定日期之前过期的任务                                       |
| dueBeforeExpression                      | 之前过期日期表达式                                           |
| withoutDueDate                           | true：没有过期日期的任务                                     |
| followUpDate                             | 指定日期有后续日期的任务（followUp date ）.                  |
| followUpDateExpression                   | 指定日期有后续日期的表达式                                   |
| followUpAfter                            | 指定日期之后有后续日期的任务.                                |
| followUpAfterExpression                  | 指定日期之后有后续日期的表达式                               |
| followUpBefore                           | 指定日期之前有后续日期的任务.                                |
| followUpBeforeExpression                 | 指定日期之前有后续日期的表达式                               |
| followUpBeforeOrNotExistent              | 仅限于没有后续日期，或在给定日期之前没有后续日期的任务. 典型的用例是查询给定日期内用户的所有“活动”任务。 |
| followUpBeforeOrNotExistentExpression    | 上面条件的表达式写法                                         |
| createdOn                                | 指定创建日期                                                 |
| createdOnExpression                      | 创建日期表达式                                               |
| createdAfter                             | 在指定日期之后创建                                           |
| createdAfterExpression                   | 之后创建日期表达式                                           |
| createdBefore                            | 在指定日期之前创建                                           |
| createdBeforeExpression                  | 之前创建日期表达式                                           |
| delegationState                          | 任务委托状态，包括 `PENDING` 和`RESOLVED`.                   |
| candidateGroups                          | 提供给指定候选组的任务，例如 `developers,support,sales`.     |
| candidateGroupsExpression                | 指定候选组表达式                                             |
| withCandidateGroups                      | true：包含候选组的任务                                       |
| withoutCandidateGroups                   | true：不具有候选组的任务                                     |
| withCandidateUsers                       | true：包含候选人的任务                                       |
| withoutCandidateUsers                    | true：不包含候选人的任务                                     |
| active                                   | true：激活的任务                                             |
| suspended                                | true：挂起的任务                                             |
| taskVariables                            | 按任务变量选取，逗号分隔的表达式，表达式格式为 `key_operator_value`. `key` 变量名称, `operator`算符， `value`值. **注意:** 值在服务端一直被当做 `String` 对象处理.  运算符包括: `eq`  `neq`  `gt`  `gteq`  `lt` `lteq`  `like`. |
| processVariables                         | 按流程实例的参数选取。表达式同上.                            |
| caseInstanceVariables                    | 按案例实例的参数选取                                         |
| variableNamesIgnoreCase                  | 参数名称忽略大小写.                                          |
| variableValuesIgnoreCase                 | 参数值忽略大小写                                             |
| parentTaskId                             | 父任务ID.                                                    |
| sortBy                                   | `instanceId`, `caseInstanceId`, `dueDate`, `executionId`, `caseExecutionId`,`assignee`, `created`, `description`, `id`, `name`, `nameCaseInsensitive` `priority`. |
| sortOrder                                | desc/asc.                                                    |
| firstResult                              | 分页                                                         |
| maxResults                               | 分页                                                         |

Result：数组。结构[同上](#[Get](https://docs.camunda.org/manual/7.15/reference/rest/task/get/) 获取任务信息)



### [Get List (POST)](https://docs.camunda.org/manual/7.15/reference/rest/task/post-query/) 获取任务列表

POST `/task`

[同上](#[Get](https://docs.camunda.org/manual/7.15/reference/rest/task/get/) 获取任务信息)



### [Get List Count](https://docs.camunda.org/manual/7.15/reference/rest/task/get-query-count/) 获取任务列表个数

GET `/task/count`

[同上](#[Get](https://docs.camunda.org/manual/7.15/reference/rest/task/get/) 获取任务信息)



### [Get List Count (POST)](https://docs.camunda.org/manual/7.15/reference/rest/task/post-query-count/) 获取任务列表个数

POST `/task/count`

[同上](#[Get](https://docs.camunda.org/manual/7.15/reference/rest/task/get/) 获取任务信息)



### [Get Form Key](https://docs.camunda.org/manual/7.15/reference/rest/task/get-form-key/) 获取表单键

返回任务的表单键。表单键对应引擎里 FormData#formKey。此键可用于在客户端应用程序中执行特定于任务的表单呈现。此外，还将返回包含流程应用程序的上下文路径

GET `/task/{id}/form`

Result：

| Name        | Value  | Description                                                  |
| :---------- | :----- | :----------------------------------------------------------- |
| key         | String | 任务的表单键                                                 |
| contextPath | String | 任务所属的流程应用上下文路径。(The process application's context path). |



### [Claim](https://docs.camunda.org/manual/7.15/reference/rest/task/post-claim/) 声明任务

为特定用户声明任务。（同指派）

注意：与Set Assignee方法的区别在于，这里执行一个检查，查看任务是否已经分配了一个用户。

POST `/task/{id}/claim`

Request Body:

| Name   | Description          |
| :----- | :------------------- |
| userId | 声明任务的用户的id。 |



### [Unclaim](https://docs.camunda.org/manual/7.15/reference/rest/task/post-unclaim/) 反声明（取消指派）

重置任务的指派人。成功后，任务将不再被指派给某个用户。

POST `/task/{id}/unclaim`



### [Complete](https://docs.camunda.org/manual/7.15/reference/rest/task/post-complete/) 完成任务	

完成任务并更新流程变量

POST `/task/{id}/complete`

Request Body:

| Name                  | Description                                                  |
| :-------------------- | :----------------------------------------------------------- |
| variables             | 键值对. 值里面包含value/type/valueInfo                       |
| withVariablesInReturn | 指示返回是否应包含流程变量。默认值为false，响应代码为204。如果设置为true，则响应包含流程变量，响应代码为200。如果任务与流程实例不关联（例如，如果它是案例实例的一部分），则不会返回任何变量。 |

例如：

POST `/task/anId/submit-form`

```json
{
    "variables": {
        "aVariable": {
            "value": "aStringValue"
        },
        "anotherVariable": {
            "value": 42
        },
        "aThirdVariable": {
            "value": true
        },
        "aFileVariable": {
            "value": "TG9yZW0gaXBzdW0=",
            "type": "File",
            "valueInfo": {
                "filename": "myFile.txt"
            }
        }
    }
}
```

Return 204

### [Submit Form](https://docs.camunda.org/manual/7.15/reference/rest/task/post-submit-form/) 提交表单

完成任务并更新流程变量。和[complete](#[Complete](https://docs.camunda.org/manual/7.15/reference/rest/task/post-complete/) 完成任务	)的差别有二

+ 如果任务处于挂起状态（即之前已被委派），则它不会完成，而是已解决。否则就完成了。
+ 后端验证。如果任务定义了表单字段元数据，流程引擎将对定义了验证器的任何表单字段执行后端验证。有关详细信息，请参阅《用户指南》的“生成的任务表单”部分。

POST `/task/{id}/submit-form`

Request Body：同上



### [Resolve](https://docs.camunda.org/manual/7.15/reference/rest/task/post-resolve/) 解决

解决任务并更新执行变量。

解决一个任务标志着受让人完成了委托给他们的任务，并且可以将任务发送回所有者。只能在已委派任务时执行。受让人将被设置为执行委托的所有者。

POST `/task/{id}/resolve`

Request Body:

| Name                  | Description                                                  |
| :-------------------- | :----------------------------------------------------------- |
| variables             | 键值对. 值里面包含value/type/valueInfo                       |
| withVariablesInReturn | 指示返回是否应包含流程变量。默认值为false，响应代码为204。如果设置为true，则响应包含流程变量，响应代码为200。如果任务与流程实例不关联（例如，如果它是案例实例的一部分），则不会返回任何变量。 |

### [Set Assignee](https://docs.camunda.org/manual/7.15/reference/rest/task/post-assignee/) 指派用户

改变任务的指派人

注意：和Claim方法的区别是，这个方法不检查当前任务是否已被指派

POST `/task/{id}/assignee`

| Name   | Description      |
| :----- | :--------------- |
| userId | 将被指派的用户ID |



### [Delegate](https://docs.camunda.org/manual/7.15/reference/rest/task/post-delegate/) 委托给另外一个用户

POST `/task/{id}/delegate`

| Name   | Description              |
| :----- | :----------------------- |
| userId | 任务应委派给的用户的id。 |



### [Get Deployed Form](https://docs.camunda.org/manual/7.15/reference/rest/task/get-deployed-form/) 获取表单

获取给定任务引用的已部署表单。

GET `/task/{id}/deployed-form`

Result：已部署表单内容

e.g.

```html
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



### [Get Rendered Form](https://docs.camunda.org/manual/7.15/reference/rest/task/get-rendered-form/) 获取通用任务渲染表单

返回一个任务的已渲染表单。获取[通用任务表单Generated Task Form](https://docs.camunda.org/manual/7.15/user-guide/task-forms/#generated-task-forms)的渲染HTML。

GET `/task/{id}/rendered-form`

Result: 渲染的HTML

例如

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



### [Get Task Form Variables](https://docs.camunda.org/manual/7.15/reference/rest/task/get-form-variables/) 获取表单变量

获取任务的表单变量。如果定义了表单字段，则会考虑表单字段的变量类型和默认值。

GET `/task/{id}/form-variables`

Query Parameters:

| Name              | Description                           |
| :---------------- | :------------------------------------ |
| variableNames     | 逗号分割的变量名称.                   |
| deserializeValues | 是否序列化变量应该在服务端被反序列化. |

Result:

| Name      | Value                              | Description                                                  |
| :-------- | :--------------------------------- | :----------------------------------------------------------- |
| value     | String / Number / Boolean / Object | 变量值.                                                      |
| type      | String                             | 变量类型                                                     |
| valueInfo | Object                             | 额外的、依赖值类型的属性.例如`Object`；类型的变量,将包含:`objectTypeName`: 对象类型.`serializationDataFormat`: 对象序列化格式. |

例如：

GET `/task/anId/form-variables`

GET `/task/anId/form-variables?variableNames=a,b,c`

返回：

```json
{
  "amount": {
      "type": "integer",
      "value": 5,
      "valueInfo": {}
  },
  "firstName": {
      "type": "String",
      "value": "Jonny",
      "valueInfo": {}
  }

}
```





### [Create](https://docs.camunda.org/manual/7.15/reference/rest/task/post-create/) 创建任务

POST `/task/create`

Request Body:

| Name            | Type   | Description                      |
| :-------------- | :----- | :------------------------------- |
| id              | String | 任务ID                           |
| name            | String | 任务名称                         |
| description     | String | 任务描述                         |
| assignee        | String | 指派用户                         |
| owner           | String | 任务拥有者                       |
|                 |        |                                  |
| delegationState | String | 委托状态 `RESOLVED` 或`PENDING`. |
| due             | String | 截止日期                         |
| followUp        | String | 后续日期(follow-up date)         |
| priority        | Number | 优先级                           |
| parentTaskId    | String | 父任务ID                         |
| caseInstanceId  | String | 任务所属案例实例ID               |
| tenantId        | String | 任务所属租户ID                   |



### [Update](https://docs.camunda.org/manual/7.15/reference/rest/task/put-update/) 更新任务

PUT `/task/{id}/`

[同上](#[Create](https://docs.camunda.org/manual/7.15/reference/rest/task/post-create/) 创建任务)

### [Handle BPMN Error](https://docs.camunda.org/manual/7.15/reference/rest/task/post-bpmn-error/) 报告BPMN错误

按id报告正在运行的任务上下文中的业务错误。必须指定错误代码才能标识BPMN错误处理程序。请参阅有关在用户任务中报告Bpmn错误的文档。

POST `/task/{id}/bpmnError`

Request Body:

| Name         | Description |
| :----------- | :---------- |
| errorCode    | BPMN错误码  |
| errorMessage | 错误描述    |
| variables    | 变量定义    |

### [Handle BPMN Escalation](https://docs.camunda.org/manual/7.15/reference/rest/task/post-bpmn-escalation/) 报告BPMN升级

按id报告正在运行的任务上下文中的升级。必须指定升级代码以标识升级处理程序。请参阅有关在用户任务中报告Bpmn升级的文档。

POST `/task/{id}/bpmnEscalation`

Request Body:

| Name           | Description |
| :------------- | :---------- |
| escalationCode | BPMN升级码  |
| variables      | 变量定义    |



### [Delete](https://docs.camunda.org/manual/7.15/reference/rest/task/delete/) 删除任务

DELETE `/task/{id}`



### [Comment Get List](https://docs.camunda.org/manual/7.15/reference/rest/task/comment/get-task-comments/) 获取任务批注

GET `/task/{id}/comment`

Result：

| Name                  | Value  | Description                  |
| :-------------------- | :----- | :--------------------------- |
| id                    | String | 批注ID                       |
| userId                | String | 创建批注用户                 |
| taskId                | String | 所属任务ID                   |
| processInstanceId     | String | 批注相关的流程实例ID         |
| time                  | Date   | 批注日期                     |
| message               | String | 批注内容                     |
| removalTime           | String | 历史清理的日期               |
| rootProcessInstanceId | String | 创建该任务所属流程的根流程ID |

### [Comment Get](https://docs.camunda.org/manual/7.15/reference/rest/task/comment/get-task-comment/) 获取任务批注

GET `/task/{id}/comment/{commentId}`

Result[同上](#[Comment Get List](https://docs.camunda.org/manual/7.15/reference/rest/task/comment/get-task-comments/) 获取任务批注)



### [Comment Create](https://docs.camunda.org/manual/7.15/reference/rest/task/comment/post-task-comment/) 创建任务批注

POST `/task/{id}/comment/create`

Request Body：

| Name              | Description          |
| :---------------- | :------------------- |
| message           | 批注内容             |
| processInstanceId | 指定到一个流程实例ID |



### [Attachment Get List](https://docs.camunda.org/manual/7.15/reference/rest/task/attachment/get-task-attachments/) 获取附件列表

GET `/task/{id}/attachment`

Result：

| Name                  | Value  | Description                     |
| :-------------------- | :----- | :------------------------------ |
| id                    | String | 附件ID                          |
| name                  | String | 附件名称                        |
| taskId                | String | 任务ID                          |
| description           | String | 描述                            |
| type                  | String | 附件内容类型，可以是 MIME或其他 |
| url                   | String | 远端附件URL                     |
| createTime            | String | 创建时间                        |
| removalTime           | String | 清理时间                        |
| rootProcessInstanceId | String | 创建该任务所属流程的根流程ID    |

### [Attachment Get](https://docs.camunda.org/manual/7.15/reference/rest/task/attachment/get-task-attachment/) 获取附件

GET `/task/{id}/attachment/{attachmentId}`

Result：

[同上](#[Attachment Get List](https://docs.camunda.org/manual/7.15/reference/rest/task/attachment/get-task-attachments/) 获取附件列表)



### [Attachment Create](https://docs.camunda.org/manual/7.15/reference/rest/task/attachment/post-task-attachment/) 创建附件

POST `/task/{id}/attachment/create`

Request Body：

<multipart form >

| Form Part Name         | Content Type | Description                                      |
| :--------------------- | :----------- | :----------------------------------------------- |
| attachment-name        | text/plain   | The name of the attachment.                      |
| attachment-description | text/plain   | The description of the attachment.               |
| attachment-type        | text/plain   | The type of the attachment.                      |
| url                    | text/plain   | The url to the remote content of the attachment. |
| content                | text/plain   | The content of the attachment.                   |

Result：

| Name                  | Value  | Description                     |
| :-------------------- | :----- | :------------------------------ |
| id                    | String | 附件ID                          |
| name                  | String | 附件名称                        |
| taskId                | String | 任务ID                          |
| description           | String | 描述                            |
| type                  | String | 附件内容类型，可以是 MIME或其他 |
| url                   | String | 远端附件URL                     |
| createTime            | String | 创建时间                        |
| removalTime           | String | 清理时间                        |
| rootProcessInstanceId | String | 创建该任务所属流程的根流程ID    |



### [Attachment Get (Binary)](https://docs.camunda.org/manual/7.15/reference/rest/task/attachment/get-task-attachment-data/) 获取附件（二进制）

GET `/task/{id}/attachment/{attachmentId}/data`

获取任务附件的二进制格式内容



### [Attachment Delete](https://docs.camunda.org/manual/7.15/reference/rest/task/attachment/delete-task-attachment/) 删除附件

DELETE `/task/{id}/attachment/{attachmentId}`



### [Identity Links Get List](https://docs.camunda.org/manual/7.15/reference/rest/task/identity-links/get-identity-links/) 获取标识链接

按id获取任务的标识链接，这些标识链接是与其有某种关系的用户和组（包括指派人和所有者）。

GET `/task/{id}/identity-links`

Query Parameters:

| Name | Description |
| :--- | :---------- |
| type | 链接的类型  |

Result：JSON数组

| Name    | Value  | Description            |
| :------ | :----- | :--------------------- |
| userId  | String | 参与此链接的用户的id。 |
| groupId | String | 参与此链接的组的id     |
| type    | String | 链接类型               |

e.g.

```json
[{
    "userId": "userId",
    "groupId": null,
    "type": "assignee"
},
{
    "userId": null,
    "groupId": "groupId1",
    "type": "candidate"
},
{
    "userId": null,
    "groupId": "groupId2",
    "type": "candidate"
}]
```



### [Identity Links Add](https://docs.camunda.org/manual/7.15/reference/rest/task/identity-links/post-identity-link/) 添加标识链接

添加一个标识链接到任务。可以用来连接任何用户或组到任务，并指定一种关系

POST `/task/{id}/identity-links`

Request Body:

| Name    | Value  | Description                      |
| :------ | :----- | :------------------------------- |
| userId  | String | 参与此链接的用户的id。（二选一） |
| groupId | String | 参与此链接的组的id（二选一）     |
| type    | String | 链接类型                         |



### [Identity Links Delete](https://docs.camunda.org/manual/7.15/reference/rest/task/identity-links/post-delete-identity-link/) 删除标识链接

POST `/task/{id}/identity-links/delete`

Request Body：

| Name    | Value  | Description                      |
| :------ | :----- | :------------------------------- |
| userId  | String | 参与此链接的用户的id。（二选一） |
| groupId | String | 参与此链接的组的id（二选一）     |
| type    | String | 链接类型                         |



### [Variables Get](https://docs.camunda.org/manual/7.15/reference/rest/task/variables/get-task-variable/) 获取任务变量

从给定任务的上下文检索变量。变量必须在任务中可见。如果它是本地任务变量或在任务的父作用域中声明，则可以从任务中看到它。

**Variables接口同时存在对应的 localVariables接口，localVariables返回任务上下文中的变量（局部变量）**

GET `/task/{id}/variables/{varName}`

Query Parameters:

| Name             | Description          |
| :--------------- | :------------------- |
| deserializeValue | 是否在服务端反序列化 |

Result:

| Name      | Value                              | Description            |
| :-------- | :--------------------------------- | :--------------------- |
| value     | String / Number / Boolean / Object | 变量值                 |
| type      | String                             | 变量类型               |
| valueInfo | Object                             | 额外的、依赖类型的参数 |



### [Variables Get (Binary)](https://docs.camunda.org/manual/7.15/reference/rest/task/variables/get-task-variable-binary/) 获取任务变量（二进制）

GET `/task/{id}/variables/{varName}/data`

Result：

如果是不带任何MIME类型信息的二进制数据或文件，返回字节流。

带MIME类型信息的文件返回保存的类型。



### [Variables Get List](https://docs.camunda.org/manual/7.15/reference/rest/task/variables/get-task-variables/) 获取任务变量列表

GET `/task/{id}/variables`

Query Parameters:

| Name             | Description          |
| :--------------- | :------------------- |
| deserializeValue | 是否在服务端反序列化 |

Result:

| Name      | Value                              | Description            |
| :-------- | :--------------------------------- | :--------------------- |
| value     | String / Number / Boolean / Object | 变量值                 |
| type      | String                             | 变量类型               |
| valueInfo | Object                             | 额外的、依赖类型的参数 |



### [Variables Modify](https://docs.camunda.org/manual/7.15/reference/rest/task/variables/post-modify-task-variables/) 修改变量

更新或删除任务中可见的变量。更新先于删除。因此，如果一个变量被更新和删除，删除会覆盖更新。

POST `/task/{id}/variables`

Request Body：

| Name          | Description      |
| :------------ | :--------------- |
| modifications | 修改的变量对象.  |
| deletions     | 待删除的变量key. |

e.g.

Request:

```json
{
  "modifications": {
    "aVariable": { "value": "aValue", "type": "String" },
    "anotherVariable": { "value": 42, "type": "Integer" }
  },
  "deletions": [
    "aThirdVariable", "FourthVariable"
  ]
}
```



### [Variables Update](https://docs.camunda.org/manual/7.15/reference/rest/task/variables/put-task-variable/) 更新变量

更新一个任务范围内可见的流程变量。如果变量不存在，将在任务可见的最顶层范围中创建该变量

PUT `/task/{id}/variables/{varName}`

Request Body:

| Name      | Description  |
| :-------- | :----------- |
| value     | 变量值       |
| type      | 变量类型     |
| valueInfo | 额外附加属性 |



### [Variables Post (Binary)](https://docs.camunda.org/manual/7.15/reference/rest/task/variables/post-task-variable-binary/) 修改变量（二进制）

设置任务可见的二进制变量的序列化值或文件变量的二进制值。

POST `/task/{id}/variables/{varName}/data`

Request Body: <Multipart-form>

| Form Part Name | Content Type             | Description                                                  |
| :------------- | :----------------------- | :----------------------------------------------------------- |
| data           | application/octet-stream | 要设置的二进制数据。对于“File”变量，此多部分可以包含要设置的文件变量的文件名、二进制值和MIME类型。只有文件名是必需的。 |
| valueType      | text/plain               | 变量类型的名称。字节数组变量的“Bytes”或文件变量的“File”。    |
| data           | application/json         | **Deprecated**.                                              |
| type           | text/plain               | **Deprecated**.                                              |



### [Variables Delete](https://docs.camunda.org/manual/7.15/reference/rest/task/variables/delete-task-variable/) 删除变量

DELETE `/task/{id}/variables/{varName}`



### [Get Task Count By Candidate Group](https://docs.camunda.org/manual/7.15/reference/rest/task/report/get-candidate-group-count/) 检索每个候选组的任务数

GET `/task/report/candidate-group-count`

Result：

| Name      | Value  | Description                                |
| :-------- | :----- | :----------------------------------------- |
| groupName | String | 候选组的名称。如果有任务没有组名，则为null |
| taskCount | Number | 任务数                                     |

e.g.

```json
[
  {
    "groupName": null,
    "taskCount": 1
  },
  {
    "groupName": "aGroupName",
    "taskCount": 2
  },
  {
    "groupName": "anotherGroupName",
    "taskCount": 3
  },
]
```

