package com.ycq.client;

import com.ycq.handler.ClientReceiveHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.Scanner;


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
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline()
                                    .addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()))
                                    .addLast(new StringEncoder())
                                    .addLast(new StringDecoder())
                                    .addLast(new ClientReceiveHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect().sync();  // 阻塞直到连接到服务器
            Channel channel = channelFuture.channel();

            Scanner scanner = new Scanner(System.in);

            while(scanner.hasNextLine()){
                String nextLine = scanner.nextLine();
                channel.writeAndFlush(nextLine + "\r\n");
            }


        } finally {
            group.shutdownGracefully().sync();  // 释放资源
        }
    }
}
