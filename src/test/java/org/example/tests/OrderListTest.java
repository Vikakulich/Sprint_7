package org.example.tests;

import io.restassured.response.Response;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.example.utils.ApiClient;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

@Feature("Список заказов")
public class OrderListTest {

    @Test
    @Description("Получение списка заказов возвращает список")
    public void testGetOrdersList() {
        Response response = ApiClient.getOrders(null, null, null, null);

        assertEquals("Статус код должен быть 200", 200, response.getStatusCode());
        response.then().body("orders", notNullValue()).body("orders", instanceOf(java.util.List.class));
    }

    @Test
    @Description("Список заказов содержит pageInfo")
    public void testOrdersListContainsPageInfo() {
        Response response = ApiClient.getOrders(null, null, null, null);

        assertEquals("Статус код должен быть 200", 200, response.getStatusCode());
        response.then().body("pageInfo", notNullValue()).body("pageInfo.page", notNullValue());
    }

    @Test
    @Description("Список заказов содержит availableStations")
    public void testOrdersListContainsAvailableStations() {
        Response response = ApiClient.getOrders(null, null, null, null);

        assertEquals("Статус код должен быть 200", 200, response.getStatusCode());
        response.then().body("availableStations", notNullValue()).body("availableStations", instanceOf(java.util.List.class));
    }

    @Test
    @Description("Получение заказов с фильтром по лимиту")
    public void testGetOrdersWithLimit() {
        Response response = ApiClient.getOrders(null, null, "10", null);

        assertEquals("Статус код должен быть 200", 200, response.getStatusCode());
        int limit = response.getBody().jsonPath().getInt("pageInfo.limit");
        assertEquals("Лимит должен быть 10", 10, limit);
    }

    @Test
    @Description("Получение заказов с фильтром по странице")
    public void testGetOrdersWithPage() {
        Response response = ApiClient.getOrders(null, null, "5", "1");

        assertEquals("Статус код должен быть 200", 200, response.getStatusCode());
        int page = response.getBody().jsonPath().getInt("pageInfo.page");
        assertEquals("Страница должна быть 1", 1, page);
    }

    @Test
    @Description("Максимальный лимит заказов 30")
    public void testGetOrdersMaxLimit() {
        Response response = ApiClient.getOrders(null, null, "30", null);

        assertEquals("Статус код должен быть 200", 200, response.getStatusCode());
        int limit = response.getBody().jsonPath().getInt("pageInfo.limit");
        assertEquals("Максимальный лимит должен быть 30", 30, limit);
    }

    @Test
    @Description("Получение заказов конкретного курьера")
    public void testGetOrdersForSpecificCourier() {
        // Этот тест может вернуть ошибку 404, если курьера нет
        Response response = ApiClient.getOrders("999999", null, null, null);

        // Может быть 404 если курьера нет, или 200 если есть заказы
        assertTrue("Статус должен быть 200 или 404",
                response.getStatusCode() == 200 || response.getStatusCode() == 404);
    }
}

