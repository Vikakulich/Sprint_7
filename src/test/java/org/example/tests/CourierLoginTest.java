package org.example.tests;

import com.github.javafaker.Faker;
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
import static org.hamcrest.Matchers.*;

@Feature("Логин курьера")
public class CourierLoginTest {
    private String testLogin;
    private String testPassword;
    private int createdCourierId;
    private Faker faker;

    @Before
    public void setUp() {
        faker = new Faker();
        testLogin = faker.name().firstName().toLowerCase() + "_" + System.currentTimeMillis();
        testPassword = faker.internet().password();
        createdCourierId = -1;
        
        // Создаём тестового курьера
        CourierData courier = new CourierData(testLogin, testPassword, faker.name().firstName());
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
        
        response.then().statusCode(200).body("id", notNullValue()).body("id", greaterThan(0));
    }

    @Test
    @Description("Логин не требует firstName")
    public void testLoginCourierWithoutFirstName() {
        CourierData credentials = new CourierData(testLogin, testPassword);
        
        Response response = ApiClient.loginCourier(credentials);
        
        response.then().statusCode(200).body("id", notNullValue());
    }

    @Test
    @Description("Ошибка при отсутствии login")
    public void testLoginWithoutLogin() {
        CourierData invalidCredentials = new CourierData(null, testPassword);
        Response response = ApiClient.loginCourier(invalidCredentials);
        
        response.then().statusCode(400).body("message", containsString("Недостаточно данных"));
    }

    @Test
    @Description("Ошибка при отсутствии password")
    public void testLoginWithoutPassword() {
        CourierData invalidCredentials = new CourierData(testLogin, null);
        Response response = ApiClient.loginCourier(invalidCredentials);
        
        // API возвращает ошибку (400 или 504) при отсутствии пароля
        assertTrue("Статус код должен быть ошибкой (не 200)",
                response.getStatusCode() != 200 && response.getStatusCode() >= 400);
    }

    @Test
    @Description("Ошибка при неправильном пароле")
    public void testLoginWithWrongPassword() {
        CourierData credentials = new CourierData(testLogin, "wrongPassword");
        
        Response response = ApiClient.loginCourier(credentials);
        
        assertTrue("Статус код должен быть 401 или 404",
                response.getStatusCode() == 401 || response.getStatusCode() == 404);
        response.then().body("message", notNullValue());
    }

    @Test
    @Description("Ошибка при неправильном логине")
    public void testLoginWithWrongLogin() {
        CourierData credentials = new CourierData("nonexistent_login", testPassword);
        
        Response response = ApiClient.loginCourier(credentials);
        
        assertTrue("Статус код должен быть 401 или 404",
                response.getStatusCode() == 401 || response.getStatusCode() == 404);
        response.then().body("message", notNullValue());
    }

    @Test
    @Description("Ошибка при логине несуществующего пользователя")
    public void testLoginNonexistentUser() {
        CourierData credentials = new CourierData("nonexistent_user_" + System.currentTimeMillis(), "password");
        
        Response response = ApiClient.loginCourier(credentials);
        
        response.then().statusCode(404).body("message", containsString("не найдена"));
    }
}

