package com.conv.HealthETrain.utils;

import com.conv.HealthETrain.callback.DataCallback;
import com.conv.HealthETrain.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


@Slf4j
public class ByteUtil {
    public static void readBytes(String path, Integer bufferSize, DataCallback callback) throws IOException, ExecutionException, InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        Path filePath = Paths.get(path);
        AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(filePath, StandardOpenOption.READ);
        Future<Integer> result = asynchronousFileChannel.read(buffer, 0);
        // 获取读取结果
        int bytesRead = result.get();
        System.out.println("Bytes read: " + bytesRead);
        buffer.clear();
        // 使用 CompletionHandler 进行异步读取
        asynchronousFileChannel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            private long position = 0;

            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if(result == -1) {
                    // 读取结束
                    try {
                        asynchronousFileChannel.close();
                    } catch (IOException e) {
                        throw new GlobalException("文件关闭异常");
                    }
                }
                log.info("Bytes read with CompletionHandler: {}", result);
                // 切换到读取模式
                attachment.flip();
                byte[] data = new byte[attachment.limit()];
                attachment.get(data);
                System.out.println("Data read: " + new String(data));
                // 清空缓冲区，为下一次读取做准备
                attachment.clear();
                callback.onData(data);
                position += result;
                // 递归调用
                asynchronousFileChannel.read(attachment, position, attachment, this);
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                exc.printStackTrace();
            }
        });

    }

}
