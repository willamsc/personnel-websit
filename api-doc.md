# API 接口文档

## 基础说明

### 接口规范
- 基础路径: `http://api.example.com/v1`
- 请求方式: REST
- 数据格式: JSON
- 时间格式: ISO 8601 (例如: "2024-01-20T08:00:00Z")
- 分页参数: page(页码，从1开始), size(每页条数)

### 响应格式
```json
{
    "code": 200,          // 状态码
    "message": "success", // 状态信息
    "data": {},          // 响应数据
    "timestamp": "",     // 响应时间戳
    "path": ""          // 请求路径
}
```

### 状态码
- 200: 成功
- 400: 请求参数错误
- 401: 未授权
- 403: 权限不足
- 404: 资源不存在
- 500: 服务器错误

## 1. 认证相关接口

### 1.1 用户注册
- 请求路径: `/auth/register`
- 请求方式: POST
- 请求参数:
```json
{
    "username": "string",    // 用户名
    "email": "string",       // 邮箱
    "password": "string",    // 密码
    "confirmPassword": "string", // 确认密码
    "verificationCode": "string" // 验证码
}
```
- 响应数据:
```json
{
    "userId": "string",
    "username": "string",
    "email": "string",
    "createTime": "string"
}
```

### 1.2 用户登录
- 请求路径: `/auth/login`
- 请求方式: POST
- 请求参数:
```json
{
    "username": "string",    // 用户名/邮箱
    "password": "string",    // 密码
    "verificationCode": "string" // 验证码（可选）
}
```
- 响应数据:
```json
{
    "token": "string",       // JWT token
    "tokenType": "Bearer",
    "expiresIn": 3600,      // 过期时间（秒）
    "userInfo": {
        "userId": "string",
        "username": "string",
        "role": "string",
        "avatar": "string"
    }
}
```

### 1.3 获取验证码
- 请求路径: `/auth/captcha`
- 请求方式: GET
- 响应数据: 图片流

### 1.4 发送邮箱验证码
- 请求路径: `/auth/email-code`
- 请求方式: POST
- 请求参数:
```json
{
    "email": "string",
    "type": "string"  // register/reset-password
}
```

### 1.5 重置密码
- 请求路径: `/auth/reset-password`
- 请求方式: POST
- 请求参数:
```json
{
    "email": "string",
    "verificationCode": "string",
    "newPassword": "string",
    "confirmPassword": "string"
}
```

## 2. 博客相关接口

### 2.1 获取博客列表
- 请求路径: `/blogs`
- 请求方式: GET
- 请求参数:
  - page: int (默认1)
  - size: int (默认10)
  - category: string (可选)
  - tag: string (可选)
  - keyword: string (可选)
  - sort: string (可选，支持"createTime"/"viewCount")
- 响应数据:
```json
{
    "total": 100,
    "pages": 10,
    "content": [{
        "id": "string",
        "title": "string",
        "summary": "string",
        "author": "string",
        "category": "string",
        "tags": ["string"],
        "viewCount": 0,
        "commentCount": 0,
        "likeCount": 0,
        "createTime": "string",
        "updateTime": "string"
    }]
}
```

### 2.2 获取博客详情
- 请求路径: `/blogs/{id}`
- 请求方式: GET
- 响应数据:
```json
{
    "id": "string",
    "title": "string",
    "content": "string",
    "author": "string",
    "category": "string",
    "tags": ["string"],
    "viewCount": 0,
    "commentCount": 0,
    "likeCount": 0,
    "createTime": "string",
    "updateTime": "string"
}
```

### 2.3 发表评论
- 请求路径: `/blogs/{blogId}/comments`
- 请求方式: POST
- 权限要求: 需要登录
- 请求参数:
```json
{
    "content": "string",
    "parentId": "string"  // 回复的评论ID（可选）
}
```
- 响应数据:
```json
{
    "id": "string",
    "content": "string",
    "author": {
        "id": "string",
        "username": "string",
        "avatar": "string"
    },
    "createTime": "string"
}
```

