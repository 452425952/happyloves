package cn.happyloves.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zc
 * @date 2021/2/2 15:21
 */
@Slf4j
@ChannelHandler.Sharable //线程安全
public class ServerChannelHandler extends SimpleChannelInboundHandler<Object> {

    /**
     * 读取客户端发送过来的消息
     *
     * @param channelHandlerContext 上下文对象，含有 管道pipeline，通道channel，地址等信息
     * @param o                     就是客户端发送的数据，默认Object
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) {
        System.out.println("服务器读取线程：" + Thread.currentThread().getName());
        System.out.println("server channelHandlerContext = " + channelHandlerContext);
        final Channel channel = channelHandlerContext.channel();
        final ChannelPipeline pipeline = channelHandlerContext.pipeline();

        //将msg转成一个ByteBuf，比NIO的ByteBuffer性能更高
        ByteBuf buf = (ByteBuf) o;
        System.out.println("客户端发送的消息是：" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址：" + channelHandlerContext.channel().remoteAddress());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channel注册 ===>>> channelRegistered() -- 客户端绑定线程: ip=[{}]", ctx.channel().remoteAddress());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channel移除 ===>>> channelUnregistered() -- 客户端解除线程绑定: ip=[{}]", ctx.channel().remoteAddress());
        super.channelUnregistered(ctx);
    }

    /**
     * 出于活动状态
     *
     * @param ctx 上下文对象
     * @throws Exception 异常信息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel活跃 ===>>> channelActive() -- 客户端准备就绪, ip=[{}]", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    /**
     * 出于不活跃状态
     *
     * @param ctx 上下文对象
     * @throws Exception 异常信息
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel不活跃，断开连接 ===>>> channelInactive() -- 客户端被关闭, ip=[{}]", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("channel读完数据，读取完毕 ===>>> channelReadComplete() -- 本次读取客户端数据完毕: ip=[{}]", ctx.channel().remoteAddress());
        //它是 write + flush，将数据写入到缓存buffer，并将buffer中的数据flush进通道
        //一般讲，我们对这个发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~", CharsetUtil.UTF_8)).syncUninterruptibly();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("channel用户事件触发 ===>>> userEventTriggered() -- 在规定时间内未进行读/写，链接断开");
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel可写更改");
        super.channelWritabilityChanged(ctx);
    }

    /**
     * 处理异常，一般是关闭通道
     *
     * @param ctx   上下文对象
     * @param cause 异常
     * @throws Exception 异常信息
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("channel异常捕获，关闭了 ===>>> exceptionCaught() -- 异常处理", cause);
        //异常信息
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 此方法表示连接建立，一旦建立连接，就第一个被执行
     *
     * @param ctx 上下文对象
     * @throws Exception 异常信息
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("channel添加handler ===>>> handlerAdded() -- 增加逻辑处理器");
        super.handlerAdded(ctx);
    }

    /**
     * 表示 channel 断开连接，最后一个执行
     *
     * @param ctx 上下文对象
     * @throws Exception 异常信息
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("channel删除handler ===>>> handlerRemoved() -- 逻辑处理器被移除");
        super.handlerRemoved(ctx);
    }
}
