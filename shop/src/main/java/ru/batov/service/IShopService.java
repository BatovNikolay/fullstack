package ru.batov.service;

import ru.batov.model.Item;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import static javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface IShopService {

    @WebMethod
    Item[] list();

    @WebMethod
    Item findById(int id);

    @WebMethod
    Item create(Item item);

    @WebMethod
    void update(Item item);

    @WebMethod
    void delele(int id);
}
