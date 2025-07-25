package org.example.opencv.service;

import java.io.File;

public interface MailService {

    void processVideoCapture();

    void sendEmailWithAttachment(String to, String subject, String body, File attachment) throws Exception;

}
