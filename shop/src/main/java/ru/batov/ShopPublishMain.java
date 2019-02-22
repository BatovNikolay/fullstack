package ru.batov;

import ru.batov.service.impl.ShopServiceWSImpl;

import javax.xml.ws.Endpoint;

public class ShopPublishMain {
    public static void main(String[] args) {
        Endpoint.publish("http://localhost:7779/ws/shop", new ShopServiceWSImpl());
    }
}
