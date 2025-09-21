const i32 SHORT_NAME = 1;
const i32 VERY_LONG_CONSTANT_NAME = 2;
const string ANOTHER_CONST = "value";

struct TestStruct {
1: i32 id,
2: string name,
3: optional double value
}

service TestService {
void method1(),
string method2(1: i32 param),
list<string> veryLongMethodName(1: string param1, 2: i32 param2)
}