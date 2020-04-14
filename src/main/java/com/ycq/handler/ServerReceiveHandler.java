package com.ycq.handler;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.SocketAddress;
import java.util.Scanner;

/**
 * Description: 服务端接收消息处理器
 *
 * @author 杨存秋
 * @version 1.0
 * date: 2020/4/10 15:42
 * @since JDK 1.8
 */
@ChannelHandler.Sharable
public class ServerReceiveHandler extends SimpleChannelInboundHandler<String> {


    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    // 服务器收到客户端连接时
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        SocketAddress socketAddress = channel.remoteAddress();
        // 把新channel添加到group中
        channelGroup.add(channel);
        // 通知其他channel新的客户端加入
        for (Channel oldChannel : channelGroup) {
            if (oldChannel != channel) {
                oldChannel.writeAndFlush("新用户: " + socketAddress + " 加入成功\n");
            }
        }
    }


    // 客户端断开时
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        SocketAddress socketAddress = channel.remoteAddress();
        // 在group中移除此channel
        channelGroup.remove(channel);
        // 通知所有channel客户端离开
        for (Channel oldChannel : channelGroup) {
            oldChannel.writeAndFlush("-- " + socketAddress + "-- 离开\n");
        }

    }


    // 服务器收到客户端的请求 在各个客户端进行转发
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        Channel channel = channelHandlerContext.channel();
        // 当一个客户端发来消息, 遍历group中所有channel
        for (Channel oldChannel : channelGroup) {
            // 转发消息回当前客户端, 前缀显示"自己"
            if (channel == oldChannel){
                oldChannel.writeAndFlush("自己: " + s + "\n");
            } else {
                // 转发消息到除他之外的所有channel, 前缀显示当前客户端的ip
                oldChannel.writeAndFlush(channel.remoteAddress() + ": " + s + "\n");
            }
        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + "在线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + "掉线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + "异常");
        channel.close();
    }
}
