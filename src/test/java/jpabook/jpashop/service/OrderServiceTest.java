package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();

        Book book = createBook("책1", 1000, 10);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        //then

        Order gerOrder = orderRepository.findOne(orderId);

        assertEquals("상품 주문시 ORDER", OrderStatus.ORDER, gerOrder.getStatus());
        assertEquals("상품 주문 종류의 수 확인",1,gerOrder.getOrderItems().size());
        assertEquals("가격 확인",1000*orderCount,gerOrder.getTotalPrice());
        assertEquals("재고 감소 확인",8,book.getStockQuantity());
    }



    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("책2",10000,10);

        int orderCount = 10;
        //when
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        orderService.cancelOrder(orderId);
        //then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals("상품 취소시 CANCEL", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("재고 증가 확인",10,item.getStockQuantity());
    }
    
    @Test(expected = NotEnoughStockException.class)
    public void 재고수량초과_예외() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("책3",10000,10);

        int orderCount = 11;
        //when
        orderService.order(member.getId(), item.getId(), orderCount);
        //then
        fail("재고예외 발생해야함");
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울","관악구","111-11"));
        em.persist(member);
        return member;
    }
}