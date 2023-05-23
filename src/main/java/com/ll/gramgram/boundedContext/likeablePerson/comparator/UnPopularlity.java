package com.ll.gramgram.boundedContext.likeablePerson.comparator;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;

import java.util.Comparator;

public class UnPopularlity implements Comparator<LikeablePerson> {
    @Override
    public int compare(LikeablePerson p1, LikeablePerson p2) {
        if (p1.getFromInstaMember().getLikes() > p2.getFromInstaMember().getLikes()) { // 인기있는 데이터가 앞에 있다면
            return 1; // 순서를 바꾼다.(인기 없는 순으로 정렬)
        }
        return -1;
    }
}