### 2.4 获取博客评论列表
- 请求路径: `/blogs/{blogId}/comments`
- 请求方式: GET
- 请求参数:
  - page: int (默认1)
  - size: int (默认10)
- 响应数据:
```json
{
    "total": 50,
    "pages": 5,
    "content": [{
        "id": "string",
        "content": "string",
        "author": {
            "id": "string",
            "username": "string",
            "avatar": "string"
        },
        "createTime": "string",
        "replies": [{
            "id": "string",
            "content": "string",
            "author": {
                "id": "string",
                "username": "string",
                "avatar": "string"
            },
            "createTime": "string"
        }]
    }]
}
```

### 2.5 点赞博客
- 请求路径: `/blogs/{blogId}/like`
- 请求方式: POST
- 权限要求: 需要登录
- 响应数据:
```json
{
    "liked": true,
    "likeCount": 0
}
```

### 2.6 收藏博客
- 请求路径: `/blogs/{blogId}/favorite`
- 请求方式: POST
- 权限要求: 需要登录
- 响应数据:
```json
{
    "favorited": true,
    "favoriteCount": 0
}
```

## 3. 项目展示接口

### 3.1 获取项目列表
- 请求路径: `/projects`
- 请求方式: GET
- 请求参数:
  - page: int (默认1)
  - size: int (默认10)
  - category: string (可选)
  - technology: string (可选)
- 响应数据:
```json
{
    "total": 50,
    "pages": 5,
    "content": [{
        "id": "string",
        "name": "string",
        "description": "string",
        "category": "string",
        "technologies": ["string"],
        "imageUrl": "string",
        "demoUrl": "string",
        "sourceUrl": "string",
        "createTime": "string"
    }]
}
```

### 3.2 获取项目详情
- 请求路径: `/projects/{id}`
- 请求方式: GET
- 响应数据:
```json
{
    "id": "string",
    "name": "string",
    "description": "string",
    "content": "string",
    "category": "string",
    "technologies": ["string"],
    "images": ["string"],
    "demoUrl": "string",
    "sourceUrl": "string",
    "downloads": [{
        "name": "string",
        "url": "string",
        "size": 0,
        "requireLogin": true
    }],
    "createTime": "string",
    "updateTime": "string"
}
```

### 3.3 下载项目资源
- 请求路径: `/projects/{projectId}/download/{resourceId}`
- 请求方式: GET
- 权限要求: 需要登录
- 响应数据: 文件流

### 3.4 获取项目评论
- 请求路径: `/projects/{projectId}/comments`
- 请求方式: GET
- 请求参数同博客评论

### 3.5 发表项目评论
- 请求路径: `/projects/{projectId}/comments`
- 请求方式: POST
- 请求参数同博客评论

## 4. 技术栈接口

### 4.1 获取技术栈列表
- 请求路径: `/tech-stack`
- 请求方式: GET
- 响应数据:
```json
{
    "categories": [{
        "name": "string",
        "technologies": [{
            "name": "string",
            "icon": "string",
            "proficiency": 0,
            "description": "string",
            "experience": "string"
        }]
    }]
}
```

### 4.2 获取技术栈详情
- 请求路径: `/tech-stack/{id}`
- 请求方式: GET
- 响应数据:
```json
{
    "id": "string",
    "name": "string",
    "category": "string",
    "icon": "string",
    "proficiency": 0,
    "description": "string",
    "experience": "string",
    "projects": [{
        "id": "string",
        "name": "string",
        "description": "string"
    }]
}
```

## 5. 搜索接口

### 5.1 全站搜索
- 请求路径: `/search`
- 请求方式: GET
- 请求参数:
  - keyword: string
  - type: string (可选，支持"blog"/"project"/"tech")
  - page: int (默认1)
  - size: int (默认10)
- 响应数据:
```json
{
    "total": 100,
    "pages": 10,
    "content": [{
        "id": "string",
        "type": "string",
        "title": "string",
        "summary": "string",
        "url": "string",
        "createTime": "string"
    }]
}
```

## 6. 用户中心接口

