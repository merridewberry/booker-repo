package RestfullBookerTest;

import RestfulBooker.Booker;
import RestfulBooker.BookerCases;
import com.sun.org.glassfish.gmbal.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.IOException;

public class BookerTest {

    @BeforeEach
    public void deleteAllTheJohns() throws IOException {
        Booker.deleteBooking("token=" + Booker.getToken());
    }

    @Test
    @Description("Testing if anything is working at all")
    public void pingTest() throws IOException {
        BookerCases.ping();
    }

    @Test
    @Description("Testing authentication with default login and password")
    public void authenticationResponseTest() throws IOException {
        BookerCases.authenticationResponse();
    }

//Not sure if failed authentication should give code 200, but, well, here it is.
    @ParameterizedTest
    @CsvFileSource(resources = "/InvalidAuthData.csv")
    @Description("Testing authentication with invalid login and/or password")
    public void invalidAuthenticationTest(String username, String password) throws IOException {
        BookerCases.invalidAuthentication(username, password);
    }

    @Test
    @Description("Testing booking creation")
    public void createBookingTest() throws IOException {
        BookerCases.createBookingValid();
    }

    @Test
    @Description("Testing booking creation with check in day later than check out day")
    public void createBookingWrongCheckinDayTest() throws IOException {
        BookerCases.createBookingWrongCheckinDay();
    }

    @Test
    @Description("Testing booking creation with check in month later than check out month")
    public void createBookingWrongCheckinMonthTest() throws IOException {
        BookerCases.createBookingWrongCheckinMonth();
    }

    @Test
    @Description("Testing booking creation with no actual dates")
    public void createBookingWrongDateFormat() throws IOException {
        BookerCases.createBookingWrongDateFormat();
    }

/*Looks like there is some bug in API, and although documentation says that searching by check-in date returns
* all the results with same or greater value, in fact it only returns results with greater value. So I made check-in
* date in json greater than in all the search queries. */
    @ParameterizedTest
    @CsvFileSource(resources = "/BookingIdsQuery.csv")
    @Description("Testing if the id of searched booking is present in the search results")
    public void getBookingIdTest(String firstname, String lastname, String checkin, String checkout) throws IOException {
        BookerCases.getBookingId(firstname, lastname,checkin, checkout);
    }

    @Test
    @Description("Testing if searching by booking id gives correct result")
    public void getBookingTest() throws IOException {
        BookerCases.getBooking();
    }

/*I thought about writing tests with wrong dates for full and partial update, but they obviously are going to fail,
because it looks like restful booker utilizes dates as a string, so I decided not to bother.*/

    @Test
    @Description("Testing booking update")
    public void updateBookingTest() throws IOException {
        BookerCases.updateBooking();
    }

    @Test
    @Description("Testing booking update with no cookie")
    public void updateBookingNoCookieTest() throws IOException {
        BookerCases.updateBookingNoCookie();
    }

    @Test
    @Description("Testing booking update with wrong cookie")
    public void updateBookingWrongCookieTest() throws IOException {
        BookerCases.updateBookingWrongCookie();
    }

    @Test
    @Description("Testing partial booking update")
    public void partialUpdateTest() throws IOException {
        BookerCases.partialUpdate();
    }

    @Test
    @Description("Testing partial booking update with no cookie")
    public void partialUpdateNoCookieTest() throws IOException {
        BookerCases.partialUpdateNoCookie();
    }

    @Test
    @Description("Testing partial booking update with wrong cookie")
    public void partialUpdateWrongCookieTest() throws IOException {
        BookerCases.partialUpdateWrongCookie();
    }

    @Test
    @Description("Testing booking deletion")
    public void deleteBookingTest() throws IOException {
        BookerCases.deleteBookingValid();
    }

    @Test
    @Description("Testing booking deletion with no cookie")
    public void deleteBookingNoCookieTest() throws IOException {
        BookerCases.deleteBookingNoCookie();
    }

    @Test
    @Description("Testing booking deletion with wrong cookie")
    public void deleteBookingWrongCookieTest() throws IOException {
        BookerCases.deleteBookingWrongCookie();
    }
}
