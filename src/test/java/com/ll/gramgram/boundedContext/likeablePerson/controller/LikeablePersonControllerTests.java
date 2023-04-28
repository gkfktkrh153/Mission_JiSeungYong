package com.ll.gramgram.boundedContext.likeablePerson.controller;


import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import com.ll.gramgram.standard.util.Ut;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class LikeablePersonControllerTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private LikeablePersonRepository likeablePersonRepository;

    @Autowired
    private LikeablePersonService likeablePersonService;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("등록 폼(인스타 인증을 안해서 폼 대신 메세지)")
    @WithUserDetails("user1")
    void t001() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/likeablePerson/like"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showLike"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("등록 폼")
    @WithUserDetails("user2")
    void t002() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/likeablePerson/like"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showLike"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("""
                        <input type="text" name="username"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="radio" name="attractiveTypeCode" value="1"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="radio" name="attractiveTypeCode" value="2"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="radio" name="attractiveTypeCode" value="3"
                        """.stripIndent().trim())));

    }


    @Test
    @DisplayName("등록 폼 처리(user2가 abcd에게 호감표시(외모), abcd는 아직 우리 서비스에 가입하지 않은상태)")
    @WithUserDetails("user2")
    void t004() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "abcd")
                        .param("attractiveTypeCode", "2")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is3xxRedirection());

    }

    @Test
    @DisplayName("호감목록")
    @WithUserDetails("user3")
    void t005() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/likeablePerson/list"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showList"))
                .andExpect(status().is2xxSuccessful());
    }
    @Test
    @DisplayName("호감 상대 삭제 처리(user2 -> instaMember3)")
    @WithUserDetails("user2")

    void t006() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(delete("/usr/likeablePerson/1")
                        .with(csrf()) // CSRF 키 생성
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("cancel"))
                .andExpect(status().is3xxRedirection());

        LikeablePerson likeablePerson = likeablePersonRepository.findById(1L).orElse(null);

        Assertions.assertThat(likeablePerson).isNull();
    }
    @Test
    @DisplayName("권한 없는 LikeablePerson 제거")
    @WithUserDetails("user3")
    void t007() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(delete("/usr/likeablePerson/1")
                        .with(csrf()) // CSRF 키 생성
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("cancel"))
                .andExpect(status().is4xxClientError());

        LikeablePerson likeablePerson = likeablePersonRepository.findById(1L).orElse(null);

        Assertions.assertThat(likeablePerson).isNotNull(); // 삭제가 안되어야 정상흐름
    }
    @Test
    @DisplayName("같은 사유로 호감 표시(user2 -> user3)")
    @WithUserDetails("user2")
    void t008() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "insta_user3")
                        .param("attractiveTypeCode", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is4xxClientError());

        LikeablePerson likeablePerson = likeablePersonRepository.findById(1L).orElse(null);

        Assertions.assertThat(likeablePerson.getAttractiveTypeCode()).isEqualTo(1); // 사유변경 X
    }
    @Test
    @DisplayName("다른 사유로 호감 표시(user2 -> user3)")
    @WithUserDetails("user2")
    void t009() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "insta_user3")
                        .param("attractiveTypeCode", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is3xxRedirection());

        LikeablePerson likeablePerson = likeablePersonRepository.findById(1L).orElse(null);

        Assertions.assertThat(likeablePerson.getAttractiveTypeCode()).isEqualTo(3); // 사유변경
    }
    @Test
    @DisplayName("10명 이상에게 호감표시")
    @WithUserDetails("user3")
    void t10() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "insta_user110")
                        .param("attractiveTypeCode", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is4xxClientError());


        List<LikeablePerson> likeablePersonList = likeablePersonRepository.findByFromInstaMemberId(2L);

        Assertions.assertThat(likeablePersonList.size()).isEqualTo(10); // 추가 X
    }
    @Test
    @DisplayName("호감표시 10명일 때 사유변경")
    @WithUserDetails("user3")
    void t11() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "insta_user109")
                        .param("attractiveTypeCode", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is3xxRedirection());


        List<LikeablePerson> likeablePersonList = likeablePersonRepository.findByFromInstaMemberId(2L);

        Assertions.assertThat(likeablePersonList.size()).isEqualTo(10); // 추가 X
    }
    @Test
    @DisplayName("querydsl test")
    void t12() {
        LikeablePerson likeablePerson = likeablePersonRepository.findQslByFromInstaMemberIdAndToInstaMember_username(1L, "insta_user4").orElse(null);

        Assertions.assertThat(likeablePerson.getId()).isEqualTo(2L);
    }
    @Test
    @DisplayName("수정 폼 처리")
    @WithUserDetails("user3")
    void t013() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/usr/likeablePerson/modify/3")
                        .with(csrf()) // CSRF 키 생성
                        .param("attractiveTypeCode", "3")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().is3xxRedirection());

    }
    @Test
    @DisplayName("설정파일에서 호감표시에 대한 수정쿨타임 가져오기 ")
    void t014() throws Exception{
        System.out.println("likeablePersonModifyCoolTime : " + AppConfig.getLikeablePersonModifyCoolTime());
        Assertions.assertThat(AppConfig.getLikeablePersonModifyCoolTime()).isGreaterThan(0);

    }
    @Test
    @DisplayName("호감표시를 하면 쿨타임이 저장된다.")
    void t015() throws Exception{
        LocalDateTime coolTime = AppConfig.getLikeablePersonModifyUnlockDate(); // 현재 시간부터 호감 쿨타임

        Member memberUser2 = memberService.findByUsername("user2").orElseThrow();
        LikeablePerson likeablePersonToBts = likeablePersonService.like(memberUser2, "bts", 2).getData();

        Assertions.assertThat(likeablePersonToBts.getModifyUnlockDate().isAfter(coolTime));
        // 기존 쿨타임(현재 시간부터 쿨타임)보다 갱신된 쿨타임이 더 이후이다.

    }
    @Test
    @DisplayName("호감표시를 변경하면 쿨타임이 저장된다.")
    void t016() throws Exception{
        // 현재시점 기준에서 쿨타임이 다 차는 시간을 구한다.(미래)
        LocalDateTime coolTime = AppConfig.getLikeablePersonModifyUnlockDate();

        Member memberUser2 = memberService.findByUsername("user2").orElseThrow();

        // 호감표시를 생성하면 쿨타임이 지정되기 때문에, 그래서 바로 수정이 안된다.
        // 그래서 강제로 쿨타임이 지난것으로 만든다.
        // 테스트를 위해서 억지로 값을 넣는다.
        LikeablePerson likeablePersonToBts = likeablePersonService.like(memberUser2, "bts", 2).getData();
        Ut.reflection.setFieldValue(likeablePersonToBts, "modifyUnlockDate", LocalDateTime.now().minusSeconds(-1));

        likeablePersonService.modifyAttractive(memberUser2, likeablePersonToBts, 1);

        Assertions.assertThat(likeablePersonToBts.getModifyUnlockDate().isAfter(coolTime));
    }
}
