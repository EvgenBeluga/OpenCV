package org.example.opencv.service;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

import static java.lang.Thread.sleep;

@Service
public class CameraServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(CameraServiceImpl.class);
    private VideoCapture capture;
    private volatile boolean running = false;

    public void start(Consumer<Mat> frameHandler) {
        capture = new VideoCapture("src/main/resources/models/test002.mp4");
        if (!capture.isOpened()) {
            log.error("Не удалось открыть видеопоток!");
            return;
        }

        running = true;
        new Thread(() -> {
            while (running && capture.isOpened()) {
                Mat frame = new Mat();
                capture.read(frame);
                if (!frame.empty()) {
                    frameHandler.accept(frame); // обработка кадра
                }
                try {
                    sleep(30); // ~30 FPS
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public void stop() {
        running = false;
        if (capture.isOpened()) {
            capture.release();
        }
    }
    public Mat getFrame() {
        Mat frame = new Mat();
        if (capture.isOpened()) {
            capture.read(frame);
        }
        return frame;
    }

}

