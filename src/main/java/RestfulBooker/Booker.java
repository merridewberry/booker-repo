package RestfulBooker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;

public class Booker {

    private static final MediaType JSON = MediaType.get("application/json");
    private static final String BASE_HOST = "restful-booker.herokuapp.com";


    private static HttpUrl.Builder getUrl() {
        return new HttpUrl.Builder()
                .scheme("HTTPS")
                .host(BASE_HOST);
    }

    private static Request.Builder getRequest(HttpUrl url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json");
    }

    private static Response executeResponse(Request.Builder request) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request.build()).execute();
        return response;
    }

    public static Response authenticate(String username, String password) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode authBody = mapper.createObjectNode();
        authBody.put("username", username);
        authBody.put("password", password);
        RequestBody body = RequestBody.create(authBody.toString(), JSON);
        Request.Builder request = getRequest(getUrl().addPathSegment("auth").build());
        Response response = executeResponse(request.post(body));
        return response;
    }

    public static Response ping() throws IOException {
        Request.Builder request = getRequest(getUrl().addPathSegment("ping").build());
        Response response = executeResponse(request);
        return response;
    }

    public static Response getBookingIds(String[] parameters) throws IOException {
        HttpUrl.Builder url = getUrl().addPathSegment("booking");
        String[] fields = {"firstname", "lastname", "checkin", "checkout"};
        for (String parameter: parameters) {
            if (parameter != null) {
                url.addQueryParameter(fields[ArrayUtils.indexOf(parameters, parameter)], parameter);
            }
        }
        Request.Builder request = getRequest(url.build());
        Response response = executeResponse(request);
        return response;
    }

    public static Response createBooking(String file) throws IOException {
        File bookingBody;
        if (file.equals("0")) {
            bookingBody = new File ("src/test/resources/BookingJohn.json");
        } else {
            bookingBody = new File (file);
        }
        RequestBody body = RequestBody.create(bookingBody, JSON);
        Request.Builder request = getRequest(getUrl().addPathSegment("booking").build());
        Response response = executeResponse(request.addHeader("Accept", "application/json").post(body));
        return response;
    }

    public static int getId() throws IOException {
        Response response = createBooking("0");
        String json = response.body().string();
        ObjectMapper mapper = new ObjectMapper();
        int id = mapper.readTree(json).at("/bookingid").asInt();
        return id;
    }

    public static Response deleteBooking (String token) throws IOException {
        String[] parameters = {"John", "Johnson", "2021-05-06", "2021-05-10"};
        ObjectMapper mapper = new ObjectMapper();
        Response response = getBookingIds(parameters);
        String responseStr = response.body().string();
        int size = mapper.readTree(responseStr).size();
        for (int i = 0; i < size; i++) {
            int id = mapper.readTree(responseStr).path(i).at("/bookingid").asInt();
            Request.Builder bookingRequest = getRequest(getUrl().addPathSegment("booking")
                    .addPathSegment(String.valueOf(id)).build());
            if (token.equals("0")){
                response = executeResponse(bookingRequest.delete());
            } else {
                response = executeResponse(bookingRequest.addHeader("Cookie", token).delete());}
        }
        return response;
    }

    public static String getToken() throws IOException {
        String response = authenticate(AuthData.USERNAME, AuthData.PASSWORD).body().string();
        ObjectMapper mapper = new ObjectMapper();
        String token = mapper.readTree(response).at("/token").asText();
        return token;
    }

    public static Response getBooking() throws IOException {
        Request.Builder request = getRequest(getUrl().addPathSegment("booking")
                .addPathSegment(String.valueOf(getId())).build())
                .addHeader("Accept", "application/json");
        Response response = executeResponse(request);
        return response;
    }

    public static Response updateBooking(String token) throws IOException {
        File bookingBody = new File ("src/test/resources/BookingJohnUpdated.json");
        RequestBody body = RequestBody.create(bookingBody, JSON);
        Request.Builder request = getRequest(getUrl().addPathSegment("booking")
                .addPathSegment(String.valueOf(getId())).build())
                .addHeader("Accept", "application/json");
        if (token.equals("0")) {
            request.put(body);
        } else {
            request.addHeader("Cookie", token)
                    .put(body);
        }
        Response response = executeResponse(request);
        return response;
    }

    public static Response partialUpdateBooking(String token) throws IOException {
        File bookingBody = new File ("src/test/resources/BookingJohnPartiallyUpdated.json");
        RequestBody body = RequestBody.create(bookingBody, JSON);
        Request.Builder request = getRequest(getUrl().addPathSegment("booking")
                .addPathSegment(String.valueOf(getId())).build())
                .addHeader("Accept", "application/json");
        if (token.equals("0")) {
            request.patch(body);
        } else {
            request.addHeader("Cookie", token)
                    .patch(body);
        }
        Response response = executeResponse(request);
        return response;
    }




}
