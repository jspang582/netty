/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.example.http.helloworld;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * 一个HTTP服务器，它以相当纯文本的形式发回所接收的HTTP请求的内容。
 *
 * <p>
 * An HTTP server that sends back the content of the received HTTP request
 * in a pretty plaintext form.
 */
public final class HttpHelloWorldServer {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8080"));

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建服务器端的启动对象
            ServerBootstrap b = new ServerBootstrap();
            // 使用链式编程配置参数
            b.group(bossGroup, workerGroup)
                    // 初始化服务器连接队列大小，服务端处理客户端连接请求是顺序处理的，所以统一时间只能处理一个客户端连接
                    // 多个客户端同时来的时候，服务端不能处理的客户端连接请求会被放在队列中等待处理
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    // 使用NioServerSocketChannel作为服务器通道的实现
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 创建通道初始化对象
                    .childHandler(new HttpHelloWorldServerInitializer(sslCtx));

            // 绑定端口并且同步，生成ChannelFuture异步对象，通过isDone()等方法可以判断异步事件的执行情况
            // 启动服务器并绑定端口，bind是异步操作，sync()是等待异步操作执行完毕
            ChannelFuture cf = b.bind(PORT);

            // 给ChannelFuture注册监听器
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    System.err.println("Open your web browser and navigate to " +
                            (SSL ? "https" : "http") + "://127.0.0.1:" + PORT + '/');
                }
            });

            // 等待服务端监听端口关闭，closeFuture()是异步操作
            // 通过sync()同步等待通道关闭处理完毕，这里会阻塞等待通道关闭完成，内部调用的是Object#wait()方法
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
