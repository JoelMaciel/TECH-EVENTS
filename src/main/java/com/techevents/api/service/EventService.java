package com.techevents.api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.techevents.api.domain.event.Event;
import com.techevents.api.domain.event.EventRequestDTO;
import com.techevents.api.repositories.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);
    @Value("${aws.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private EventRepository eventRepository;

    public Event createEvent(EventRequestDTO eventRequestDTO) {
        String imgUrl = null;

        if (eventRequestDTO.image() != null) {
            imgUrl = this.uploadImg(eventRequestDTO.image());
        }

        Event event = new Event();
        event.setTitle(eventRequestDTO.title());
        event.setDescription(eventRequestDTO.description());
        event.setEventUrl(eventRequestDTO.eventUrl());
        event.setRemote(eventRequestDTO.remote());
        event.setDate(new Date(eventRequestDTO.date()));
        event.setImgUrl(imgUrl);

        return eventRepository.save(event);
    }

    private String uploadImg(MultipartFile multipartFile) {
        String fileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        try {
            File file = this.convertMiltiparToFile(multipartFile);
            s3Client.putObject(bucketName, fileName, file);
            file.delete();
            log.info("Image to be saved {} ", file);
            return s3Client.getUrl(bucketName, fileName).toString();
        } catch (Exception e) {
            log.error("Error uploading file");
            return null;
        }
    }

    private File convertMiltiparToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(multipartFile.getBytes());
        fileOutputStream.close();
        return file;
    }
}
