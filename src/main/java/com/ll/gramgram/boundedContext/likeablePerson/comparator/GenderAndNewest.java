package com.ll.gramgram.boundedContext.likeablePerson.comparator;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;

import java.util.Comparator;

public class GenderAndNewest implements Comparator<LikeablePerson> {
    @Override
    public int compare(LikeablePerson p1, LikeablePerson p2) {
        if (p1.getFromInstaMember().getGender().equals("M") && p2.getFromInstaMember().getGender().equals("W")){
            // 남성에게 받은 호감표시가 앞에 있다면
            return 1; // 바꾼다. (여성에게 받은 호감순으로 정렬)
        }
        else if(p1.getFromInstaMember().getGender().equals(p2.getFromInstaMember().getGender())){ // 성별이 같다면
            if(p1.getCreateDate().isAfter(p2.getCreateDate())) { // 이후에 등록된 데이터가 앞에 있더라도
                return -1; // 순서를 바꾸지 않는다.(최신순으로 정렬)
            }
            else return 1;
        }

        return -1;
    }
}
