const i32 SHORT_CONST = 1
const string VERY_LONG_CONSTANT_NAME = "test"
const double PI_VALUE = 3.14159

struct TestStruct {
    1: i32 id
    2: string name
    3: double value
    4: list<string> tags
    5: map<string, i32> counters
}

service TestService {
    void simpleMethod()
    string getName(1: i32 id)
    list<string> getItems(1: i32 count, 2: string filter)
    map<string, i32> getCounters() throws (1: InvalidRequest ex)
}