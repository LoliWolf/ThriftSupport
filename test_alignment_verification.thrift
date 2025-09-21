// 常量对齐测试
const i32 SHORT_CONST = 1;
const string VERY_LONG_CONSTANT_NAME = "test";
const double PI = 3.14159;

// 结构体字段对齐测试
struct TestStruct {
    1: required i32 id;
    2: optional string name;
    3: required list<string> tags;
    4: optional map<string, i32> metadata;
}

// 服务方法对齐测试
service TestService {
    void ping();
    string getName(1: i32 id) throws (1: InvalidIdException ex);
    list<TestStruct> getItems(1: i32 limit, 2: string filter) throws (1: ServiceException ex, 2: TimeoutException timeout);
}

// 泛型间距测试
typedef map<string, list<i32>> ComplexType;
typedef set<map<i32, string>> VeryComplexType;