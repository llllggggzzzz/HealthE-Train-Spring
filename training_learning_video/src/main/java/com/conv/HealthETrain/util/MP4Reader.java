package com.conv.HealthETrain.util;

import lombok.SneakyThrows;
import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.MovieBox;
import org.mp4parser.boxes.iso14496.part12.MovieFragmentBox;
import org.mp4parser.boxes.iso14496.part12.MovieHeaderBox;
import org.mp4parser.boxes.iso14496.part12.TrackBox;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.List;

public class MP4Reader {
    @SneakyThrows
    public static void main(String[] args) {
        try{
        String filePath = "/home/john/Desktop/test.mp4";

        FileInputStream fis = new FileInputStream(filePath);
        IsoFile isoFile = new IsoFile(fis.getChannel());

        // Read moov box
        MovieBox moov = isoFile.getBoxes(MovieBox.class).get(0);
        if (moov != null) {
            System.out.println("Found moov box:"+ moov);
        } else {
            System.out.println("moov box not found");
        }

        // Read moof boxes
        for (MovieFragmentBox moof : isoFile.getBoxes(MovieFragmentBox.class)) {
            System.out.println("Found moof box");
        }

        fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public static byte[] createEmptyMoovWithDefaultBaseMoof(String inputFilePath) {
        try (FileChannel fc = FileChannel.open(Paths.get(inputFilePath))) {
            IsoFile isoFile = new IsoFile(fc);

            // 创建空的 moov atom
            MovieBox moov = new MovieBox();
            MovieHeaderBox mvhd = new MovieHeaderBox();
            moov.addBox(mvhd);

            // 设置默认的 base_moof
            List<TrackBox> trackBoxes = isoFile.getMovieBox().getBoxes(TrackBox.class);
            for (TrackBox trackBox : trackBoxes) {
                trackBox.getTrackHeaderBox();
            }

            // 将 moov atom 添加到文件中
            isoFile.getBoxes().add(moov);

            // 将 moov 和 base_moof 写入 ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            moov.getBox(Channels.newChannel(baos));

            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
