package jpastudy.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpastudy.jpashop.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    //Querydsl 사용한 동적쿼리
    public List<Order> findAll(OrderSearch orderSearch) {
        //return em.createQuery("select o from Order o", Order.class).getResultList();
        //1. JPAQueryFactory 생성
        JPAQueryFactory query = new JPAQueryFactory(em);
        //2. QOrder, QMember 가져오기
        QOrder order = QOrder.order;
        QMember member = QMember.member;
        return query.select(order)
                    .from(order)
                    .join(order.member, member)
                    .where(statusEq(orderSearch.getOrderStatus()),
                            nameLike(orderSearch.getMemberName()))
                .limit(1000)
                .fetch();
    }

    private BooleanExpression nameLike(String memberName) {
        if(!StringUtils.hasText(memberName)) {
            return null;
        }
        return QMember.member.name.contains(memberName);
    }

    private BooleanExpression statusEq(OrderStatus orderStatus) {
        if(orderStatus == null) {
            return null;
        }
        return QOrder.order.status.eq(orderStatus);
    }
}