const i32 SHORT_CONST = 1
const string VERY_LONG_CONSTANT_NAME = "test"

struct TestStruct {
    1: i32 id
    2: string name
}

service TestService {
    void ping()
    string getName(1: i32 id)
}