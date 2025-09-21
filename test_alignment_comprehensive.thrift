// 全面测试对齐功能

// 1. 常量对齐测试
const i32 A = 1
const string VERY_LONG_NAME = "test"
const double PI = 3.14159
const bool FLAG = true

// 2. 结构体字段对齐测试
struct TestStruct {
    1: required i32 id,
    2: optional string name,
    3: required list<string> tags,
    4: optional map<string, i32> counts,
    5: required bool active = true
}

// 3. 服务方法对齐测试
service TestService {
    i32 getId(),
    string getName(1: i32 id),
    list<string> getTags() throws(1: Exception ex),
    bool updateItem(1: i32 id, 2: string name) throws(1: Exception ex, 2: NotFoundException nf)
}

// 4. 泛型间距测试
struct GenericTest {
    1: list<string> stringList,
    2: map<string,i32> stringToInt,
    3: set<i64> longSet
}