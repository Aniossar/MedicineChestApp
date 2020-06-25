import java.io.IOException;
import java.util.Properties;

public class AppProperties {

    private static AppProperties instance = null;
    private Properties properties;

    protected AppProperties() throws IOException {
        properties = new Properties();
        properties.load(getClass().getResourceAsStream("data.properties"));
    }

    public static AppProperties getInstance(){
        if(instance == null){
            try {
                instance = new AppProperties();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public String getValue(String key){
        return properties.getProperty(key);
    }
}
