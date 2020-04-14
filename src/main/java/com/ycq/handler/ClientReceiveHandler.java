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
public class ClientReceiveHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println(s);
    }
}
