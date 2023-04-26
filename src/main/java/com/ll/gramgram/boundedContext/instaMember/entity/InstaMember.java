package com.ll.gramgram.boundedContext.instaMember.entity;

import com.ll.gramgram.base.baseEntity.BaseEntity;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity
@Getter
public class InstaMember extends BaseEntity {

    @Column(unique = true)
    private String username;
    @Setter
    private String gender;

    @OneToMany(mappedBy = "fromInstaMember", cascade = {CascadeType.ALL})
    @OrderBy("id desc")
    @LazyCollection(LazyCollectionOption.EXTRA)
    @Builder.Default // @Builder가 있으면 new ArrayList(); rk 동작하지 않는데 이를 가능하게 해줌
    private List<LikeablePerson> fromLikeablePerson = new ArrayList<>();


    @OneToMany(mappedBy = "toInstaMember", cascade = {CascadeType.ALL})
    @OrderBy("id desc")
    @LazyCollection(LazyCollectionOption.EXTRA)
    @Builder.Default // @Builder가 있으면 new ArrayList(); rk 동작하지 않는데 이를 가능하게 해줌
    private List<LikeablePerson> toLikeablePerson = new ArrayList<>();

    private long likesCountByGenderWomanAndAttractiveTypeCode1;
    private long likesCountByGenderWomanAndAttractiveTypeCode2;
    private long likesCountByGenderWomanAndAttractiveTypeCode3;
    private long likesCountByGenderManAndAttractiveTypeCode1;
    private long likesCountByGenderManAndAttractiveTypeCode2;
    private long likesCountByGenderManAndAttractiveTypeCode3;

    public Long getLikesCountByGenderWoman() {
        return likesCountByGenderWomanAndAttractiveTypeCode1 + likesCountByGenderWomanAndAttractiveTypeCode2 + likesCountByGenderWomanAndAttractiveTypeCode3;
    }

    public Long getLikesCountByGenderMan() {
        return likesCountByGenderManAndAttractiveTypeCode1 + likesCountByGenderManAndAttractiveTypeCode2 + likesCountByGenderManAndAttractiveTypeCode3;
    }

    public Long getLikesCountByAttractionTypeCode1() {
        return likesCountByGenderWomanAndAttractiveTypeCode1 + likesCountByGenderManAndAttractiveTypeCode1;
    }

    public Long getLikesCountByAttractionTypeCode2() {
        return likesCountByGenderWomanAndAttractiveTypeCode2 + likesCountByGenderManAndAttractiveTypeCode2;
    }

    public Long getLikesCountByAttractionTypeCode3() {
        return likesCountByGenderWomanAndAttractiveTypeCode3 + likesCountByGenderManAndAttractiveTypeCode3;
    }

    public Long getLikes() {
        return getLikesCountByGenderWoman() + getLikesCountByGenderMan();
    }
    public void increaseLikesCount(String gender, int attractiveTypeCode) {
        if (gender.equals("W") && attractiveTypeCode == 1) likesCountByGenderWomanAndAttractiveTypeCode1++;
        if (gender.equals("W") && attractiveTypeCode == 2) likesCountByGenderWomanAndAttractiveTypeCode2++;
        if (gender.equals("W") && attractiveTypeCode == 3) likesCountByGenderWomanAndAttractiveTypeCode3++;
        if (gender.equals("M") && attractiveTypeCode == 1) likesCountByGenderManAndAttractiveTypeCode1++;
        if (gender.equals("M") && attractiveTypeCode == 2) likesCountByGenderManAndAttractiveTypeCode2++;
        if (gender.equals("M") && attractiveTypeCode == 3) likesCountByGenderManAndAttractiveTypeCode3++;
    }

    public void decreaseLikesCount(String gender, int attractiveTypeCode) {
        if (gender.equals("W") && attractiveTypeCode == 1) likesCountByGenderWomanAndAttractiveTypeCode1--;
        if (gender.equals("W") && attractiveTypeCode == 2) likesCountByGenderWomanAndAttractiveTypeCode2--;
        if (gender.equals("W") && attractiveTypeCode == 3) likesCountByGenderWomanAndAttractiveTypeCode3--;
        if (gender.equals("M") && attractiveTypeCode == 1) likesCountByGenderManAndAttractiveTypeCode1--;
        if (gender.equals("M") && attractiveTypeCode == 2) likesCountByGenderManAndAttractiveTypeCode2--;
        if (gender.equals("M") && attractiveTypeCode == 3) likesCountByGenderManAndAttractiveTypeCode3--;
    }
    public String getGenderDisplayName() {
        return switch (gender) {
            case "W" -> "여성";
            default -> "남성";
        };
    }
    public void addFromLikeablePerson(LikeablePerson likeablePerson)
    {
        fromLikeablePerson.add(0, likeablePerson);
    }
    public void addToLikeablePerson(LikeablePerson likeablePerson)
    {
        toLikeablePerson.add(0, likeablePerson);
    }

    public void removeFromLikeablePerson(LikeablePerson likeablePerson) {
        fromLikeablePerson.removeIf(e -> e.equals(likeablePerson));
    }

    public void removeToLikeablePerson(LikeablePerson likeablePerson) {
        toLikeablePerson.removeIf(e -> e.equals(likeablePerson));
    }
}
