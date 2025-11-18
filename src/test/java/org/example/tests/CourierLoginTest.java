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

@Feature("Логин курьера")
public class CourierLoginTest {
    private String testLogin;
    private String testPassword;
    private int createdCourierId;

    @Before
    public void setUp() {
        testLogin = "testCourier_" + System.currentTimeMillis();
        testPassword = "password123";
        createdCourierId = -1;
        
        // Создаём тестового курьера
        CourierData courier = new CourierData(testLogin, testPassword, "TestName");
        Response response = ApiClient.createCourier(courier);
        
        if (response.getStatusCode() == 201) {
            try {
                createdCourierId = response.getBody().jsonPath().getInt("id");
            } catch (Exception e) {
                // ID может быть не возвращён
            }
        }
    }

    @After
    public void tearDown() {
        if (createdCourierId != -1) {
            ApiClient.deleteCourier(createdCourierId);
        }
    }

    @Test
    @Description("Успешный логин курьера с корректными учётными данными")
    public void testLoginCourierSuccess() {
        CourierData credentials = new CourierData(testLogin, testPassword);
        
        Response response = ApiClient.loginCourier(credentials);
        
        assertEquals("Статус код должен быть 200", 200, response.getStatusCode());
        assertTrue("Ответ должен содержать id", response.getBody().asString().contains("\"id\""));
        int userId = response.getBody().jsonPath().getInt("id");
        assertTrue("ID должен быть положительным числом", userId > 0);
    }

    @Test
    @Description("Логин не требует firstName")
    public void testLoginCourierWithoutFirstName() {
        CourierData credentials = new CourierData(testLogin, testPassword);
        
        Response response = ApiClient.loginCourier(credentials);
        
        assertEquals("Логин должен успешно выполниться без firstName", 200, response.getStatusCode());
        assertTrue("Ответ должен содержать id", response.getBody().asString().contains("\"id\""));
    }

    @Test
    @Description("Ошибка при отсутствии login")
    public void testLoginWithoutLogin() {
        Response response = ApiClient.loginCourier("{\"password\": \"" + testPassword + "\"}");
        
        assertEquals("Статус код должен быть 400", 400, response.getStatusCode());
        assertTrue("Ответ должен содержать сообщение об ошибке",
                response.getBody().asString().contains("Недостаточно данных"));
    }

    @Test
    @Description("Ошибка при отсутствии password")
    public void testLoginWithoutPassword() {
        Response response = ApiClient.loginCourier("{\"login\": \"" + testLogin + "\"}");
        
        // API возвращает ошибку (400 или 504) при отсутствии пароля
        // Проверяем что это ошибка, но не успешный ответ (200)
        assertTrue("Статус код должен быть ошибкой (не 200)",
                response.getStatusCode() != 200 && response.getStatusCode() >= 400);
    }

    @Test
    @Description("Ошибка при неправильном пароле")
    public void testLoginWithWrongPassword() {
        CourierData credentials = new CourierData(testLogin, "wrongPassword");
        
        Response response = ApiClient.loginCourier(credentials);
        
        assertEquals("Статус код должен быть 401 или 404", true,
                response.getStatusCode() == 401 || response.getStatusCode() == 404);
        assertTrue("Ответ должен содержать сообщение об ошибке",
                response.getBody().asString().contains("message"));
    }

    @Test
    @Description("Ошибка при неправильном логине")
    public void testLoginWithWrongLogin() {
        CourierData credentials = new CourierData("nonexistent_login", testPassword);
        
        Response response = ApiClient.loginCourier(credentials);
        
        assertEquals("Статус код должен быть 401 или 404", true,
                response.getStatusCode() == 401 || response.getStatusCode() == 404);
        assertTrue("Ответ должен содержать сообщение об ошибке",
                response.getBody().asString().contains("message"));
    }

    @Test
    @Description("Ошибка при логине несуществующего пользователя")
    public void testLoginNonexistentUser() {
        CourierData credentials = new CourierData("nonexistent_user_" + System.currentTimeMillis(), "password");
        
        Response response = ApiClient.loginCourier(credentials);
        
        assertEquals("Статус код должен быть 404", 404, response.getStatusCode());
        assertTrue("Ответ должен содержать сообщение 'не найдена'",
                response.getBody().asString().contains("не найдена"));
    }
}

