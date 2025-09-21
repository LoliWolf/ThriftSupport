// 测试格式化问题的文件
namespace java com.example.test
namespace py example.test

// 常量定义 - 测试格式化
const i32 TIMEOUT=5000
const string NAME="Test"
const double PI=3.14159
const bool FLAG=true

// 枚举类型 - 测试格式化
enum Status{
UNKNOWN=0,
ACTIVE=1,
INACTIVE=2
}

// 结构体定义 - 测试格式化
struct User{
1:required i32 id,
2:required string name,
3:optional string email,
4:optional i64 timestamp
}

struct Product{
1:required i32 id,
2:required string title,
3:optional double price,
4:optional list<string>tags,
5:optional map<string,string>attributes,
6:optional set<i32>categoryIds
}

// 异常定义 - 测试格式化
exception ValidationError{
1:required string field,
2:required string message
}

// 服务定义 - 测试格式化
service UserService{
User getUser(1:i32 userId)throws(1:ValidationError error),
list<User>listUsers(1:optional i32 limit,2:optional i32 offset),
bool deleteUser(1:i32 userId)throws(1:ValidationError error),
void updateUser(1:User user)throws(1:ValidationError error)
}

// 继承服务 - 测试格式化
service AdminService extends UserService{
bool banUser(1:i32 userId)throws(1:ValidationError error),
void resetPassword(1:i32 userId)throws(1:ValidationError error)
}

// 复杂类型定义 - 测试格式化
typedef map<string,list<i32>>StringToIntListMap
typedef set<map<string,string>>SetOfStringMaps

// 复杂结构体 - 测试格式化
struct ComplexStruct{
1:required map<string,list<User>>userGroups,
2:optional set<Product>products,
3:required list<map<string,string>>metadata,
4:optional StringToIntListMap complexField
}