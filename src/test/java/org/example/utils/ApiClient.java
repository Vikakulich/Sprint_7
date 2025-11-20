package org.example.utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.qameta.allure.restassured.AllureRestAssured;

public class ApiClient {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    private static final String API_VERSION = "/api/v1";

    static {
        RestAssured.baseURI = BASE_URL;
    }

    private static RequestSpecification getRequest() {
        return RestAssured.given()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .baseUri(BASE_URL);
    }

    // Курьер: Создание
    public static Response createCourier(Object courierData) {
        return getRequest()
                .body(courierData)
                .post(API_VERSION + "/courier");
    }

    // Курьер: Логин
    public static Response loginCourier(Object credentials) {
        return getRequest()
                .body(credentials)
                .post(API_VERSION + "/courier/login");
    }

    // Курьер: Удаление
    public static Response deleteCourier(int courierId) {
        return getRequest()
                .delete(API_VERSION + "/courier/" + courierId);
    }

    // Курьер: Получить количество заказов
    public static Response getOrdersCount(int courierId) {
        return getRequest()
                .get(API_VERSION + "/courier/" + courierId + "/ordersCount");
    }

    // Заказ: Создание
    public static Response createOrder(Object orderData) {
        return getRequest()
                .body(orderData)
                .post(API_VERSION + "/orders");
    }

    // Заказы: Получить список
    public static Response getOrders(String courierId, String nearestStation, String limit, String page) {
        RequestSpecification request = getRequest();

        if (courierId != null && !courierId.isEmpty()) {
            request.queryParam("courierId", courierId);
        }
        if (nearestStation != null && !nearestStation.isEmpty()) {
            request.queryParam("nearestStation", nearestStation);
        }
        if (limit != null && !limit.isEmpty()) {
            request.queryParam("limit", limit);
        }
        if (page != null && !page.isEmpty()) {
            request.queryParam("page", page);
        }

        return request.get(API_VERSION + "/orders");
    }

    // Заказ: Получить по номеру
    public static Response getOrderByTrack(String trackNumber) {
        return getRequest()
                .queryParam("t", trackNumber)
                .get(API_VERSION + "/orders/track");
    }

    // Заказ: Принять
    public static Response acceptOrder(int orderId, int courierId) {
        return getRequest()
                .queryParam("courierId", courierId)
                .put(API_VERSION + "/orders/accept/" + orderId);
    }

    // Заказ: Завершить
    public static Response finishOrder(int orderId) {
        return getRequest()
                .put(API_VERSION + "/orders/finish/" + orderId);
    }

    // Заказ: Отменить
    public static Response cancelOrder(int trackNumber) {
        return getRequest()
                .put(API_VERSION + "/orders/cancel/" + trackNumber);
    }

    // Утилиты: Ping
    public static Response ping() {
        return getRequest()
                .get(API_VERSION + "/ping");
    }

    // Утилиты: Поиск станций метро
    public static Response searchStations(String searchQuery) {
        return getRequest()
                .queryParam("s", searchQuery)
                .get(API_VERSION + "/stations/search");
    }
}

