package com.ll.gramgram.boundedContext.notification.controller;

import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.controller.LikeablePersonController;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import com.ll.gramgram.boundedContext.notification.service.NotificationService;
import com.ll.gramgram.standard.util.Ut;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class NotificationControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private Rq rq;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LikeablePersonService likeablePersonService;
    @Test
    @DisplayName("안내사항을 확인했을 때 readDate갱신")
    @WithUserDetails("user4")
    void t001() throws Exception {

        
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/notification/list")
                        .with(csrf()) // CSRF 키 생성
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(NotificationController.class))
                .andExpect(handler().methodName("showList"))
                .andExpect(status().is2xxSuccessful());

        Member user = memberService.findByUsername("user4").orElse(null);
        InstaMember instaMember = user.getInstaMember();

        List<Notification> notifications = notificationService.findByToInstaMember(instaMember);

        /*
         안내사항 리스트를 확인하는 순간 나에 대한 안내사항들은 전부 readDate가 갱신됨
         */
        notifications.stream()
                .forEach(notification -> Assertions.assertThat(notification.getReadDate()).isNotNull());

    }
    @Test
    @DisplayName("호감표시를 했을 때 Notification등록")
    @WithUserDetails("user4")
    void t002() throws Exception {


        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "insta_user2")
                        .param("attractiveTypeCode", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is3xxRedirection());

        Member user = memberService.findByUsername("user2").orElse(null);
        InstaMember instaMember = user.getInstaMember();

        List<Notification> notifications = notificationService.findByToInstaMember(instaMember);

        Assertions.assertThat(notifications.size()).isEqualTo(1);

    }
    @Test
    @DisplayName("호감을 변경했을 때 Notification등록")
    @WithUserDetails("user2")
    void t003() throws Exception {


        // coolTime갱신
        LikeablePerson likeablePerson = likeablePersonService.findById(1L).orElse(null);
        Ut.reflection.setFieldValue(likeablePerson, "modifyUnlockDate", LocalDateTime.now().plusSeconds(-1));

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/likeablePerson/modify/1")
                        .with(csrf()) // CSRF 키 생성
                        .param("attractiveTypeCode", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().is3xxRedirection());

        Member user = memberService.findByUsername("user3").orElse(null);
        InstaMember instaMember = user.getInstaMember();

        List<Notification> notifications = notificationService.findByToInstaMember(instaMember);
        Assertions.assertThat(notifications.size()).isEqualTo(2);
        // notPr

    }

}