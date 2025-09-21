// 测试格式化修复效果
// 这些代码在格式化前有多余的空格，格式化后应该规范化

// 常量定义测试
const i32      DEFAULT_TIMEOUT = 5000
const string       DEFAULT_NAME = "Unknown"
const double   PI = 3.14159
const bool     ENABLED = true

// 枚举类型测试
enum Status      {
    UNKNOWN = 0,
    ACTIVE = 1,
    INACTIVE = 2,
    DELETED = 3
}

// 结构体测试
struct User    {
    1: i32     id,
    2: string      name,
    3: bool    active,
    4: double     score
}

// 服务测试
service UserService     {
    User getUser(1: i32    userId),
    bool     updateUser(1: User   user),
    void   deleteUser(1: i32     userId)
}