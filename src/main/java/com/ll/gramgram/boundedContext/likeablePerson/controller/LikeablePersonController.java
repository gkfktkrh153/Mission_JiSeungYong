package com.ll.gramgram.boundedContext.likeablePerson.controller;

import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/likeablePerson")
@RequiredArgsConstructor
public class LikeablePersonController {
    private final Rq rq;
    private final LikeablePersonService likeablePersonService;
    private final LikeablePersonRepository likeablePersonRepository;
    @GetMapping("/add")
    public String showAdd() {
        return "usr/likeablePerson/add";
    }

    @AllArgsConstructor
    @Getter
    public static class AddForm {
        private final String username;
        private final int attractiveTypeCode;
    }

    @PostMapping("/add")
    public String add(@Valid AddForm addForm) {
        RsData<LikeablePerson> createRsData = likeablePersonService.like(rq.getMember(), addForm.getUsername(), addForm.getAttractiveTypeCode());

        if (createRsData.isFail()) {
            return rq.historyBack(createRsData);
        }

        return rq.redirectWithMsg("/likeablePerson/list", createRsData);
    }

    @GetMapping("/list")
    public String showList(Model model) {
        InstaMember instaMember = rq.getMember().getInstaMember();

        // 인스타인증을 했는지 체크
        if (instaMember != null) {
            List<LikeablePerson> likeablePeople = likeablePersonService.findByFromInstaMemberId(instaMember.getId());
            model.addAttribute("likeablePeople", likeablePeople);
        }

        return "usr/likeablePerson/list";
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{likeablePersonId}")
    public String delete(@PathVariable("likeablePersonId") Long deleteId){
        Optional<LikeablePerson> opLikeablePerson = likeablePersonRepository.findById(deleteId);

        if (opLikeablePerson.isEmpty())
            return rq.historyBack( "해당 데이터는 이미 삭제되었습니다.");

        LikeablePerson likeablePerson = opLikeablePerson.get();

        if (!(likeablePerson.getFromInstaMember().getId().equals(rq.getMember().getInstaMember().getId())))           // 현재 로그인된 멤버의 인스타아이디가 likeablePerson의 from(호감을 표시한 본인)이 아닐 때
            return rq.historyBack( "%s는 삭제 권한이 없습니다".formatted(likeablePerson.getToInstaMemberUsername()));


        RsData<String> deleteRsData = likeablePersonService.delete(likeablePerson);

        if (deleteRsData.isFail())
            return rq.historyBack(deleteRsData);

        return rq.redirectWithMsg("/likeablePerson/list", deleteRsData.getMsg());
    }
}
