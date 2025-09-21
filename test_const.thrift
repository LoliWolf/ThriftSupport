// 测试 const 关键字高亮
namespace java com.example.thrift
namespace py example.thrift
namespace cpp example

// 包含其他thrift文件
// include "other.thrift"

// 常量定义 - 测试 const 关键字高亮
const     i32 DEFAULT_TIMEOUT = 5000
const           string DEFAULT_NAME = "Unknown"
const       double PI = 3.14159
const bool ENABLED = true

// 枚举类型
enum Status {
    UNKNOWN = 0, // 12
    ACTIVE = 1,// 12312
    INACTIVE = 2,
    DELETED = 3
}

enum Priority {
    LOW = 0,
    NORMAL = 1,
    HIGH = 2,
    URGENT = 3
}

// 结构体定义
struct User {
    1: required i32 id,
    2: required string name,
    3: optional string email,
    4: optional i64 createdTime,
    5: optional Status status = Status.UNKNOWN
}

struct Task {
    1: required i32 id,
    2: required string title,
    3: optional string   description,
    4: optional Priority priority = Priority.NORMAL,
    5: optional User assignee,
    6: optional list<string> tags,
    7: optional map<string, string> metadata,
    8: optional set<i32> relatedTaskIds
}

// 异常定义
exception InvalidOperation {
    1: required i32 errorCode,
    2: required string message
}

exception NotFoundException {
    1: required string resourceType,
    2: required string resourceId
}

// 服务定义
service UserService {
    // 创建用户
    User createUser(1: User user) throws (1: InvalidOperation ouch),

    // 获取用户
    User getUser(1: i32 userId) throws (1: NotFoundException notFound),

    // 更新用户
    User updateUser(1: User user) throws (1: InvalidOperation ouch, 2: NotFoundException notFound),

    // 删除用户
    bool deleteUser(1: i32 userId) throws (1: NotFoundException notFound),

    // 查询用户列表
    list<User> listUsers(1: optional i32 limit, 2: optional i32 offset),

    // 搜索用户
    list<User> searchUsers(1: string keyword)
}

service TaskService {
    // 创建任务
    Task createTask(1: Task task) throws (1: InvalidOperation ouch),

    // 获取任务
    Task getTask(1: i32 taskId) throws (1: NotFoundException notFound),

    // 更新任务
    Task updateTask(1: Task task) throws (1: InvalidOperation ouch, 2: NotFoundException notFound),

    // 删除任务
    bool deleteTask(1: i32 taskId) throws (1: NotFoundException notFound),

    // 分配任务
    Task assignTask(1: i32 taskId, 2: i32 userId) throws (1: InvalidOperation ouch, 2: NotFoundException notFound),

    // 查询任务列表
    list<Task> listTasks(1: optional i32 limit, 2: optional i32 offset),

    // 根据状态查询任务
    list<Task> listTasksByStatus(1: Status status)
}

// 继承服务示例
service AdminUserService extends UserService {
    // 管理员功能：禁用用户
    bool disableUser(1: i32 userId) throws (1: NotFoundException notFound, 2: InvalidOperation ouch),

    // 管理员功能：启用用户
    bool enableUser(1: i32 userId) throws (1: NotFoundException notFound, 2: InvalidOperation ouch)
}

// 测试 void 关键字
service VoidTestService {
    void doSomething(1: string param)
}