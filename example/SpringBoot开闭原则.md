# 开闭原则、策略模式
**定义：** 一个软件实体如类、模块和函数应该对扩展开放，对修改关闭。

**问题由来：** 在软件的生命周期内，因为变化、升级和维护等原因需要对软件原有代码进行修改时，可能会给旧代码中引入错误，也可能会使我们不得不对整个功能进行重构，并且需要原有代码经过重新测试。

**解决方案：** 当软件需要变化时，尽量通过扩展软件实体的行为来实现变化，而不是通过修改已有的代码来实现变化。

**Springboot可以帮助我们简单方便的实现开闭原则**

以下示例代码：

**定义接口**
```java
/**
 * @author ZC
 * @date 2020/9/21 23:12
 */
public interface IOpenClosePrinciple {

    String TEST1 = "TEST1";
    String TEST2 = "TEST2";

    /**
     * 测试输出
     * @return 返回值
     */
    String testOut();
}
```

**实现接口,业务1，Test1**
```java
/**
 * @author ZC
 * @date 2020/9/21 23:12
 */
@Service(IOpenClosePrinciple.TEST1)
public class Test1Impl implements IOpenClosePrinciple {

    @Override
    public String testOut() {
        String key = "test1";
        System.out.println(key);

        return key;
    }
}
```
**实现接口,业务2，Test2**
```java
/**
 * @author ZC
 * @date 2020/9/21 23:12
 */
@Service(IOpenClosePrinciple.TEST2)
public class Test2Impl implements IOpenClosePrinciple {

    @Override
    public String testOut() {
        String key = "test2";
        System.out.println(key);
        return key;
    }
}
```

**Controller测试接口代码**
```java
/**
 * 如果状态增加了，只需增加相应状态的类即可，也只需测试新增加的业务，这样就简单方便的实现了开闭原则。
 *
 * @author ZC
 * @date 2020/9/21 23:15
 */
@RestController
@RequestMapping("/openClosePrinciple")
public class OpenClosePrincipleController {
    @Autowired
    Map<String, IOpenClosePrinciple> openClosePrincipleMap;

    @GetMapping("/{name}")
    public String openClosePrinciple(@PathVariable String name) {
        return openClosePrincipleMap.get(name).testOut();
    }
}
```

**输出**
```
# Test1接口：
GET http://localhost:8080/openClosePrinciple/TEST1

HTTP/1.1 200 
Content-Type: text/plain;charset=UTF-8
Content-Length: 5
Date: Mon, 21 Sep 2020 15:28:53 GMT
Keep-Alive: timeout=60
Connection: keep-alive
# Test1输出：
test1

# Test2接口：
GET http://localhost:8080/openClosePrinciple/TEST2

HTTP/1.1 200 
Content-Type: text/plain;charset=UTF-8
Content-Length: 5
Date: Mon, 21 Sep 2020 15:38:49 GMT
Keep-Alive: timeout=60
Connection: keep-alive
# Test2输出：
test2

```