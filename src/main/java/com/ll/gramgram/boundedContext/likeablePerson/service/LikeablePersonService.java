package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;

    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {
        if ( member.hasConnectedInstaMember() == false ) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        LikeablePerson likeablePerson = buildLikeablePerson(member, toInstaMember, attractiveTypeCode);


        likeablePersonRepository.save(likeablePerson); // 저장
        InstaMember fromInstaMember = member.getInstaMember();

        fromInstaMember.addFromLikeablePerson(likeablePerson);
        toInstaMember.addToLikeablePerson(likeablePerson);

        return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }

    private LikeablePerson buildLikeablePerson(Member member, InstaMember toInstaMember, int attractiveTypeCode) {
        return  LikeablePerson
                .builder()
                .fromInstaMember(member.getInstaMember()) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(member.getInstaMember().getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();
    }

    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }

    public RsData<Object> canDelete(Member actor, LikeablePerson likeablePerson) {
        if(likeablePerson == null) return RsData.of("F-1", "이미 삭제되었습니다.");


        if (!(likeablePerson.getFromInstaMember().getId().equals(actor.getInstaMember().getId())))           // 현재 로그인된 멤버의 인스타아이디가 likeablePerson의 from(호감을 표시한 본인)이 아닐 때
            return RsData.of( "F-2", "%s는 삭제 권한이 없습니다".formatted(likeablePerson.getToInstaMemberUsername()));

        return RsData.of("S-1", "삭제가능합니다");
    }
    @Transactional
    public RsData<String> delete(LikeablePerson likeablePerson) {



        likeablePersonRepository.delete(likeablePerson);


        return RsData.of("S-1", "입력하신 인스타유저(%s)가 호감상대에서 삭제되었습니다".formatted(likeablePerson.getToInstaMemberUsername()));
    }
}
