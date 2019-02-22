package ru.batov.service.impl;

import ru.batov.model.Item;
import ru.batov.service.IShopService;

import javax.jws.WebService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebService(endpointInterface = "ru.batov.service.IShopService")
public class ShopServiceWSImpl implements IShopService {

    private final Map<Integer, Item> items = new ConcurrentHashMap<>();
    {
        Item item = new Item();
        item.setId(1);
        item.setName("test");
        items.put(item.getId(), item);
    }

    @Override
    public Item[] list() {
        return items.values().toArray(new Item[0]);
    }

    @Override
    public Item findById(int id) {
        return null;
    }

    @Override
    public Item create(Item item) {
        return null;
    }

    @Override
    public void update(Item item) {
    }

    @Override
    public void delele(int id) {
    }
}
