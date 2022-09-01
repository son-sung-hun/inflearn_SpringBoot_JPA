package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {
    @PersistenceContext
    private EntityManager em;

    public Long save(Member member){ //생성
       em.persist(member);
        return member.getId();
    }

    public Member find(Long id){ //조회
        return em.find(Member.class, id);
    }
}
