下面给出 MySQL-8.x 版「Virtual Office」项目数据库设计，包含所有核心表、索引、外键、常用约束，以及可直接在终端（mysql CLI）里一次性执行的 SQL 脚本。  
如无特殊说明，字符集统一 utf8mb4，排序规则 utf8mb4_0900_ai_ci，主键均为 BIGINT 自增，时间字段用 TIMESTAMP DEFAULT CURRENT_TIMESTAMP，软删除统一用 deleted_at（NULL 表示未删除）。

--------------------------------------------------
1. 建库 & 切库
--------------------------------------------------
```sql
CREATE DATABASE IF NOT EXISTS virtual_office
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE virtual_office;
```

--------------------------------------------------
2. 用户模块
--------------------------------------------------
```sql
-- 用户表
CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    avatar_url    VARCHAR(500),
    status        TINYINT DEFAULT 1 COMMENT '1=正常, 0=禁用',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMP NULL
) ENGINE=InnoDB;

-- 刷新令牌（登录后签发 JWT + 长刷新令牌）
CREATE TABLE refresh_tokens (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    token      VARCHAR(512) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;
```

--------------------------------------------------
3. 虚拟办公空间模块
--------------------------------------------------
```sql
-- 工作空间（公司/团队维度）
CREATE TABLE workspaces (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    created_by  BIGINT NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP NULL,
    CONSTRAINT fk_ws_creator FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB;

-- 用户-空间 成员关系
CREATE TABLE workspace_members (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    workspace_id  BIGINT NOT NULL,
    user_id       BIGINT NOT NULL,
    role          ENUM('OWNER','ADMIN','MEMBER') DEFAULT 'MEMBER',
    joined_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_ws_user (workspace_id, user_id),
    CONSTRAINT fk_wm_ws  FOREIGN KEY (workspace_id) REFERENCES workspaces(id) ON DELETE CASCADE,
    CONSTRAINT fk_wm_user FOREIGN KEY (user_id)       REFERENCES users(id)      ON DELETE CASCADE
) ENGINE=InnoDB;

-- 虚拟房间（可理解为楼层/区域）
CREATE TABLE rooms (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    workspace_id  BIGINT NOT NULL,
    name          VARCHAR(100) NOT NULL,
    bg_image_url  VARCHAR(500),
    pos_x         INT DEFAULT 0 COMMENT '画布左上角x',
    pos_y         INT DEFAULT 0 COMMENT '画布左上角y',
    width         INT DEFAULT 1600,
    height        INT DEFAULT 900,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_room_ws FOREIGN KEY (workspace_id) REFERENCES workspaces(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 用户当前位置 & 状态
CREATE TABLE user_positions (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT NOT NULL,
    room_id      BIGINT NOT NULL,
    pos_x        INT NOT NULL,
    pos_y        INT NOT NULL,
    status       VARCHAR(30) DEFAULT 'online' COMMENT 'online|coffee|meeting|busy|offline',
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_room (user_id, room_id),
    CONSTRAINT fk_up_user FOREIGN KEY (user_id) REFERENCES users(id)      ON DELETE CASCADE,
    CONSTRAINT fk_up_room FOREIGN KEY (room_id) REFERENCES rooms(id)      ON DELETE CASCADE
) ENGINE=InnoDB;
```

--------------------------------------------------
4. 聊天模块
--------------------------------------------------
```sql
-- 频道分类：PUBLIC / PRIVATE
CREATE TABLE channels (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    workspace_id BIGINT NOT NULL,
    name          VARCHAR(100) NOT NULL,
    type          ENUM('PUBLIC','PRIVATE') DEFAULT 'PUBLIC',
    created_by    BIGINT NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMP NULL,
    CONSTRAINT fk_ch_ws    FOREIGN KEY (workspace_id) REFERENCES workspaces(id) ON DELETE CASCADE,
    CONSTRAINT fk_ch_user  FOREIGN KEY (created_by)   REFERENCES users(id)
) ENGINE=InnoDB;

-- 频道成员（私聊频道需要）
CREATE TABLE channel_members (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_id BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    joined_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_ch_user (channel_id, user_id),
    CONSTRAINT fk_cm_ch  FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    CONSTRAINT fk_cm_user FOREIGN KEY (user_id)   REFERENCES users(id)     ON DELETE CASCADE
) ENGINE=InnoDB;

-- 消息
CREATE TABLE messages (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_id   BIGINT NOT NULL,
    sender_id    BIGINT NOT NULL,
    content      TEXT NOT NULL,
    msg_type     ENUM('TEXT','IMAGE','FILE','SYSTEM') DEFAULT 'TEXT',
    reply_to_id  BIGINT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at   TIMESTAMP NULL,
    CONSTRAINT fk_msg_ch   FOREIGN KEY (channel_id)  REFERENCES channels(id) ON DELETE CASCADE,
    CONSTRAINT fk_msg_user FOREIGN KEY (sender_id)   REFERENCES users(id),
    CONSTRAINT fk_msg_reply FOREIGN KEY (reply_to_id) REFERENCES messages(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 消息已读游标（谁最后读到了哪条）
CREATE TABLE message_read_cursor (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_id  BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,
    message_id  BIGINT NOT NULL,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_cursor (channel_id, user_id),
    CONSTRAINT fk_cur_ch   FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    CONSTRAINT fk_cur_user FOREIGN KEY (user_id)    REFERENCES users(id)     ON DELETE CASCADE,
    CONSTRAINT fk_cur_msg  FOREIGN KEY (message_id) REFERENCES messages(id)
) ENGINE=InnoDB;
```

