# Virtual Office API 测试教程

## 目录
1. [Hoppscotch 介绍与安装](#hoppscotch-介绍与安装)
2. [环境配置](#环境配置)
3. [认证流程测试](#认证流程测试)
4. [工作空间接口测试](#工作空间接口测试)
5. [房间接口测试](#房间接口测试)
6. [用户位置接口测试](#用户位置接口测试)
7. [频道接口测试](#频道接口测试)
8. [消息接口测试](#消息接口测试)
9. [会议接口测试](#会议接口测试)
10. [完整测试流程](#完整测试流程)

---

## Hoppscotch 介绍与安装

### 什么是 Hoppscotch？
Hoppscotch 是一个免费的开源 API 开发平台，提供直观的界面来测试 RESTful API。

### 安装方法
1. **在线版本**: 访问 https://hoppscotch.io
2. **浏览器扩展**: 在 Chrome 网上应用店搜索 "Hoppscotch"
3. **桌面应用**: 从 https://github.com/hoppscotch/hoppscotch/releases 下载
4. **移动应用**: App Store 或 Google Play 搜索 "Hoppscotch"

---

## 环境配置

### 1. 创建新环境
1. 打开 Hoppscotch
2. 点击左侧 "Environments" 面板
3. 点击 "+" 创建新环境，命名为 "Virtual Office Dev"

### 2. 配置环境变量
在 "Virtual Office Dev" 环境中添加以下变量：

```json
{
  "baseURL": "http://localhost:8080/api",
  "token": "",
  "userId": "",
  "workspaceId": "",
  "roomId": "",
  "channelId": ""
}
```

### 3. 选择环境
点击右上角环境选择器，选择 "Virtual Office Dev"

---

## 认证流程测试

### 1. 用户注册测试

**接口信息**
- 方法: `POST`
- URL: `{{baseURL}}/auth/register`
- Content-Type: `application/json`

**请求体:**
```json
{
  "email": "testuser@example.com",
  "password": "password123",
  "username": "testuser"
}
```

**预期响应:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "testuser@example.com",
    "username": "testuser",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "message": "注册成功"
}
```

### 2. 用户登录测试

**接口信息**
- 方法: `POST`
- URL: `{{baseURL}}/auth/login`
- Content-Type: `application/json`

**请求体:**
```json
{
  "email": "testuser@example.com",
  "password": "password123"
}
```

**操作步骤:**
1. 发送注册请求获取token
2. 在环境变量中更新 `token` 值
3. 验证登录响应中的token格式

**预期响应:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "testuser@example.com",
    "username": "testuser",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "message": "登录成功"
}
```

### 3. 获取当前用户信息测试

**接口信息**
- 方法: `GET`
- URL: `{{baseURL}}/auth/me`
- Headers: `Authorization: Bearer {{token}}`

**预期响应:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "testuser@example.com",
    "username": "testuser",
    "avatar_url": "https://i.pravatar.cc/150?img=1"
  }
}
```

---

## 工作空间接口测试

### 1. 获取所有工作空间

**接口信息**
- 方法: `GET`
- URL: `{{baseURL}}/workspaces`
- Headers: `Authorization: Bearer {{token}}`

**操作步骤:**
1. 登录获取token
2. 发送请求
3. 记录返回的workspaceId

**预期响应:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "Demo Workspace",
      "description": "测试用的默认空间",
      "created_by": 1,
      "created_at": "2024-01-01T10:00:00.000Z"
    }
  ]
}
```

### 2. 创建工作空间

**接口信息**
- 方法: `POST`
- URL: `{{baseURL}}/workspaces`
- Headers: `Authorization: Bearer {{token}}`
- Content-Type: `application/json`

**请求体:**
```json
{
  "name": "我的团队空间",
  "description": "用于团队协作的工作空间"
}
```

**操作步骤:**
1. 更新环境变量 `workspaceId` 为创建的工作空间ID
2. 验证创建成功

**预期响应:**
```json
{
  "success": true,
  "data": {
    "id": 2,
    "name": "我的团队空间",
    "description": "用于团队协作的工作空间",
    "created_by": 1,
    "created_at": "2024-01-01T11:00:00.000Z"
  },
  "message": "工作空间创建成功"
}
```

### 3. 获取工作空间详情

**接口信息**
- 方法: `GET`
- URL: `{{baseURL}}/workspaces/{{workspaceId}}`
- Headers: `Authorization: Bearer {{token}}`

---

## 房间接口测试

### 1. 获取工作空间下的房间

**接口信息**
- 方法: `GET`
- URL: `{{baseURL}}/workspaces/{{workspaceId}}/rooms`
- Headers: `Authorization: Bearer {{token}}`

**操作步骤:**
1. 使用已有的workspaceId（默认为1）
2. 发送请求
3. 记录返回的roomId

**预期响应:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "workspace_id": 1,
      "name": "Demo Room",
      "bg_image_url": "https://via.placeholder.com/1600x900/eee?text=Office",
      "pos_x": 0,
      "pos_y": 0,
      "width": 1600,
      "height": 900,
      "created_at": "2024-01-01T10:00:00.000Z"
    }
  ]
}
```

### 2. 创建房间

**接口信息**
- 方法: `POST`
- URL: `{{baseURL}}/workspaces/{{workspaceId}}/rooms`
- Headers: `Authorization: Bearer {{token}}`
- Content-Type: `application/json`

**请求体:**
```json
{
  "name": "会议室A",
  "bg_image_url": "https://via.placeholder.com/1600x900/blue?text=Room+A",
  "width": 1200,
  "height": 800
}
```

**操作步骤:**
1. 更新环境变量 `roomId` 为新创建的房间ID
2. 验证房间创建成功

---

## 用户位置接口测试

### 1. 获取房间内用户位置

**接口信息**
- 方法: `GET`
- URL: `{{baseURL}}/rooms/{{roomId}}/positions`
- Headers: `Authorization: Bearer {{token}}`

**预期响应:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "user_id": 1,
      "room_id": 1,
      "pos_x": 400,
      "pos_y": 300,
      "status": "online",
      "updated_at": "2024-01-01T12:00:00.000Z",
      "username": "testuser",
      "avatar_url": "https://i.pravatar.cc/150?img=1"
    }
  ]
}
```

### 2. 更新用户位置

**接口信息**
- 方法: `PUT`
- URL: `{{baseURL}}/rooms/{{roomId}}/positions`
- Headers: `Authorization: Bearer {{token}}`
- Content-Type: `application/json`

**请求体:**
```json
{
  "pos_x": 500,
  "pos_y": 400,
  "status": "meeting"
}
```

**预期响应:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "user_id": 1,
    "room_id": 1,
    "pos_x": 500,
    "pos_y": 400,
    "status": "meeting",
    "updated_at": "2024-01-01T12:30:00.000Z"
  },
  "message": "位置更新成功"
}
```

---

## 频道接口测试

### 1. 获取工作空间下的频道

**接口信息**
- 方法: `GET`
- URL: `{{baseURL}}/workspaces/{{workspaceId}}/channels`
- Headers: `Authorization: Bearer {{token}}`

**操作步骤:**
1. 使用workspaceId = 1（数据库中的测试数据）
2. 发送请求
3. 记录返回的channelId

**预期响应:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "workspace_id": 1,
      "name": "general",
      "type": "PUBLIC",
      "created_by": 1,
      "created_at": "2024-01-01T10:00:00.000Z"
    },
    {
      "id": 2,
      "workspace_id": 1,
      "name": "private",
      "type": "PRIVATE",
      "created_by": 1,
      "created_at": "2024-01-01T10:00:00.000Z"
    }
  ]
}
```

### 2. 创建频道

**接口信息**
- 方法: `POST`
- URL: `{{baseURL}}/workspaces/{{workspaceId}}/channels`
- Headers: `Authorization: Bearer {{token}}`
- Content-Type: `application/json`

**请求体:**
```json
{
  "name": "项目讨论",
  "type": "PUBLIC"
}
```

**操作步骤:**
1. 更新环境变量 `channelId` 为新创建的频道ID
2. 验证频道创建成功

---

## 消息接口测试

### 1. 获取频道消息

**接口信息**
- 方法: `GET`
- URL: `{{baseURL}}/channels/{{channelId}}/messages`
- Headers: `Authorization: Bearer {{token}}`

**操作步骤:**
1. 使用channelId = 1（general频道）
2. 发送请求

**预期响应:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "channel_id": 1,
      "sender_id": 1,
      "content": "大家好，欢迎来到 Virtual Office！",
      "msg_type": "TEXT",
      "created_at": "2024-01-01T10:00:00.000Z",
      "sender_username": "Alice",
      "sender_avatar_url": "https://i.pravatar.cc/150?img=1"
    }
  ]
}
```

### 2. 发送消息

**接口信息**
- 方法: `POST`
- URL: `{{baseURL}}/messages`
- Headers: `Authorization: Bearer {{token}}`
- Content-Type: `application/json`

**请求体:**
```json
{
  "content": "这是一条测试消息",
  "msg_type": "TEXT",
  "channel_id": 1
}
```

**预期响应:**
```json
{
  "success": true,
  "data": {
    "id": 2,
    "channel_id": 1,
    "sender_id": 1,
    "content": "这是一条测试消息",
    "msg_type": "TEXT",
    "created_at": "2024-01-01T13:00:00.000Z",
    "sender_username": "testuser",
    "sender_avatar_url": "https://i.pravatar.cc/150?img=1"
  },
  "message": "消息发送成功"
}
```

---

## 会议接口测试

### 1. 获取工作空间下的会议

**接口信息**
- 方法: `GET`
- URL: `{{baseURL}}/workspaces/{{workspaceId}}/meetings`
- Headers: `Authorization: Bearer {{token}}`

**预期响应:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "workspace_id": 1,
      "title": "项目启动会",
      "room_id": 1,
      "created_by": 1,
      "start_time": "2024-01-01T14:00:00.000Z",
      "status": "ONGOING",
      "created_at": "2024-01-01T10:00:00.000Z"
    }
  ]
}
```

