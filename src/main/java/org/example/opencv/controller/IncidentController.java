package org.example.opencv.controller;

import jakarta.annotation.PostConstruct;
import org.example.opencv.service.CameraServiceImpl;
import org.example.opencv.service.IncidentServiceImpl;
//import org.example.opencv.service.MailServiceImpl;
import org.example.opencv.service.YoloServiceImpl;
import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/incidents")
public class IncidentController {

    private static final Logger log = LoggerFactory.getLogger(IncidentController.class);

    static {
        try {
//            nu.pattern.OpenCV.loadShared();
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            log.info("OpenCV library loaded - ДААААААА");

        } catch (UnsatisfiedLinkError e) {
//            e.printStackTrace();
            log.error("Unable to load OpenCV library -НЕЕЕЕЕЕТ", e);
        }
    }

    private final CameraServiceImpl cameraService;
    private final YoloServiceImpl yoloService;
    private final IncidentServiceImpl incidentService;
//    private final MailServiceImpl mailService;

    public IncidentController(CameraServiceImpl cameraService, YoloServiceImpl yoloService, IncidentServiceImpl incidentService) {
        this.cameraService = cameraService;
        this.yoloService = yoloService;
        this.incidentService = incidentService;
//        this.mailService = mailService;
    }

    @PostConstruct
    public void startCamera() {
        cameraService.start(frame -> {
            try {
                if (yoloService.detectPerson(frame)) {
                    // Сохраняем инцидент
                    incidentService.saveIncident(frame, "Person detected   УУУРРРРРАААААА");

//                   mailService.processVideoCapture();
                }
            } catch (Exception ex) {
                log.error("Error while starting camera", ex);
            }
        });
    }
}

