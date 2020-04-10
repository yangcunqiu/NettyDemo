package com.ycq.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Description: 客户端接收消息处理
 *
 * @author 杨存秋
 * @version 1.0
 * date: 2020/4/10 16:55
 * @since JDK 1.8
 */
@ChannelHandler.Sharable
public class ClientReceiveHandler extends SimpleChannelInboundHandler<ByteBuf> {


    // 客户端和服务器建立起连接会调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接成功, 自己的ip:port " + ctx.channel().localAddress());
        ctx.writeAndFlush(Unpooled.copiedBuffer("小明".getBytes()));
    }

    // 客户端每次接收到服务器消息调用
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        System.out.println("接收到服务器消息: " + byteBuf.toString());
    }

    // 遇到异常时调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("出现异常, " + cause.toString());
        super.exceptionCaught(ctx, cause);
    }
}
