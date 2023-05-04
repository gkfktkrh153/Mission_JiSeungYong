package com.ll.gramgram.boundedContext.notification.service;

import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import com.ll.gramgram.boundedContext.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<Notification> findByToInstaMemberAndReadDateIsNull(InstaMember toInstaMember) {
        return notificationRepository.findByToInstaMemberAndReadDateIsNull(toInstaMember);
    }
    @Transactional
    public void checkNotification(List<Notification> notifications){
        notifications.stream()
                .forEach(notification -> notification.check());


    }
}