package com.ll.gramgram.base.appConfig;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Getter
    private static long fromLikeablePersonMax;

    @Value("${custom.fromLikeablePerson.max}")
    public void setFromLikeablePersonMax(Long fromLikeablePersonMax){
        AppConfig.fromLikeablePersonMax = fromLikeablePersonMax;
    }
}
