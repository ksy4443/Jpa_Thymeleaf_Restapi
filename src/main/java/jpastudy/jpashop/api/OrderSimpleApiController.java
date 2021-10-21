package jpastudy.jpashop.api;

import jpastudy.jpashop.domain.Order;
import jpastudy.jpashop.domain.OrderSearch;
import jpastudy.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * xToOne(ManyToOne, OneToOne) 관계 최적화
 * Order, Order -> Member , Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        all.forEach(order -> {
            order.getMember().getName(); //Member를 Lazy Loading 강제 초기화
            order.getDelivery().getAddress(); //Delivery를 Lazy Loading 강제 초기화
        });
        return all;
    }
}