### 6.1 获取个人信息
- 请求路径: `/user/profile`
- 请求方式: GET
- 权限要求: 需要登录
- 响应数据:
```json
{
    "id": "string",
    "username": "string",
    "email": "string",
    "avatar": "string",
    "role": "string",
    "createTime": "string",
    "statistics": {
        "commentCount": 0,
        "likeCount": 0,
        "downloadCount": 0
    }
}
```

### 6.2 更新个人信息
- 请求路径: `/user/profile`
- 请求方式: PUT
- 权限要求: 需要登录
- 请求参数:
```json
{
    "username": "string",
    "avatar": "string",
    "email": "string"
}
```

### 6.3 获取收藏列表
- 请求路径: `/user/favorites`
- 请求方式: GET
- 权限要求: 需要登录
- 请求参数:
  - page: int (默认1)
  - size: int (默认10)
  - type: string (可选，支持"blog"/"project")
- 响应数据:
```json
{
    "total": 50,
    "pages": 5,
    "content": [{
        "id": "string",
        "type": "string",
        "title": "string",
        "summary": "string",
        "createTime": "string"
    }]
}
```

### 6.4 获取评论历史
- 请求路径: `/user/comments`
- 请求方式: GET
- 权限要求: 需要登录
- 请求参数:
  - page: int (默认1)
  - size: int (默认10)
- 响应数据:
```json
{
    "total": 50,
    "pages": 5,
    "content": [{
        "id": "string",
        "content": "string",
        "targetType": "string",  // blog/project
        "targetId": "string",
        "targetTitle": "string",
        "createTime": "string"
    }]
}
```

## 7. 管理员接口

### 7.1 博客管理
#### 7.1.1 创建博客
- 请求路径: `/admin/blogs`
- 请求方式: POST
- 权限要求: 管理员
- 请求参数:
```json
{
    "title": "string",
    "content": "string",
    "category": "string",
    "tags": ["string"],
    "status": "draft/published"
}
```

#### 7.1.2 更新博客
- 请求路径: `/admin/blogs/{id}`
- 请求方式: PUT
- 权限要求: 管理员
- 请求参数: 同创建博客

### 7.2 项目管理
#### 7.2.1 创建项目
- 请求路径: `/admin/projects`
- 请求方式: POST
- 权限要求: 管理员
- 请求参数:
```json
{
    "name": "string",
    "description": "string",
    "content": "string",
    "category": "string",
    "technologies": ["string"],
    "images": ["string"],
    "demoUrl": "string",
    "sourceUrl": "string",
    "downloads": [{
        "name": "string",
        "url": "string",
        "requireLogin": true
    }]
}
```

### 7.3 系统管理
#### 7.3.1 获取系统统计
- 请求路径: `/admin/statistics`
- 请求方式: GET
- 权限要求: 管理员
- 响应数据:
```json
{
    "userCount": 0,
    "blogCount": 0,
    "projectCount": 0,
    "commentCount": 0,
    "todayVisits": 0,
    "totalVisits": 0
}
```

### 7.4 文件管理
#### 7.4.1 上传文件
- 请求路径: `/admin/files/upload`
- 请求方式: POST
- 权限要求: 管理员
- 请求参数: multipart/form-data
- 响应数据:
```json
{
    "id": "string",
    "url": "string",
    "name": "string",
    "size": 0,
    "type": "string"
}
```

#### 7.4.2 删除文件
- 请求路径: `/admin/files/{id}`
- 请求方式: DELETE
- 权限要求: 管理员

### 7.5 日志管理
#### 7.5.1 获取操作日志
- 请求路径: `/admin/logs/operations`
- 请求方式: GET
- 权限要求: 管理员
- 请求参数:
  - page: int (默认1)
  - size: int (默认10)
  - startTime: string
  - endTime: string
  - username: string (可选)
  - type: string (可选)
- 响应数据:
```json
{
    "total": 100,
    "pages": 10,
    "content": [{
        "id": "string",
        "username": "string",
        "operation": "string",
        "method": "string",
        "params": "string",
        "ip": "string",
        "createTime": "string"
    }]
}
```
