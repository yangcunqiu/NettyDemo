package com.ycq.server;

import com.ycq.handler.ServerReceiveHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * Description: netty服务器
 *
 * @author 杨存秋
 * @version 1.0
 * date: 2020/4/10 15:31
 * @since JDK 1.8
 */
public class NettyServer {

    private int port;

    private NettyServer(int port){
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyServer(8888).start();
    }

    // 服务器引导方法
    private void start() throws InterruptedException {

        // 创建NioEventLoopGroup
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            // 创建ServerBootstrap
            ServerBootstrap serverbootstrap = new ServerBootstrap();

            // 将NioEventLoopGroup添加到ServerBootstrap, 并对ServerBootstrap进行配置
            serverbootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)  // 使用nio的channel
                    .localAddress(new InetSocketAddress(port))  // 指定端口
                    .childHandler(new ChannelInitializer<SocketChannel>() {  // 添加初始化类
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()))
                                    .addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(new ServerReceiveHandler());
                        }
                    });

            ChannelFuture channelFuture = serverbootstrap.bind().sync();  // 等阻塞直到服务器绑定成功
            channelFuture.channel().closeFuture().sync();  // 阻塞直到channel关闭

        } finally {
            bossGroup.shutdownGracefully().sync();  // 释放资源
            workGroup.shutdownGracefully().sync();  // 释放资源
        }
    }



}
