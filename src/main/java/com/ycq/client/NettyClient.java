package com.ycq.client;

import com.ycq.handler.ClientReceiveHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;


/**
 * Description: netty客户端
 *
 * @author 杨存秋
 * @version 1.0
 * date: 2020/4/10 16:54
 * @since JDK 1.8
 */
public class NettyClient {

    private String ip;
    private int port;

    private NettyClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyClient("127.0.0.1", 8888).start();
    }

    private void start() throws InterruptedException {

        // 创建EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            // 创建Bootstrap
            Bootstrap bootstrap = new Bootstrap();

            // 将NioEventLoopGroup添加到Bootstrap, 并对Bootstrap进行配置
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)  // 使用nio的channel
                    .remoteAddress(new InetSocketAddress(ip, port))  // 指定ip, port
                    .handler(new ChannelInitializer<SocketChannel>() {  // 添加初始化类
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ClientReceiveHandler()); // 初始化类中添加自定义消息处理器
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect().sync();  // 阻塞直到连接到服务器
            System.out.println("连接到服务器成功, 服务器ip: " + ip + ", 端口: " + port);
            channelFuture.channel().closeFuture().sync();  // 阻塞直到channel关闭

        } finally {
            group.shutdownGracefully().sync();  // 释放资源
        }
    }
}