### 2. 创建会议

**接口信息**
- 方法: `POST`
- URL: `{{baseURL}}/workspaces/{{workspaceId}}/meetings`
- Headers: `Authorization: Bearer {{token}}`
- Content-Type: `application/json`

**请求体:**
```json
{
  "title": "周例会",
  "room_id": 1,
  "start_time": "2024-01-02T10:00:00.000Z"
}
```

**预期响应:**
```json
{
  "success": true,
  "data": {
    "id": 2,
    "workspace_id": 1,
    "title": "周例会",
    "room_id": 1,
    "created_by": 1,
    "start_time": "2024-01-02T10:00:00.000Z",
    "status": "SCHEDULED",
    "created_at": "2024-01-01T14:00:00.000Z"
  },
  "message": "会议创建成功"
}
```

### 3. 加入会议

**接口信息**
- 方法: `POST`
- URL: `{{baseURL}}/meetings/{{meetingId}}/join`
- Headers: `Authorization: Bearer {{token}}`

**预期响应:**
```json
{
  "success": true,
  "message": "成功加入会议"
}
```

---

## 完整测试流程

### 阶段一：基础认证测试
1. ✅ 用户注册
2. ✅ 用户登录
3. ✅ 获取当前用户信息

### 阶段二：工作空间和房间测试
1. ✅ 获取工作空间列表
2. ✅ 创建新工作空间
3. ✅ 获取房间列表
4. ✅ 创建新房间

