package cn.happyloves.rpc.client;

import cn.happyloves.rpc.config.ClientBeanConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

/**
 * @author ZC
 * @date 2021/3/1 23:48
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({ClientBeanConfig.class})
public @interface EnableNettyClient {
}
