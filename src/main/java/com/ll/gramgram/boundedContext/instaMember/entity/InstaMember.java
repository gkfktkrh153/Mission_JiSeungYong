package com.ll.gramgram.boundedContext.instaMember.entity;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString
@Entity
@Getter
public class InstaMember {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @CreatedDate
    private LocalDateTime createDate;
    @LastModifiedDate
    private LocalDateTime modifyDate;
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

    public void addFromLikeablePerson(LikeablePerson likeablePerson)
    {
        fromLikeablePerson.add(0, likeablePerson);
    }
    public void addToLikeablePerson(LikeablePerson likeablePerson)
    {
        toLikeablePerson.add(0, likeablePerson);
    }
}
