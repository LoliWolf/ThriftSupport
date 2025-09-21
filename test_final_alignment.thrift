// 常量对齐测试
const i32 A = 1;
const string VERY_LONG_NAME = "test";
const double PI_VALUE = 3.14159;

// 结构体字段对齐测试
struct User {
    1: required i32 id;
    2: optional string name;
    3: required list<string> tags;
    4: optional map<string, i32> metadata;
}

// 服务方法对齐测试
service UserService {
    void ping();
    string getName(1: i32 id) throws (1: InvalidIdException ex);
    list<User> getUsers(1: i32 limit, 2: string filter) throws (1: ServiceException ex, 2: TimeoutException timeout);
}