--------------------------------------------------
5. 视频会议模块
--------------------------------------------------
```sql
-- 会议房间
CREATE TABLE meetings (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    workspace_id  BIGINT NOT NULL,
    title         VARCHAR(200) NOT NULL,
    room_id       BIGINT NULL COMMENT '可绑定到虚拟房间',
    created_by    BIGINT NOT NULL,
    start_time    TIMESTAMP NULL,
    end_time      TIMESTAMP NULL,
    status        ENUM('SCHEDULED','ONGOING','ENDED','CANCELLED') DEFAULT 'SCHEDULED',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMP NULL,
    CONSTRAINT fk_mt_ws    FOREIGN KEY (workspace_id) REFERENCES workspaces(id) ON DELETE CASCADE,
    CONSTRAINT fk_mt_room  FOREIGN KEY (room_id)      REFERENCES rooms(id)       ON DELETE SET NULL,
    CONSTRAINT fk_mt_user  FOREIGN KEY (created_by)   REFERENCES users(id)
) ENGINE=InnoDB;

-- 参会者
CREATE TABLE meeting_participants (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    meeting_id  BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,
    joined_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    left_at     TIMESTAMP NULL,
    CONSTRAINT fk_mp_mt   FOREIGN KEY (meeting_id) REFERENCES meetings(id) ON DELETE CASCADE,
    CONSTRAINT fk_mp_user FOREIGN KEY (user_id)    REFERENCES users(id)     ON DELETE CASCADE
) ENGINE=InnoDB;
```

--------------------------------------------------
6. 统一审计/字典等可选表
--------------------------------------------------
```sql
-- 翻译缓存（聊天消息自动翻译）
CREATE TABLE translation_cache (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_text TEXT NOT NULL,
    from_lang     VARCHAR(10),
    to_lang       VARCHAR(10),
    translated    TEXT NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_pair (MD5(original_text), from_lang, to_lang)
) ENGINE=InnoDB;
```

--------------------------------------------------
7. 常用索引（可按需再增）
--------------------------------------------------
```sql
-- 消息分页
CREATE INDEX idx_msg_ch_time ON messages(channel_id, created_at DESC);
-- 刷新令牌快速过期的扫描
CREATE INDEX idx_rt_expires ON refresh_tokens(expires_at);
-- 用户位置快速查询房间内所有人
CREATE INDEX idx_up_room ON user_positions(room_id);
```

--------------------------------------------------
8. 终端一键执行
--------------------------------------------------
把以上所有 SQL 保存为 `virtual_office.sql`，然后：

```bash
mysql -u root -p < virtual_office.sql
```

脚本会自动建库、建表、建外键与索引，即可直接供 Spring Boot 项目对接。

下面给出一段可直接在 MySQL 终端执行的「测试数据脚本」。  
已包含：

1. 1 个测试工作空间（Demo Workspace）  
2. 3 个测试用户（密码统一明文 `123456`，已用 BCrypt 哈希 `$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW`）  
3. 把 3 人全部加入该空间，并分别加入 1 个公共频道 `#general` 和 1 个私人频道 `#private`  
4. 在默认房间（Demo Room）里给每人随机摆个位置、状态  
5. 插入 1 条示例消息、1 个示例会议及参与者  

复制保存为 `seed.sql`，执行：

```bash
mysql -u root -p virtual_office < seed.sql
```

即可用下表任意邮箱 + 密码 `123456` 登录，并体验所有功能。

