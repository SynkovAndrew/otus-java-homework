import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SerializationUtilsTest {
    private Gson gson;

    @BeforeEach
    public void setUp() {
        this.gson = new Gson();

    }

    @Test
    public void testToJson() {
        final Data data = new Data();
        final String json = SerializationUtils.toJson(data);

        Assertions.assertEquals(gson.toJson(data), json);
    }
}
