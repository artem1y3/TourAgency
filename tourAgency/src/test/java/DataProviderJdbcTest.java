import org.junit.Test;
import ru.sfedu.touragency.api.DataProviderJdbc;
import ru.sfedu.touragency.model.Hotel;
import ru.sfedu.touragency.model.ModelType;

import static org.junit.Assert.assertEquals;

public class DataProviderJdbcTest {
    private static DataProviderJdbc hotelDataProvider = new DataProviderJdbc(ModelType.HOTEL);

    private Hotel hotel = makeHotel();

    private static Hotel makeHotel(){
        Hotel hotel = new Hotel();
        hotel.setId(1);
        hotel.setName("Verona");
        hotel.setDescription("The best hotel of my life");
        hotel.setRate(5);
        return hotel;
    }

    @Test
    public void a_save(){
        long id = hotelDataProvider.save(hotel);
        hotel.setId(id);
    }

    @Test
    public void b_getAll(){
        assertEquals(hotel, hotelDataProvider.getAll().get(0));
    }

}
