package cn.happyloves.netty.rpc.examples.client.controller;

import cn.happyloves.rpc.api.Test1Api;
import cn.happyloves.rpc.client.RpcServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZC
 * @date 2021/3/3 0:04
 */
@RestController
public class ClientController {
    @RpcServer
    private Test1Api testServiceImpl;

    @GetMapping("/test1")
    public void test() {
        testServiceImpl.test();
    }

    @GetMapping("/test2")
    public void test(int id, String name) {
        testServiceImpl.test(id, name);
    }

    @GetMapping("/test3")
    public String testStr(int id) {
        return testServiceImpl.testStr(id);
    }

    @GetMapping("/test4")
    public Object testObj() {
        return testServiceImpl.testObj();
    }
}
