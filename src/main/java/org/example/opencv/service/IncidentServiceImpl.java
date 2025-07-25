package org.example.opencv.service;

import org.example.opencv.entity.Incident;
import org.example.opencv.repository.IncidentRepository;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.time.LocalDateTime;

@Service
public class IncidentServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(IncidentServiceImpl.class);
    private final IncidentRepository incidentRepository;

    public IncidentServiceImpl(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Transactional
    public void saveIncident(Mat frame, String status) {
        if (frame == null || frame.empty()) {
            log.warn("Attempted to save an empty or null frame for status: {}", status);
            return;
        }

        // 1. Создаем объект инцидента
        Incident incident = new Incident();
        incident.setCreationDate(LocalDateTime.now());
        incident.setStatus(status);

        // 2. Сохраняем инцидент в базу данных, чтобы получить сгенерированный ID.
        try {
            incident = incidentRepository.save(incident);
            log.info("Incident with status '{}' created in DB with ID: {}", status, incident.getId());
        } catch (Exception e) {
            log.error("Failed to save initial incident record to database for status '{}'. Aborting.", status, e);
            return;
        }

        // 3. Готовим папку для хранения фотографий инцидентов.
        String dirPath = "incidents";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            try {
                if (dir.mkdirs()) {
                    log.info("Successfully created incident photo directory: {}", dirPath);
                } else {
                    log.error("Failed to create incident photo directory: {}. Check permissions or disk space. " +
                            "Image will not be saved.", dirPath);
                    return;
                }
            } catch (SecurityException e) {
                log.error("Security exception while creating directory: {}. Image will not be saved. {}", dirPath, e.getMessage());
                return;
            }
        }

        // 4. Формируем уникальное имя файла для фотографии инцидента.
        String fileName = "incident" + incident.getId() + System.currentTimeMillis() + ".png";
        String filePath = dirPath + File.separator + fileName;
        log.debug("Attempting to save image to: {}", filePath);

        // 5. Сохраняем фотографию (кадр Mat) на диск по сформированному пути.
        boolean photoSavedSuccessfully = false;
        try {
            photoSavedSuccessfully = Imgcodecs.imwrite(filePath, frame);

            if (photoSavedSuccessfully) {
                log.info("Incident photo for ID {} saved successfully to: {}", incident.getId(), filePath);
            } else {
                log.error("Failed to save incident photo for ID {} to {}. " +
                                "Check file path, permissions, image data validity, or disk space.",
                        incident.getId(), filePath);
            }
        } catch (CvException e) {
            log.error("OpenCV specific error occurred while saving image for incident ID {} to {}: {}",
                    incident.getId(), filePath, e.getMessage(), e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while saving image for incident ID {} to {}: {}",
                    incident.getId(), filePath, e.getMessage(), e);
        }


        // 6. Обновляем объект инцидента в базе данных, добавляя путь к сохраненной фотографии.
        // Этот шаг выполняется только в том случае, если фотография была успешно сохранена на диск.
        if (photoSavedSuccessfully) {
            incident.setImagePath(filePath); // Устанавливаем полный путь к файлу изображения в объект инцидента
            try {
                incidentRepository.save(incident);
                log.info("Incident {} updated in DB with image path: {}", incident.getId(), filePath);
            } catch (Exception e) {
                log.error("Failed to update incident {} with image path in database. " +
                        "Incident record may be incomplete (missing image path).", incident.getId(), e);
            }
        } else {
            log.warn("Image was NOT successfully saved for incident {}. ImagePath not updated in DB. " +
                    "Please check previous error logs for details on image save failure.", incident.getId());
        }
    }

}
