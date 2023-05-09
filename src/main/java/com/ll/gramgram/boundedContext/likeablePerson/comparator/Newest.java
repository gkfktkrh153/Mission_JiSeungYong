package com.ll.gramgram.boundedContext.likeablePerson.comparator;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;

import java.util.Comparator;

public class Newest implements Comparator<LikeablePerson> {
    @Override
    public int compare(LikeablePerson p1, LikeablePerson p2) {
        if (p1.getCreateDate().isAfter(p2.getCreateDate())) { // 이후에 등록된 데이터가 앞에 있더라도
            return -1; // 순서를 바꾸지 않는다.(나중에 등록된 순으로 정렬)
        }
        return 1;
    }
}
