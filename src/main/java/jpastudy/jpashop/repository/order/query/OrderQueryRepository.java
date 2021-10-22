package jpastudy.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    /**
     * Order, Member, Delivery를 한번에 조회
     * : ToOne 관계인 Member, Delivery만 조회함
     * : OrderItem은 조회하지 않음
     */
    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        "select new jpastudy.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    } //findOrders

    /**
     * Order, OrderItem, Item을 조회
     * : ToMany 관계인 OrderItem과 Item을 조회해서 Dto 저장
     */
    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                        "select new jpastudy.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id = : orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    } //findOrderItems

    /**
     * OrderQueryDto가 참조하는 OrderItemQueryDto를 채워넣기
     */
    public List<OrderQueryDto> findOrderQueryDtos() {
        //toOne 관계인 객체를 한번에 조회
        List<OrderQueryDto> orderQueryDtos = findOrders();
        //루프를 돌면서 toMany 관계인 객체를 저장하기
        orderQueryDtos.forEach(order -> {
            List<OrderItemQueryDto> orderItemQueryDtos = findOrderItems(order.getOrderId());
            order.setOrderItems(orderItemQueryDtos);
        });
        return orderQueryDtos;
    }

    /**
     * OrderQueryDto가 참조하는 OrderItemQueryDto를 채워넣기
     * Order, Member, Delivery : 쿼리 1번
     * Order, OrderItem, Item : 쿼리 1번
     * Refactoring 이전 코드
     */
    public List<OrderQueryDto> findOrdersQueryDtos_optimaize_before() {
        List<OrderQueryDto> orders = findOrders();
        //OrderId 목록을 List<Long> 형태로 추출하기
        //List<OrderQueryDto> --> List<Long>
        List<Long> orderIds = orders.stream()     //Stream<OrderQueryDto>
                .map(order -> order.getOrderId())   //Stream<Long>
                .collect(Collectors.toList());//List<Long>

        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpastudy.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        //Map<Long, List<OrderItemQueryDto>>
        //Map<OrderId(주문번호), OrderItemQueryDto목록>
        Map<Long, List<OrderItemQueryDto>> orderItemMap =
                orderItems.stream() //Stream<OrderIteQueryDto>
                          .collect(Collectors.groupingBy(orderItem -> orderItem.getOrderId()));

        orders.forEach(order -> order.setOrderItems(orderItemMap.get(order.getOrderId())));
        return orders;
    }


    public List<OrderQueryDto> findOrdersQueryDtos_optimaize_after() {
        //Order, ToOne 관계인 Member, Delivery를 조회하기
        List<OrderQueryDto> orders = findOrders();

        //Order, ToMany 관계인 OrderItem, Item을 조회하기
        //조회한 OrderItem을 OrderId를 key 값인 Map에 저장하기
        Map<Long, List<OrderItemQueryDto>> orderItemMap = getOrderItemMap(getOrderIds(orders));

        //Map에서 OrderId와 매핑하는 OrderItem 리스트를 Order에 연결해주기
        orders.forEach(order -> order.setOrderItems(orderItemMap.get(order.getOrderId())));
        return orders;
    }

    private Map<Long, List<OrderItemQueryDto>> getOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpastudy.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        //Map<Long, List<OrderItemQueryDto>>
        //Map<OrderId(주문번호), OrderItemQueryDto목록>
        return orderItems.stream() //Stream<OrderIteQueryDto>
                .collect(Collectors.groupingBy(orderItem -> orderItem.getOrderId()));
    }

    private List<Long> getOrderIds(List<OrderQueryDto> orders) {
        //OrderId 목록을 List<Long> 형태로 추출하기
        //List<OrderQueryDto> --> List<Long>
        return orders.stream()     //Stream<OrderQueryDto>
                .map(order -> order.getOrderId())   //Stream<Long>
                .collect(Collectors.toList());
    }
}