### 阶段三：位置和状态测试
1. ✅ 获取房间用户位置
2. ✅ 更新用户位置和状态

### 阶段四：频道和消息测试
1. ✅ 获取频道列表
2. ✅ 创建新频道
3. ✅ 获取频道消息
4. ✅ 发送新消息

### 阶段五：会议功能测试
1. ✅ 获取会议列表
2. ✅ 创建新会议
3. ✅ 加入会议

## 测试验证要点

### 1. 认证验证
- 所有需要认证的接口都应返回401未授权（当token无效时）
- token应该正确包含在Authorization header中

### 2. 数据一致性验证
- 创建操作应该返回完整的数据对象
- 更新操作应该返回更新后的数据
- 删除操作应该返回成功状态

### 3. 错误处理验证
- 尝试访问不存在的资源
- 尝试创建重复的数据
- 验证输入数据的验证规则

### 4. 性能验证
- 响应时间应该在合理范围内
- 大量数据时的分页功能
- 并发请求的处理

## 常用测试场景

### 场景一：完整用户流程
1. 注册新用户
2. 登录获取token
3. 创建工作空间和房间
4. 更新用户位置
5. 创建频道和发送消息
6. 创建和加入会议

### 场景二：多用户协作
1. 创建多个测试账号
2. 添加用户到工作空间
3. 在同一频道中发送消息
4. 查看其他用户的位置更新

### 场景三：边界条件测试
1. 发送空消息
2. 创建同名频道
3. 更新不存在的用户位置
4. 访问未授权的资源

## 环境变量管理

### 开发环境变量
```json
{
  "baseURL": "http://localhost:8080/api",
  "token": "",
  "userId": "",
  "workspaceId": "",
  "roomId": "",
  "channelId": ""
}
```

### 生产环境变量
```json
{
  "baseURL": "https://api.yourdomain.com/api",
  "token": "",
  "userId": "",
  "workspaceId": "",
  "roomId": "",
  "channelId": ""
}
```

## 故障排除

### 常见问题及解决方案

1. **401 Unauthorized**
   - 检查token是否正确设置
   - 确认token未过期
   - 验证Authorization header格式

2. **404 Not Found**
   - 检查URL路径是否正确
   - 确认资源ID是否存在
   - 验证HTTP方法是否正确

3. **500 Internal Server Error**
   - 检查数据库连接
   - 查看后端日志
   - 验证请求体格式

4. **CORS 错误**
   - 检查后端CORS配置
   - 确认前端请求头设置
   - 验证域名白名单

---

## 总结

本教程提供了完整的Virtual Office API测试指南，涵盖了所有核心功能模块。通过Hoppscotch的直观界面，您可以快速验证API的功能性和稳定性。

记住：
- 始终使用环境变量管理不同的配置
- 按照逻辑顺序测试各个接口
- 验证成功和错误场景
- 保持测试数据的一致性

Happy Testing! 🚀