## Title: [1Week] 지승용

### 미션 요구사항 분석 & 체크리스트

# 할일

- [O] 호감상대 삭제
  - [O] 로그인한 사람만 가능
  - [O] 해당 항목에 대한 소유권이 있어야 함
  - [O] 삭제 확인
  - [O] 삭제 후 다시 호감목록 페이지로 이동
- [O] 구글로그인
  - [O] 재시작 시에도 DB자동 등록

- [O] 리팩토링
  - [O] op.isPresent()는 컨트롤러에서 확인!
  - [O] 삭제 시 username -> id 이용
  - [O] orElse(null) -> Optional반환
  - [O] Rq는 컨트롤러단에서만 사용
  - [O] getMapping -> deleteMapping 변경
  
### N주차 미션 요약

---

**[접근 방법]**

- TDD기반으로 기능을 구현하는 것에 집중하였으며 테스트에서 MvcMock객체를 어떻게 사용법, Transactional의 동작원리를 추가적으로 학습하며 구현했습니다
- 우선 특정 사용자로부터의 호감 리스트를 가져와야 했기에 셋업 데이터를 생성한 뒤 @WithUserDetails("user2")를 통해 특정 사용자에 대해 상황을 구체화 시켰습니다.
- 멤버가 삭제된 후에는 DB에서 해당 멤버를 조회해 Null체크를 통해 삭제 로직이 잘 동작하는지 확인했습니다. 
- 삭제 로직이 잘 동작하는 것을 확인한 후에는 호감 리스트에 권한이 권한이 없는 유저로 접근하여 실패하는 테스트를 생성했습니다.
- 로그인한 멤버의 인스타아이디와 삭제하려고 하는 LikeablePerson의 fromInstaUsername을 비교하여 실패 예외처리를 해주어 테스트를 성공시켰습니다.

**[알게된 점]**
- op.isPresent()는 컨트롤러에서 확인!

- 메서드명은 명확할 수록 좋음
delete -> deleteLikeablePerson

- 삭제 시 이름 보다는 id값으로 하는게 좋음
(username은 변경 가능 + 번호 비교가 더 빠름)

- orElse(null) 보단 Optional반환을 지향(Optional 추가기능이 유용함!)

- Rq는 컨트롤러단에서!(컨트롤러는 입구컷) 복잡한 로직은 서비스단에서
이 둘이 모호할 경우에는 취향차이

- get -> delete
get매핑은 csrf이슈


- 서비스는 로그인에 대한 정보를 몰라도 돌아갈 수 있게끔
loginedInstaMemberId -> InstaMemberId

- 테스트 케이스는 한번에 하나만!

- MPA 스프링부트가 view단 서버단 동시관리
SPA는 스프링부트가 서버단만 관리 뷰 단은
JSON통신(username을 name으로 할것인지 nickname으로 할것인지 등 네이밍 규약을 ㅁㅈ춰놓음)
DB변경시 Entity까지 변경될 수 있는 방식을 제어(뷰 단에 노출은 entity가 아닌 DTO)




**[특이사항]**

- 궁금했던 점으로는 likeablePerson의 Id로 삭제하는 것이 아닌 현재 인증된 유저의 이름과 삭제할 유저의 이름으로도 삭제할 수 있을까입니다.


**[참고 자료]**

https://lucaskim.tistory.com/40

https://lovon.tistory.com/167

http://jjhprog.tistory.com/88
