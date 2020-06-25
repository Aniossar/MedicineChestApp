import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static Statement statement;
    public static SimpleDateFormat inputDateLine;
    public static SimpleDateFormat sqlDateLine;

    public static void main(String[] args) {
        DBconnection dBconnection = new DBconnection();
        inputDateLine = new SimpleDateFormat("MM.yyyy");
        sqlDateLine = new SimpleDateFormat("yyyy-MM-dd");

        try {
            statement = dBconnection.getConnection().createStatement();
            showInterface();
            statement.close();
            dBconnection.closeConnection();
        } catch (SQLException e) {
            System.out.println("--Ошибка при обращении к базе");
        } catch (IOException e) {
            System.out.println("--Ошибка ввода");
        } catch (ParseException e) {
            System.out.println("--Ошибка ввода даты");
        } finally {
            dBconnection.closeConnection();
        }
    }

    public static void showInterface() throws IOException, SQLException, ParseException {
        String menuItem;
        while (true) {
            System.out.println("1 - добавить новое лекарство");
            System.out.println("2 - удалить лекарство");
            System.out.println("3 - показать все лекарства");
            System.out.println("4 - показать просроченные лекарства");
            System.out.println("5 - найти лекарство");
            System.out.println("6 - выйти");
            System.out.println("------");

            BufferedReader xx = new BufferedReader(new InputStreamReader(System.in));
            menuItem = xx.readLine();
            if (menuItem.equals("1")) {
                while (true) {
                    drugAdd();
                    System.out.println("Добавить еще одно? y/n");
                    if (xx.readLine().equals("n")) break;
                }
            } else if (menuItem.equals("2")) {
                while (true) {
                    drugRemove();
                    System.out.println("Удалить еще одно? y/n");
                    if (xx.readLine().equals("n")) break;
                }
            } else if (menuItem.equals("3")) {
                drugListShow();
            } else if (menuItem.equals("4")) {
                drugSpoiledShow();
            } else if (menuItem.equals("5")) {
                while (true) {
                    System.out.println("Введите название лекарства для поиска:");
                    for (Map.Entry<Integer, DrugItem> drugFound : drugFind(xx.readLine()).entrySet()) {
                        System.out.println(drugFound.getKey() + ". " + drugFound.getValue());
                    }
                    System.out.println("Найти еще? y/n");
                    if (xx.readLine().equals("n")) break;
                }
            } else return;
        }
    }

    public static void drugAdd() throws IOException, SQLException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        DrugItem drugItem = new DrugItem();

        System.out.println("Введите название лекарства:");
        drugItem.setName(reader.readLine());

        System.out.println("Введите тип лекарства:");
        String typeChoice = reader.readLine();
        drugItem.setType(typeChoice);

        System.out.println("Введите срок годности в формате ММ.ГГГГ:");
        String dateLine = reader.readLine();
        Date date = inputDateLine.parse(dateLine);
        drugItem.setExpiryDate(date);
        if (checkDrug(drugItem)) {
            String sqlLine = String.format("insert into public.drug (name, type, expiry_date) values ('%s', '%s', '%s');",
                    drugItem.getName(),
                    drugItem.getType(),
                    sqlDateLine.format(drugItem.getExpiryDate()));
            statement.execute(sqlLine);
            System.out.println("--Лекарство внесено в базу");
        }
        return;
    }

    public static boolean checkDrug(DrugItem drug) throws IOException {
        System.out.println("------");
        System.out.println(drug);
        System.out.println("Проверьте, все ли введено правильно? y/n");
        BufferedReader checkReader = new BufferedReader(new InputStreamReader(System.in));
        return (checkReader.readLine().equals("y"));
    }

    public static void drugRemove() throws SQLException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Введите название лекарства, которое нужно удалить:");
        String drugRemoved = reader.readLine();

        Map<Integer, DrugItem> drugRemoveMap = drugFind(drugRemoved);
        try {
            for (Map.Entry<Integer, DrugItem> drugFound : drugRemoveMap.entrySet()) {
                System.out.println(drugFound.getKey() + ". " + drugFound.getValue());
            }
        } catch (NullPointerException e) {
            System.out.println("Такое лекарство не найдено");
        }

        System.out.println("Введите номер лекарства, которое нужно удалить, либо 0, чтобы выйти:");
        int drugRemoveNumber = Integer.parseInt(reader.readLine());
        if (drugRemoveNumber == 0) return;
        DrugItem drugItem = drugRemoveMap.get(drugRemoveNumber);

        String sqlLine = String.format("delete from public.drug where (name = '%s' and expiry_date = '%s');",
                drugItem.getName(),
                sqlDateLine.format(drugItem.getExpiryDate()));

        statement.executeUpdate(sqlLine);
        System.out.println("--Лекарство удалено из базы");
    }

    public static void drugListShow() throws SQLException {
        String sqlLine = "select * from public.drug order by type, expiry_date";
        int i = 1;

        ResultSet resultSet = statement.executeQuery(sqlLine);
        while (resultSet.next()) {
            DrugItem drugItem = new DrugItem();
            drugItem.setName(resultSet.getString("name"));
            drugItem.setType(resultSet.getString("type"));
            drugItem.setExpiryDate(resultSet.getDate("expiry_date"));
            System.out.print(i + ". ");
            System.out.println(drugItem);
            i++;
        }
        System.out.println("------");
    }

    public static void drugSpoiledShow() throws SQLException, IOException {
        Date dateNow = new Date();
        int i = 1;
        String sqlLine = String.format("select * from public.drug where expiry_date < '%s';",
                sqlDateLine.format(dateNow));

        ResultSet resultSet = statement.executeQuery(sqlLine);
        while (resultSet.next()) {
            DrugItem drugItem = new DrugItem();
            drugItem.setName(resultSet.getString("name"));
            drugItem.setType(resultSet.getString("type"));
            drugItem.setExpiryDate(resultSet.getDate("expiry_date"));
            System.out.print(i + ". ");
            System.out.println(drugItem);
            i++;
        }
        System.out.println("------");
        System.out.println("Удалить все просроченные лекарства (при их наличии)? y/n");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        if (bufferedReader.readLine().equals("y")) {
            sqlLine = String.format("delete from public.drug where expiry_date < '%s';", sqlDateLine.format(dateNow));
            statement.execute(sqlLine);
            System.out.println("--Все просроченные лекарства удалены из базы");
            System.out.println("------");
        }
        return;
    }

    public static Map<Integer, DrugItem> drugFind(String name) throws SQLException {
        Map<Integer, DrugItem> drugFindMap = new HashMap<Integer, DrugItem>();
        int i = 1;

        String sqlLine = String.format("select * from public.drug where name = '%s';", name);
        ResultSet resultSet = statement.executeQuery(sqlLine);

        while (resultSet.next()) {
            DrugItem drugItem = new DrugItem();
            drugItem.setName(resultSet.getString("name"));
            drugItem.setType(resultSet.getString("type"));
            drugItem.setExpiryDate(resultSet.getDate("expiry_date"));
            drugFindMap.put(i, drugItem);
            i++;
        }
        if (drugFindMap.isEmpty()) System.out.println("--Ничего не было найдено");
        return drugFindMap;
    }
}
