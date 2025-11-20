package org.example.tests;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.restassured.response.Response;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.example.models.CourierData;
import org.example.utils.ApiClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

@Feature("Курьеры")
public class CourierCreationTest {
    private String testLogin;
    private int createdCourierId;
    private Faker faker;
    private Gson gson;

    @Before
    public void setUp() {
        faker = new Faker();
        gson = new Gson();
        testLogin = faker.name().firstName().toLowerCase() + "_" + System.currentTimeMillis();
        createdCourierId = -1;
    }

    @After
    public void tearDown() {
        // Удаляем созданного курьера
        if (createdCourierId != -1) {
            ApiClient.deleteCourier(createdCourierId);
        }
    }

    @Test
    @Description("Проверка создания курьера с корректными данными")
    public void testCreateCourierSuccess() {
        String password = faker.internet().password();
        String firstName = faker.name().firstName();
        CourierData courier = new CourierData(testLogin, password, firstName);
        
        Response response = createCourierAndCapture(courier);
        
        assertEquals("Статус код должен быть 201", 201, response.getStatusCode());
        response.then().body("ok", equalTo(true));
    }

    @Test
    @Description("Нельзя создать двух одинаковых курьеров")
    public void testCreateDuplicateCourier() {
        String password = faker.internet().password();
        String firstName = faker.name().firstName();
        CourierData courier = new CourierData(testLogin, password, firstName);
        
        // Создаём первого курьера
        Response response1 = createCourierAndCapture(courier);
        assertEquals("Первый курьер должен быть создан", 201, response1.getStatusCode());
        
        // Пытаемся создать курьера с тем же логином
        Response response2 = ApiClient.createCourier(courier);
        assertEquals("Статус код должен быть 409 (Conflict)", 409, response2.getStatusCode());
        response2.then().body("message", containsString("логин"));
    }

    @Test
    @Description("Проверка обязательности поля login")
    public void testCreateCourierWithoutLogin() {
        String password = faker.internet().password();
        String firstName = faker.name().firstName();
        
        CourierData invalidCourier = new CourierData(null, password, firstName);
        Response response = ApiClient.createCourier(invalidCourier);
        
        assertEquals("Статус код должен быть 400", 400, response.getStatusCode());
        response.then().body("message", containsString("Недостаточно данных"));
    }

    @Test
    @Description("Проверка обязательности поля password")
    public void testCreateCourierWithoutPassword() {
        String firstName = faker.name().firstName();
        CourierData invalidCourier = new CourierData(testLogin, null, firstName);
        Response response = ApiClient.createCourier(invalidCourier);
        
        assertEquals("Статус код должен быть 400", 400, response.getStatusCode());
        response.then().body("message", containsString("Недостаточно данных"));
    }

    @Test
    @Description("firstName - необязательное поле при создании курьера")
    public void testCreateCourierWithoutFirstName() {
        // API принимает запрос без firstName - он не обязательный
        String password = faker.internet().password();
        CourierData courierWithoutFirstName = new CourierData(testLogin, password);
        Response response = ApiClient.createCourier(courierWithoutFirstName);
        
        assertEquals("Статус код должен быть 201 (firstName не обязательный)", 201, response.getStatusCode());
        response.then().body("ok", equalTo(true));
    }

    @Step("Создание курьера и запоминание его ID")
    private Response createCourierAndCapture(CourierData courier) {
        Response response = ApiClient.createCourier(courier);
        
        if (response.getStatusCode() == 201) {
            try {
                createdCourierId = response.getBody().jsonPath().getInt("id");
            } catch (Exception e) {
                // ID может быть не возвращён
            }
        }
        
        return response;
    }
}

