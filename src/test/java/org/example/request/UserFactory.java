package org.example.request;

public class UserFactory {

    public static CreateUserRequest createUserRequest(String email) {
        var request = new CreateUserRequest();
        request.setEmail(email);
        request.setPassword("qwert");
        request.setName("Username");
        return request;
    }

    public static CreateUserRequest createWithoutEmailRequest() {
        var request = new CreateUserRequest();
        request.setEmail(null);
        request.setPassword("qwert1");
        request.setName("Username");
        return request;
    }

    public static CreateUserRequest createWithoutPasswordRequest() {
        var request = new CreateUserRequest();
        request.setEmail("test@yandex.ru");
        request.setPassword(null);
        request.setName("Username");
        return request;
    }

    public static CreateUserRequest createWithoutNameRequest() {
        var request = new CreateUserRequest();
        request.setEmail("test@yandex.ru");
        request.setPassword("qwert1");
        request.setName(null);
        return request;
    }
}
