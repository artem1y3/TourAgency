import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.sfedu.touragency.api.DataProviderCsv;
import ru.sfedu.touragency.api.DataProviderXml;
import ru.sfedu.touragency.model.Hotel;
import ru.sfedu.touragency.model.ModelType;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class DataProviderXmlTest {

    private static DataProviderXml hotelDataProvider = new DataProviderXml(ModelType.HOTEL);

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
