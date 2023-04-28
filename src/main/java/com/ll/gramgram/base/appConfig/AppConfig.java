package com.ll.gramgram.base.appConfig;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class AppConfig {
    @Getter
    private static long fromLikeablePersonMax;

    @Value("${custom.fromLikeablePerson.max}")
    public void setFromLikeablePersonMax(Long fromLikeablePersonMax){
        AppConfig.fromLikeablePersonMax = fromLikeablePersonMax;
    }

    @Getter
    private static long likeablePersonModifyCoolTime;

    @Value("${custom.likeablePerson.modifyCoolTime}")
    public void setLikeablePersonModifyCoolTime(long likeablePersonModifyCoolTime) {
        AppConfig.likeablePersonModifyCoolTime = likeablePersonModifyCoolTime;
    }

    public static LocalDateTime getLikeablePersonModifyUnlockDate(){
        return LocalDateTime.now().plusSeconds(AppConfig.getLikeablePersonModifyCoolTime());
    }
}
