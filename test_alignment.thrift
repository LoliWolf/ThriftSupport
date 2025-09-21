// 测试常量对齐
const i32 SHORT_NAME = 1
const string VERY_LONG_CONSTANT_NAME = "test"
const double PI = 3.14

// 测试结构体字段对齐
struct TestStruct {
    1: required i32 id,
    2: required string name,
    3: optional list<string> tags,
    4: optional map<string, string> metadata
}

// 测试服务方法对齐
service TestService {
    i32 shortMethod(),
    string veryLongMethodName(),
    list<string> getList() throws(1: Exception ex),
    bool deleteItem(1: i32 id) throws(1: Exception ex, 2: NotFoundException nf)
}