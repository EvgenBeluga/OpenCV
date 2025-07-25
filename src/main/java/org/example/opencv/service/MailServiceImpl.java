//package org.example.opencv.service;
//
//import jakarta.mail.*;
//import org.springframework.stereotype.Service;
//
//import jakarta.mail.internet.InternetAddress;
//import jakarta.mail.internet.MimeBodyPart;
//import jakarta.mail.internet.MimeMessage;
//import jakarta.mail.internet.MimeMultipart;
//import org.opencv.core.Mat;
//import org.opencv.core.Size;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.videoio.VideoCapture;
//import org.opencv.videoio.VideoWriter;
//import org.opencv.videoio.Videoio;
//
//import javax.swing.*;
//import java.io.*;
//import java.util.*;
//import java.util.zip.*;
//
//@Service
//public class MailServiceImpl implements MailService {
//
//    private static final String EMAIL_FROM = "som-nt@mail.ru"; // отправитель
//    private static final String EMAIL_PASSWORD = "aHHjveTaVWC6Co11lCfu"; // пароль приложения Mail.ru
//    private static final String EMAIL_TO = "kogeka@list.ru"; // получатель
//
////    @Override
////    public void sendIncidentMail(
////            EMAIL_FROM,
////            "",
////                    "") {
////
////    }
//
//    @Override
//    public void processVideoCapture() {
//        JFrame window = new JFrame("Video File Processing");
//        JLabel screen = new JLabel();
//        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        window.setVisible(true);
//
//        VideoCapture cap = new VideoCapture(0);
//        if (!cap.isOpened()) {
//            System.err.println("Не удалось открыть видеофайл!");
//            return;
//        }
//
//        int frameWidth = (int) cap.get(Videoio.CAP_PROP_FRAME_WIDTH);
//        int frameHeight = (int) cap.get(Videoio.CAP_PROP_FRAME_HEIGHT);
//        Size frameSize = new Size(frameWidth, frameHeight);
//        VideoWriter writer = new VideoWriter("testVideoWriter.mpeg", VideoWriter.fourcc('X', 'V', 'I', 'D'),
//                cap.get(Videoio.CAP_PROP_FPS), frameSize);
//
//        if (!writer.isOpened()) {
//            System.err.println("Не удалось открыть файл для записи видео!");
//            cap.release();
//            window.dispose();
//            return;
//        }
//
////        processVideoCapture(cap, writer, screen, window);
//
//        window.dispose();
//
//        // 1. Сохраняем кадры
//        List<File> frames = saveFrames(new VideoCapture(0), "frames");
//
//        // 2. Архивируем
//        File zipFile;
//        try {
//            zipFile = zipFrames(frames, "frames.zip");
//            System.out.println("архивация успешна");
//            if (zipFile.length() > 20 * 1024 * 1024) { // 20 МБ запасом
//                System.err.println("Архив слишком большой для отправки по почте!");
//                return;
//            }
//
//        } catch (Exception e) {
//            System.err.println("Ошибка при архивации: " + e.getMessage());
//            return;
//        }
//
//        // 3. Отправляем на почту
//        try {
//            sendEmailWithAttachment(
//                    EMAIL_TO,
//                    "Video Frames",
//                    "Кадры из видео во вложении.",
//                    zipFile
//            );
//            System.out.println("Письмо успешно отправлено!");
//        } catch (Exception e) {
//            System.err.println("Ошибка при отправке письма: " + e.getMessage());
//        }
//    }
//
//    // Сохранение кадров
//    public List<File> saveFrames(VideoCapture cap, String framesDir) {
//        List<File> frames = new ArrayList<>();
//        File dir = new File(framesDir);
//        if (!dir.exists() && !dir.mkdirs()) {
//            System.err.println("Не удалось создать папку: " + framesDir);
//            return frames;
//        }
//        Mat frame = new Mat();
//        int idx = 0;
//        while (cap.read(frame)) {
//            if (idx % 20 == 0) { // Сохраняем только каждый 20-й кадр
//                String filename = framesDir + "/frame_" + idx + ".jpg";
//                Imgcodecs.imwrite(filename, frame);
//                frames.add(new File(filename));
//            }
//            idx++;
//        }
//        cap.release();
//        return frames;
//    }
//    public File zipFrames(List<File> frames, String zipFileName) throws Exception {
//        File zipFile = new File(zipFileName);
//        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
//            for (File frame : frames) {
//                try (FileInputStream fis = new FileInputStream(frame)) {
//                    ZipEntry entry = new ZipEntry(frame.getName());
//                    zos.putNextEntry(entry);
//
//                    byte[] buffer = new byte[4096];
//                    int len;
//                    while ((len = fis.read(buffer)) > 0) {
//                        zos.write(buffer, 0, len);
//                    }
//                    zos.closeEntry();
//                }
//            }
//        }
//        return zipFile;
//    }
//
//    @Override
//    // Отправка письма с вложением через Mail.ru
//    public void sendEmailWithAttachment(String to, String subject, String body, File attachment) throws Exception {
//        Properties props = new Properties();
//        props.put("mail.smtp.host", "smtp.mail.ru");
//        props.put("mail.smtp.port", "465");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.ssl.enable", "true");
//        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//
//        Session session = Session.getInstance(props, new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
//            }
//        });
//
//        Message message = new MimeMessage(session);
//        message.setFrom(new InternetAddress(EMAIL_FROM));
//        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
//        message.setSubject(subject);
//
//        MimeBodyPart messageBodyPart = new MimeBodyPart();
//        messageBodyPart.setText(body);
//
//        MimeBodyPart attachmentPart = new MimeBodyPart();
//        attachmentPart.attachFile(attachment);
//
//        Multipart multipart = new MimeMultipart();
//        multipart.addBodyPart(messageBodyPart);
//        multipart.addBodyPart(attachmentPart);
//
//        message.setContent(multipart);
//
//        Transport.send(message);
//    }
//}
//
//
