package com.ycq.handler;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Description: 接收消息处理器
 *
 * @author 杨存秋
 * @version 1.0
 * date: 2020/4/10 15:42
 * @since JDK 1.8
 */
@ChannelHandler.Sharable
public class ServerReceiveHandler extends ChannelInboundHandlerAdapter {


    // 客户端连接时被调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("建立连接成功, 自己ip:port " + ctx.channel().localAddress());
        super.channelActive(ctx);
    }

    // 每次接收到客户端消息都会调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("接收到消息: " + msg);
        ByteBuf byteBuf = (ByteBuf) msg;
        ctx.write(byteBuf);  // 写数据到buff缓存区, 还没有发, 且必须是byte字节类型
    }


    // 当前批处理消息中, 最后一条消息
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("收到批处理消息中最后一条消息");
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);  // 把buff里数据刷出去, 刷出去才是真正发到客户端
    }


    // 出现异常时会调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("出现异常了: " + cause.toString());
        cause.printStackTrace();
        ctx.close();
    }
}
