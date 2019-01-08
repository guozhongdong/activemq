package com.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author gzd
 * @create 2018-11-01 16:11
 * @desc
 **/
public class NIODemo {
    public static void main(String[] args) throws Exception{
        //创建选择器
        Selector selector = Selector.open();
        //将通道注册到选择器上
        ServerSocketChannel serverSocketChannel =  ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);

        ServerSocket serverSocket =     serverSocketChannel.socket();
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8888);
        serverSocket.bind(address);

        while (true){
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = keys.iterator();
            while (keyIterator.hasNext()){
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()){
                    ServerSocketChannel ssChannel1 = (ServerSocketChannel) key.channel();

                    SocketChannel sChannel = ssChannel1.accept();
                    sChannel.configureBlocking(false);

                    sChannel.register(selector,SelectionKey.OP_READ);
                }else if (key.isReadable()){
                    SocketChannel sChannel = (SocketChannel) key.channel();
                    System.out.println(readDataFromSocketChannel(sChannel));
                    sChannel.close();
                }

                keyIterator.remove();
            }
        }

    }

    private static String readDataFromSocketChannel(SocketChannel sChannel) throws IOException{

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuilder data = new StringBuilder();
        while (true){

            buffer.clear();
            int n = sChannel.read(buffer);
            if (n == -1){
                break;
            }
            buffer.flip();
            int limit = buffer.limit();
            char[] dst = new char[limit];
            for (int i = 0; i < limit; i++) {
                dst[i] = (char) buffer.get(i);
            }
            data.append(dst);
            buffer.clear();

        }
        return data.toString();
    }
}
