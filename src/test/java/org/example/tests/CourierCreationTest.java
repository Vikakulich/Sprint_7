package org.example.tests;

import io.restassured.response.Response;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.example.models.CourierData;
import org.example.utils.ApiClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

@Feature("Курьеры")
public class CourierCreationTest {
    private String testLogin;
    private int createdCourierId;

    @Before
    public void setUp() {
        testLogin = "testCourier_" + System.currentTimeMillis();
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
        CourierData courier = new CourierData(testLogin, "password123", "TestName");
        
        Response response = createCourierAndCapture(courier);
        
        assertEquals("Статус код должен быть 201", 201, response.getStatusCode());
        assertTrue("Ответ должен содержать ok: true", response.getBody().asString().contains("\"ok\":true"));
    }

    @Test
    @Description("Нельзя создать двух одинаковых курьеров")
    public void testCreateDuplicateCourier() {
        CourierData courier = new CourierData(testLogin, "password123", "TestName");
        
        // Создаём первого курьера
        Response response1 = createCourierAndCapture(courier);
        assertEquals("Первый курьер должен быть создан", 201, response1.getStatusCode());
        
        // Пытаемся создать курьера с тем же логином
        Response response2 = ApiClient.createCourier(courier);
        assertEquals("Статус код должен быть 409 (Conflict)", 409, response2.getStatusCode());
        assertTrue("Ответ должен содержать сообщение об ошибке",
                response2.getBody().asString().contains("логин"));
    }

    @Test
    @Description("Проверка обязательности поля login")
    public void testCreateCourierWithoutLogin() {
        CourierData courier = new CourierData(null, "password123", "TestName");
        
        Response response = ApiClient.createCourier("{\"password\": \"password123\", \"firstName\": \"TestName\"}");
        
        assertEquals("Статус код должен быть 400", 400, response.getStatusCode());
        assertTrue("Ответ должен содержать сообщение об ошибке",
                response.getBody().asString().contains("Недостаточно данных"));
    }

    @Test
    @Description("Проверка обязательности поля password")
    public void testCreateCourierWithoutPassword() {
        Response response = ApiClient.createCourier("{\"login\": \"" + testLogin + "\", \"firstName\": \"TestName\"}");
        
        assertEquals("Статус код должен быть 400", 400, response.getStatusCode());
        assertTrue("Ответ должен содержать сообщение об ошибке",
                response.getBody().asString().contains("Недостаточно данных"));
    }

    @Test
    @Description("firstName - необязательное поле при создании курьера")
    public void testCreateCourierWithoutFirstName() {
        // API принимает запрос без firstName - он не обязательный
        Response response = ApiClient.createCourier("{\"login\": \"" + testLogin + "\", \"password\": \"password123\"}");
        
        assertEquals("Статус код должен быть 201 (firstName не обязательный)", 201, response.getStatusCode());
        assertTrue("Ответ должен содержать ok: true", response.getBody().asString().contains("\"ok\":true"));
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