--------------------------------------------------
seed.sql 内容开始
--------------------------------------------------
```sql
-- 必须先切到目标库
USE virtual_office;

-- ----------- 1. 测试用户（密码=123456，BCrypt） ----------- 
INSERT INTO users (email, password_hash, username, avatar_url, status) VALUES
('alice@test.com',  '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Alice', 'https://i.pravatar.cc/150?img=1', 1),
('bob@test.com',    '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Bob',   'https://i.pravatar.cc/150?img=2', 1),
('carol@test.com',  '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Carol', 'https://i.pravatar.cc/150?img=3', 1);

-- ----------- 2. 工作空间 ----------- 
INSERT INTO workspaces (name, description, created_by) VALUES
('Demo Workspace', '测试用的默认空间', 1);

-- 把三人全部拉进去
INSERT INTO workspace_members (workspace_id, user_id, role) VALUES
(1, 1, 'OWNER'),
(1, 2, 'MEMBER'),
(1, 3, 'MEMBER');

-- ----------- 3. 默认房间 ----------- 
INSERT INTO rooms (workspace_id, name, bg_image_url, width, height) VALUES
(1, 'Demo Room', 'https://via.placeholder.com/1600x900/eee?text=Office', 1600, 900);

-- ----------- 4. 用户初始位置 & 状态 ----------- 
INSERT INTO user_positions (user_id, room_id, pos_x, pos_y, status) VALUES
(1, 1, 400, 300, 'online'),
(2, 1, 600, 300, 'coffee'),
(3, 1, 800, 450, 'meeting');

-- ----------- 5. 频道 ----------- 
-- 公共频道
INSERT INTO channels (workspace_id, name, type, created_by) VALUES
(1, 'general', 'PUBLIC', 1);
-- 私人频道（创建人 Alice）
INSERT INTO channels (workspace_id, name, type, created_by) VALUES
(1, 'private', 'PRIVATE', 1);

-- 把三人全部加入 #general
INSERT INTO channel_members (channel_id, user_id) VALUES
(1, 1), (1, 2), (1, 3);
-- 把 Alice 和 Bob 加入 #private
INSERT INTO channel_members (channel_id, user_id) VALUES
(2, 1), (2, 2);

-- ----------- 6. 示例消息 ----------- 
INSERT INTO messages (channel_id, sender_id, content, msg_type) VALUES
(1, 1, '大家好，欢迎来到 Virtual Office！', 'TEXT');

-- ----------- 7. 示例会议 ----------- 
INSERT INTO meetings (workspace_id, title, room_id, created_by, status, start_time) VALUES
(1, '项目启动会', 1, 1, 'ONGOING', NOW());

-- 参会者
INSERT INTO meeting_participants (meeting_id, user_id) VALUES
(1, 1), (1, 2), (1, 3);
```

--------------------------------------------------
登录信息速查
--------------------------------------------------
| 邮箱             | 密码   | 角色 |
|------------------|--------|------|
| alice@test.com   | 123456 | 空间所有者 |
| bob@test.com     | 123456 | 成员 |
| carol@test.com   | 123456 | 成员 |

--------------------------------------------------
Spring Boot 端提示
--------------------------------------------------
1. 上述密码哈希由 BCrypt 生成（强度 10）。  
   如果你项目里用的不是 `BCryptPasswordEncoder`，请把哈希换成对应算法。  
2. 登录接口返回 JWT 后，前端即可用 token 访问  
   `/api/workspaces/1/rooms/1/positions` 等所有业务接口。

--------------------------------------------------
API 端点文档
--------------------------------------------------

### 用户认证相关
- POST `/api/auth/register` - 用户注册
- POST `/api/auth/login` - 用户登录
- POST `/api/auth/logout` - 用户登出
- GET `/api/auth/me` - 获取当前用户信息
- GET `/api/users/{id}` - 获取用户信息
- PUT `/api/users/{id}` - 更新用户信息

### 工作空间相关
- GET `/api/workspaces` - 获取所有工作空间
- POST `/api/workspaces` - 创建工作空间
- GET `/api/workspaces/{id}` - 获取工作空间详情
- PUT `/api/workspaces/{id}` - 更新工作空间
- DELETE `/api/workspaces/{id}` - 删除工作空间

### 房间相关
- GET `/api/workspaces/{workspaceId}/rooms` - 获取工作空间下的房间
- POST `/api/workspaces/{workspaceId}/rooms` - 创建房间
- GET `/api/rooms/{id}` - 获取房间详情
- PUT `/api/rooms/{id}` - 更新房间信息
- DELETE `/api/rooms/{id}` - 删除房间

### 用户位置相关
- GET `/api/rooms/{roomId}/positions` - 获取房间内用户位置
- PUT `/api/rooms/{roomId}/positions` - 更新用户位置

### 频道相关
- GET `/api/workspaces/{workspaceId}/channels` - 获取工作空间下的频道
- POST `/api/workspaces/{workspaceId}/channels` - 创建频道
- GET `/api/channels/{id}` - 获取频道详情
- PUT `/api/channels/{id}` - 更新频道
- DELETE `/api/channels/{id}` - 删除频道

### 消息相关
- GET `/api/channels/{channelId}/messages` - 获取频道消息
- POST `/api/messages` - 发送消息
- GET `/api/messages/{id}` - 获取消息详情
- PUT `/api/messages/{id}` - 更新消息
- DELETE `/api/messages/{id}` - 删除消息

### 会议相关
- GET `/api/workspaces/{workspaceId}/meetings` - 获取工作空间下的会议
- POST `/api/workspaces/{workspaceId}/meetings` - 创建会议
- GET `/api/meetings/{id}` - 获取会议详情
- PUT `/api/meetings/{id}` - 更新会议信息
- DELETE `/api/meetings/{id}` - 删除会议
- POST `/api/meetings/{id}/join` - 加入会议
- POST `/api/meetings/{id}/leave` - 离开会议