package org.example.opencv.service;

import ai.onnxruntime.*;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.nio.FloatBuffer;
import java.util.Collections;

@Service
public class YoloServiceImpl {

    private final OrtEnvironment env;
    private final OrtSession session;

    public YoloServiceImpl() throws OrtException {
        env = OrtEnvironment.getEnvironment();
        session = env.createSession("src/main/resources/models/yolov10n.onnx", new OrtSession.SessionOptions());
    }

    // Преобразование Mat -> float[] -> инференс
    public boolean detectPerson(Mat frame) throws OrtException {
        float[] inputTensor = preprocess(frame);

        // Допиши размеры входа под твою модель
        long[] shape = new long[]{1, 3, 640, 640};
        OnnxTensor input = OnnxTensor.createTensor(env, FloatBuffer.wrap(inputTensor), shape);

        OrtSession.Result result = session.run(Collections.singletonMap("images", input));
        // Разбор результата (примерно)
        float[][][] output = (float[][][]) result.get(0).getValue();

        // Здесь логика детекции человека (по метке класса)
        for (float[] det : output[0]) {
            int classId = (int) det[5];
            if (classId == 0 && det[4] > 0.5) { // класс 0 — человек, порог вероятности
                return true;
            }
        }
        return false;
    }

    private float[] preprocess(Mat frame) {
        // 1. Изменить размер до 640x640
        Mat resized = new Mat();
        Imgproc.resize(frame, resized, new Size(640, 640));

        // 2. BGR -> RGB
        Mat rgb = new Mat();
        Imgproc.cvtColor(resized, rgb, Imgproc.COLOR_BGR2RGB);

        // 3. Преобразовать в float32 и нормализовать (делим на 255)
        rgb.convertTo(rgb, CvType.CV_32FC3, 1.0 / 255.0);

        // 4. Получаем данные массива (HWC)
        int width = rgb.cols();
        int height = rgb.rows();
        int channels = rgb.channels();
        float[] hwc = new float[channels * width * height];
        rgb.get(0, 0, hwc);

        // 5. Меняем порядок HWC -> CHW
        float[] chw = new float[channels * width * height];
        int channelSize = width * height;
        for (int c = 0; c < channels; c++) {
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    int hwcIndex = h * width * channels + w * channels + c;
                    int chwIndex = c * channelSize + h * width + w;
                    chw[chwIndex] = hwc[hwcIndex];
                }
            }
        }

        return chw;
    }


}
