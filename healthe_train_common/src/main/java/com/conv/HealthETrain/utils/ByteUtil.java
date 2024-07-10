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
    public static void readBytes(String path, Integer bufferSize, DataCallback callback, String uuid) throws IOException, ExecutionException, InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        Path filePath = Paths.get(path);
        AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(filePath, StandardOpenOption.READ);
        Future<Integer> result = asynchronousFileChannel.read(buffer, 0);
        // 获取读取结果
        int bytesRead = result.get();
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
                        log.info("文件读取成功, 关闭流");
                        return;
                    } catch (IOException e) {
                        throw new GlobalException("文件关闭异常");
                    }
                }
                // 切换到读取模式
                attachment.flip();
                byte[] data = new byte[attachment.limit()];
                attachment.get(data);
                // 清空缓冲区，为下一次读取做准备
                attachment.clear();
                callback.onData(data, uuid);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                position += result;
                // 递归调用
                asynchronousFileChannel.read(attachment, position, attachment, this);
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                log.error("文件读取失败: {}", exc.toString());
                try {
                    asynchronousFileChannel.close();
                } catch (IOException e) {
                    log.error("文件关闭异常", e);
                }
            }
        });

    }
    public static void readChunkBytes(String path,
                                      Integer bufferSize,
                                      DataCallback callback,
                                      String uuid,
                                      Long startByte) throws IOException, ExecutionException, InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        Path filePath = Paths.get(path);
        AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(filePath, StandardOpenOption.READ);
        Future<Integer> result = asynchronousFileChannel.read(buffer, 0);
        // 获取读取结果
        int bytesRead = result.get();
        buffer.clear();
        // 使用 CompletionHandler 进行异步读取
        asynchronousFileChannel.read(buffer, startByte, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if(result == -1) {
                    // 读取结束
                    try {
                        asynchronousFileChannel.close();
                        log.info("文件读取成功, 关闭流");
                        return;
                    } catch (IOException e) {
                        throw new GlobalException("文件关闭异常");
                    }
                }
                // 切换到读取模式
                attachment.flip();
                byte[] data = new byte[attachment.limit()];
                attachment.get(data);
                // 清空缓冲区
                attachment.clear();
                callback.onData(data, uuid);
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                log.error("文件读取失败: {}", exc.toString());
                try {
                    asynchronousFileChannel.close();
                } catch (IOException e) {
                    log.error("文件关闭异常", e);
                }
            }
        });

    }
    public static void jumpToIndex(String path,
                                      Integer bufferSize,
                                      DataCallback callback,
                                      String uuid,
                                      Long targetIndexByte) throws IOException, ExecutionException, InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        Path filePath = Paths.get(path);
        AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(filePath, StandardOpenOption.READ);
        Future<Integer> result = asynchronousFileChannel.read(buffer, 0);
        // 获取读取结果
        int bytesRead = result.get();
        buffer.clear();
        // 使用 CompletionHandler 进行异步读取
        asynchronousFileChannel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            private long position = 0;

            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if(result == -1 || position >= targetIndexByte) {
                    // 读取结束
                    try {
                        asynchronousFileChannel.close();
                        log.info("文件读取成功, 关闭流: {}", result+position);
                        return;
                    } catch (IOException e) {
                        throw new GlobalException("文件关闭异常");
                    }
                }
                // 切换到读取模式
                attachment.flip();
                int bytesToRead = (int) Math.min(attachment.remaining(), targetIndexByte - position);
                byte[] data = new byte[bytesToRead];
                attachment.get(data, 0, bytesToRead);
                // 清空缓冲区
                attachment.clear();
                position += bytesToRead;
                callback.onData(data, uuid);
                log.info("读取: {}", position);
                try {
                    Thread.sleep(1000);
                    // 继续读取下一个缓冲区
                    if (position <= targetIndexByte) {
                        long nextReadSize = Math.min(buffer.capacity(), targetIndexByte - position);
                        asynchronousFileChannel.read(attachment, position, attachment, this);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                log.error("文件读取失败: {}", exc.toString());
                try {
                    asynchronousFileChannel.close();
                } catch (IOException e) {
                    log.error("文件关闭异常", e);
                }
            }
        });

    }

}
