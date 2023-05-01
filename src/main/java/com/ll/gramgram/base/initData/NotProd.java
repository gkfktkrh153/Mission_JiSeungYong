package com.ll.gramgram.base.initData;

import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Profile({"dev", "test"})
public class NotProd {
    @Autowired
    Environment environment;
    @Bean
    CommandLineRunner initData(
            MemberService memberService,
            InstaMemberService instaMemberService,
            LikeablePersonService likeablePersonService
    ){
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {
                Member memberAdmin = memberService.join("admin", "1234").getData();
                Member memberUser1 = memberService.join("user1", "1234").getData();
                Member memberUser2 = memberService.join("user2", "1234").getData();
                Member memberUser3 = memberService.join("user3", "1234").getData();
                Member memberUser4 = memberService.join("user4", "1234").getData();

                Member memberUser5ByKakao = memberService.whenSocialLogin("KAKAO", environment.getProperty("KAKAO_CLIENT_ID")).getData();
                Member memberUser6ByGoogle = memberService.whenSocialLogin("GOOGLE", environment.getProperty("GOOGLE_CLIENT_ID")).getData();
                Member memberUser7ByNAVER = memberService.whenSocialLogin("NAVER", environment.getProperty("NAVER_CLIENT_ID")).getData();
                Member memberUser8ByFACEBOOK = memberService.whenSocialLogin("FACEBOOK", environment.getProperty("FACEBOOK_CLIENT_ID")).getData();

                instaMemberService.connect(memberUser2, "insta_user2", "M");
                instaMemberService.connect(memberUser3, "insta_user3", "W");
                instaMemberService.connect(memberUser4, "insta_user4", "M");


                likeablePersonService.like(memberUser2, "insta_user3", 1);
                likeablePersonService.like(memberUser2, "insta_user4", 2);
                for (int i = 100; i < 110; i++){
                    likeablePersonService.like(memberUser3, String.format("insta_user%d",i), 2);
                }

                memberUser3.getInstaMember().updateGender("M");
            }
        };
    }
}
