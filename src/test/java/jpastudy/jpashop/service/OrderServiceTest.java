package jpastudy.jpashop.service;

import jpastudy.jpashop.domain.*;
import jpastudy.jpashop.domain.item.Book;
import jpastudy.jpashop.domain.item.Item;
import jpastudy.jpashop.exception.NotEnoughStockException;
import jpastudy.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 주문검색() throws Exception {
        Member member = createMember("몽타", new Address("서울", "동작", "12345"));
        Item item = createBook("부트책", 10000, 10);
        int orderCnt = 3;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCnt);

        OrderSearch search = new OrderSearch();
        search.setMemberName("몽");
        search.setOrderStatus(OrderStatus.ORDER);
        List<Order> orders = orderService.findOrders(search);
        assertEquals("검색된 Order 갯수", 1, orders.size());
    }

    @Test
    public  void 상품주문() throws Exception {
        Member member = createMember("몽타", new Address("서울", "동작", "12345"));
        Item item = createBook("부트책", 10000, 10);
        int orderCnt = 3;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCnt);

        Order getOrder = orderRepository.findOne(orderId);
        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다.",1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다.", 10000 * 3, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다.",7, item.getStockQuantity());
        assertEquals("주문한 상품의 이름이 같아야 한다.", "부트책", item.getName());
    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        //Given
        Member member = createMember("회원1", new Address("서울", "성내로", "80"));
        Item item = createBook("스프링 부트", 10000, 10); //이름, 가격, 재고
        int orderCount = 11; //재고보다 많은 수량
        NotEnoughStockException exception =
                Assertions.assertThrows(NotEnoughStockException.class, () -> {
        //When
                    orderService.order(member.getId(), item.getId(), orderCount);
                });
        //Then
        assertNotNull("NotEnoughStockException 발생", exception);
        assertEquals("재고수량이 부족함","need more stock",exception.getMessage());
    }

    @Test
    public void 주문취소() {
        //Given
        Member member = createMember("회원1", new Address("서울", "성내로", "180"));
        Item item = createBook("스프링 부트", 10000, 10); //이름, 가격, 재고
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(),orderCount);

        assertEquals("주문취소하기 전의 재고수량 ",8, item.getStockQuantity());
        //When
        orderService.cancelOrder(orderId);
        //Then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals("주문 취소시 상태는 CANCEL 이다.",OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다.", 10, item.getStockQuantity());
    }

    private Member createMember(String name, Address address) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(address);
        em.persist(member);
        return member;
    }
    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        em.persist(book);
        return book;
    }

}