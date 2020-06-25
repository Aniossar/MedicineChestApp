import java.text.SimpleDateFormat;
import java.util.Date;

public class DrugItem {

    private int id;
    private String name;
    private String type;
    private Date expiryDate;

    public DrugItem(){

    }
    public DrugItem(String name, String type, Date expiryDate){
        this.name = name;
        this.type = type;
        this.expiryDate = expiryDate;
    }

    public DrugItem(int id, String name, String type, Date expiryDate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.expiryDate = expiryDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy");
        return String.format("Лекарство: %s; тип: %s; годен до: %s", this.getName(), this.getType(), simpleDateFormat.format(this.getExpiryDate()));
    }
}
