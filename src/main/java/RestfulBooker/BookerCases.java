package RestfulBooker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;

import java.io.IOException;
import java.util.Arrays;

public class BookerCases {

    public static void ping() throws IOException {
        Response response = Booker.ping();
        Assertions.assertEquals(201, response.code());
    }

    public static void authenticationResponse() throws IOException {
        Response response = Booker.authenticate(AuthData.USERNAME, AuthData.PASSWORD);
        Assumptions.assumeTrue(response.code() == 200);

        String responseStr = response.body().string();
        ObjectNode node = new ObjectMapper().readValue(responseStr, ObjectNode.class);
        Assertions.assertTrue(node.has("token"));
    }

    public static void invalidAuthentication(String username, String password) throws IOException {
        Response response = Booker.authenticate(username, password);
        Assumptions.assumeTrue(response.code() == 200);

        String responseStr = response.body().string();
        ObjectNode node = new ObjectMapper().readValue(responseStr, ObjectNode.class);
        Assertions.assertFalse(node.has("token"));
        Assertions.assertTrue(node.has("reason"));
    }

    public static void createBookingValid() throws IOException {
        Response response = Booker.createBooking("0");
        Assertions.assertEquals(200, response.code());
    }

    public static void createBookingWrongCheckinDay() throws IOException {
        Response response = Booker.createBooking("src/test/resources/BookingWrongCheckinDay.json");
        Assertions.assertNotEquals(200, response.code());
    }

    public static void createBookingWrongCheckinMonth() throws IOException {
        Response response = Booker.createBooking("src/test/resources/BookingWrongCheckinMonth.json");
        Assertions.assertNotEquals(200, response.code());
    }

    public static void createBookingWrongDateFormat() throws IOException {
        Response response = Booker.createBooking("src/test/resources/BookingWrongDateFormat.json");
        Assertions.assertNotEquals(200, response.code());
    }

    public static void getBookingId(String firstname, String lastname, String checkin, String checkout) throws IOException {
        int expectedId = Booker.getId();
        String[] parameters = {firstname, lastname, checkin, checkout};
        Response response = Booker.getBookingIds(parameters);
        Assumptions.assumeTrue(response.code() == 200);

        ObjectMapper mapper = new ObjectMapper();
        String responseStr = response.body().string();
        int size = mapper.readTree(responseStr).size();
        int[] actualIds = new int[size];
        for (int i = 0; i < size; i++) {
            int actualId = mapper.readTree(responseStr).path(i).at("/bookingid").asInt();
            actualIds[i] = actualId;
        }
        Assertions.assertTrue(Arrays.stream(actualIds).anyMatch(actualId -> actualId == expectedId));
    }

    public static void getBooking() throws IOException {
        Response response = Booker.getBooking();
        Assumptions.assumeTrue(response.code() == 200);
        String responseStr = response.body().string();
        Assertions.assertTrue(responseStr.contains("John"));
        Assertions.assertTrue(responseStr.contains("Johnson"));
        Assertions.assertTrue(responseStr.contains("2021-05-06"));
        Assertions.assertTrue(responseStr.contains("2021-05-10"));
    }

    public static void updateBooking() throws IOException {
        Response response = Booker.updateBooking("token=" + Booker.getToken());
        Assumptions.assumeTrue(response.code() == 200);
        String responseStr = response.body().string();
        Assertions.assertTrue(responseStr.contains("Johnathan"));
        Assertions.assertTrue(responseStr.contains("2021-05-20"));
    }

    public static void updateBookingNoCookie() throws IOException {
        Response response = Booker.updateBooking("0");
        Assertions.assertEquals(403, response.code());
    }

    public static void updateBookingWrongCookie() throws IOException {
        Response response = Booker.updateBooking("token=WrongToken");
        Assertions.assertEquals(403, response.code());
    }

    public static void partialUpdate() throws IOException {
        Response response = Booker.partialUpdateBooking("token=" + Booker.getToken());
        Assumptions.assumeTrue(response.code() == 200);
        String responseStr = response.body().string();
        boolean firstname = responseStr.contains("Johnathan");
        boolean checkout = responseStr.contains("2021-05-20");
        Assertions.assertTrue(firstname && checkout);
    }

    public static void partialUpdateNoCookie() throws IOException {
        Response response = Booker.partialUpdateBooking("0");
        Assertions.assertEquals(403, response.code());
    }

    public static void partialUpdateWrongCookie() throws IOException {
        Response response = Booker.partialUpdateBooking("token=WrongToken");
        Assertions.assertEquals(403, response.code());
    }

    public static void deleteBookingValid() throws IOException {
        Booker.createBooking("0");
        Response response = Booker.deleteBooking("token=" + Booker.getToken());
        Assertions.assertEquals(201, response.code());
    }

    public static void deleteBookingNoCookie() throws IOException {
        Booker.createBooking("0");
        Response response = Booker.deleteBooking("0");
        System.out.println(response.code());
        Assertions.assertEquals(403, response.code());
    }

    public static void deleteBookingWrongCookie() throws IOException {
        Booker.createBooking("0");
        Response response = Booker.deleteBooking("token=WrongToken");
        System.out.println(response.code());
        Assertions.assertEquals(403, response.code());
    }
}
