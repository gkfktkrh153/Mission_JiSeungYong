package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.expression.Lists;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;
    @Value("${MAX_COUNT_LIKEABLE_PERSON}")
    private Long MAX_COUNT_LIKEABLE_PERSON;

    @Transactional
    public RsData<LikeablePerson> like(Member actor, String username, int attractiveTypeCode) {
        InstaMember toLikeInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        RsData canLikeRsData = canLike(actor, toLikeInstaMember, attractiveTypeCode);

        if (canLikeRsData.isFail()) return canLikeRsData;

        if (canLikeRsData.getResultCode().equals("S-1")) {// 호감 사유 변경이 가능할 때!
            ((LikeablePerson) canLikeRsData.getData()) // "S-1" 일 때는 변경할 likeablePerson 객체가 리턴됨
                    .changeAttractiveType(attractiveTypeCode);
            return canLikeRsData;
        }

        InstaMember fromLikeInstaMember = (InstaMember) canLikeRsData.getData(); // 호감을 표현할 수 있음이 검증된 객체

        LikeablePerson likeablePerson = buildLikeablePerson(fromLikeInstaMember, toLikeInstaMember, attractiveTypeCode);
        likeablePersonRepository.save(likeablePerson); // 저장

        fromLikeInstaMember.addFromLikeablePerson(likeablePerson); // 양방향 연관관계 설정
        toLikeInstaMember.addToLikeablePerson(likeablePerson);

        return RsData.of("S-3", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }

    private RsData canLike(Member actor, InstaMember toLikeInstaMember, int attractiveTypeCode) {
        if (!actor.hasConnectedInstaMember()) {
            return RsData.of("F-1", "먼저 본인의 인스타그램 아이디를 입력해주세요.");
        }

        if (actor.getInstaMember().getUsername().equals(toLikeInstaMember.getUsername()))
            return RsData.of("F-2", "본인을 호감상대로 등록할 수 없습니다.");


        InstaMember fromLikeInstaMember = actor.getInstaMember();

        List<LikeablePerson> likes = likeablePersonRepository.findByFromInstaMemberIdAndToInstaMemberId(fromLikeInstaMember.getId(), toLikeInstaMember.getId());

        if (!likes.isEmpty()) {// 이전에 상대방에게 호감을 표시한 경우
            LikeablePerson likeablePerson = likes.get(0);
            int priorAttractiveTypeCode = likeablePerson.getAttractiveTypeCode();
            if (attractiveTypeCode == priorAttractiveTypeCode)  // 호감 표현 사유가 이전과 동일
                return RsData.of("F-3", "같은 사유로 동일한 대상에게 호감을 표현할 수 없습니다");
            return RsData.of("S-1", "입력하신 인스타유저(%s)에 대한 호감사유를 %s에서 %s로 변경합니다.".formatted(toLikeInstaMember.getUsername(), getAttractiveTypeDisplayName(priorAttractiveTypeCode), getAttractiveTypeDisplayName(attractiveTypeCode)),likeablePerson);
        }
        int countOfLikeablePerson = likeablePersonRepository.findByFromInstaMemberId(fromLikeInstaMember.getId()).size();

        if (countOfLikeablePerson >= MAX_COUNT_LIKEABLE_PERSON)
            return RsData.of("F-4", "10명 이상의 대상에게 호감을 표시할 수 없습니다.");
        return RsData.of("S-2", "호감표시가 가능합니다.", fromLikeInstaMember);
    }


    private LikeablePerson buildLikeablePerson(InstaMember fromInstaMember, InstaMember toInstaMember, int attractiveTypeCode) {
        return  LikeablePerson
                .builder()
                .fromInstaMember(fromInstaMember) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(fromInstaMember.getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();
    }

    public String getAttractiveTypeDisplayName(int attractiveTypeCode) {
        return switch (attractiveTypeCode) {
            case 1 -> "외모";
            case 2 -> "성격";
            default -> "능력";
        };
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
