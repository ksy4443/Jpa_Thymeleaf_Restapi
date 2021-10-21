package jpastudy.jpashop.service;

import jpastudy.jpashop.domain.item.Item;
import jpastudy.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

    //Dirty Checking을 사용한 update
    @Transactional
    public void updateItem(Long id, String name, int price) {
        //영속성컨텍스트에 저장된 Item 먼저 조회
        Item item = itemRepository.findOne(id);
        //setter, method 만 호출해도 테이블의 데이터가 갱신되어 진다.
        item.setName(name);
        item.setPrice(price);
    }
}