package programGUI.database;

import javafx.scene.control.TableView;
import programGUI.BookMain;
import programGUI.data.BookData;
import programGUI.data.ReaderData;
import programGUI.data.RegisterData;

import java.sql.*;

public class JDBCManager {
    static final String DATABASE_URL = "jdbc:mysql://localhost/datajava";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    static final String USER = "root";
    static final String PASSWORD = "10874<_A_+_!MyS!@@$!^-%7@!pASsW@$!+-*-@";

    static final String sqlCreateBook = "CREATE TABLE Book (" +
            "Register_Number BIGINT PRIMARY KEY NOT NULL," +
            "Name_Book NVARCHAR(80) NULL," +
            "Count_Pages INT NULL," +
            "Year_Publishing INT NULL," +
            "Section NVARCHAR(150) NULL" +
            ");";

    static final String sqlCreateReader = "CREATE TABLE Reader (" +
            "Password_Data BIGINT PRIMARY KEY NOT NULL," +
            "Home_Address NVARCHAR(80) NULL," +
            "Full_Name NVARCHAR(50) NULL" +
            ");";
    static final String sqlCreateRegister = "CREATE TABLE Register(" +
            "ID INT PRIMARY KEY AUTO_INCREMENT NOT NULL," +
            "Register_Number BIGINT NULL," +
            "Password_Data BIGINT NULL," +
            "Date_Issue DATE NULL," +
            "Date_Return DATE NULL," +
            "FOREIGN KEY (Register_Number) REFERENCES Book (Register_Number)," +
            "FOREIGN KEY (Password_Data) REFERENCES Reader (Password_Data));";

    public static void CreateTable(String nameTable) throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        String sqlCommand = "";
        if(nameTable.equals("Reader")){
            sqlCommand = sqlCreateReader;
        }else if(nameTable.equals("Book")){
            sqlCommand = sqlCreateBook;
        }else if(nameTable.equals("Register")){
            sqlCommand = sqlCreateRegister;
        }else{
            return;
        }

        Statement stat = null;
        Connection con = null;

        try{
            con = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
            stat = con.createStatement();
            stat.executeUpdate(sqlCommand);
        } catch (Exception e) { } finally{
            if(stat != null){
                stat.close();
            }
            if(con != null){
                con.close();
            }
        }
    }

    public static boolean WriteDataToDB_Register(TableView<RegisterData> table) throws ClassNotFoundException {
        if((table == null) || (table.getItems().size() == 0)){
            return false;
        }
        Class.forName(JDBC_DRIVER);

        try{
            try(Connection con = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD)){
                for(int i = 0; i < table.getItems().size(); i++){
                    Statement stat = null;
                    try{
                        stat = con.createStatement();
                        stat.executeUpdate("INSERT INTO Register(Register_Number, Password_Data, Date_Issue" +
                                ", Date_Return) VALUES ("
                                + table.getItems().get(i).getRegister() + ", "
                                + table.getItems().get(i).getPassword() + ", "
                                + "DATE \'" + table.getItems().get(i).getDateIssue() + "\', "
                                + "DATE \'" + table.getItems().get(i).getDateReturn() + "\'"
                                + ");");
                    }catch (Exception e){}
                    finally {
                        if(stat != null)
                            stat.close();
                    }
                }
            } catch (SQLException e) {
                return false;
            }
        }catch (Exception e){
            return false;
        }

        return true;
    }

    public static TableView ReadDataFromDB_Register(boolean del) throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        TableView<RegisterData> table = new TableView<>();

        Connection con = null;
        Statement stat = null;
        ResultSet resultSet = null;

        try{
            con = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
            stat = con.createStatement();
            resultSet = stat.executeQuery("SELECT * FROM Register;");
            while(resultSet.next()){
                table.getItems().add(new RegisterData(
                        String.valueOf(resultSet.getLong(2)),
                        String.valueOf(resultSet.getLong(3)),
                        resultSet.getDate(4).toString(),
                        resultSet.getDate(5).toString())
                );
            }

            if(del){
                Statement delStat = con.createStatement();
                delStat.executeUpdate("TRUNCATE TABLE Register;");
                delStat.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if(con != null)
                con.close();
            if(stat != null)
                stat.close();
            if(resultSet != null)
                resultSet.close();
        }

        return table;
    }

    public static boolean WriteDataToDB_Book(TableView<BookData> table) throws ClassNotFoundException {
        if((table == null) || (table.getItems().size() == 0)){
            return false;
        }
        Class.forName(JDBC_DRIVER);

        try{
            try(Connection con = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD)){
                for(int i = 0; i < table.getItems().size(); i++){
                    Statement stat = null;
                    try{
                        stat = con.createStatement();
                        stat.executeUpdate("INSERT INTO Book(Register_Number, Name_Book, Count_Pages" +
                                ", Year_Publishing, Section) VALUES ("
                                + table.getItems().get(i).getRegister() + ", \'"
                                + table.getItems().get(i).getName() + "\', "
                                + table.getItems().get(i).getPages() + ","
                                + table.getItems().get(i).getYear() + ", \'"
                                + table.getItems().get(i).getSection() + "\'" +
                                ");");
                    }catch (Exception e){}
                    finally {
                        if(stat != null)
                            stat.close();
                    }
                }
            } catch (SQLException e) {
                return false;
            }
        }catch (Exception e){
            return false;
        }

        return true;
    }

    public static boolean WriteDataToDB_Reader(TableView<ReaderData> table) throws ClassNotFoundException {
        if((table == null) || (table.getItems().size() == 0)){
            return false;
        }
        Class.forName(JDBC_DRIVER);

        try{
            try(Connection con = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD)){
                for(int i = 0; i < table.getItems().size(); i++){
                    Statement stat = null;
                    try{
                        stat = con.createStatement();
                        stat.executeUpdate("INSERT INTO Reader(Password_Data, Home_Address, Full_Name) VALUES ("
                                + table.getItems().get(i).getPassword() + ", \'"
                                + table.getItems().get(i).getAddress() + "\', \'"
                                + table.getItems().get(i).getFio() + "\');");
                    }catch (Exception e){}
                    finally {
                        if(stat != null)
                            stat.close();
                    }
                }
            } catch (SQLException e) {
                return false;
            }
        }catch (Exception e){
            return false;
        }

        return true;
    }

    public static TableView ReadDataFromDB_Book(boolean del) throws SQLException, ClassNotFoundException {
        Class.forName(JDBC_DRIVER);
        TableView<BookData> table = new TableView<>();

        Connection con = null;
        Statement stat = null;
        ResultSet resultSet = null;

        try{
            con = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
            stat = con.createStatement();
            resultSet = stat.executeQuery("SELECT * FROM Book;");
            while(resultSet.next()){
                table.getItems().add(new BookData(
                        String.valueOf(resultSet.getLong(1)),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getInt(4),
                        resultSet.getString(5))
                );
            }

            if(del){
                Statement delStat = con.createStatement();
                delStat.executeUpdate("TRUNCATE TABLE Book;");
                delStat.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if(con != null)
                con.close();
            if(stat != null)
                stat.close();
            if(resultSet != null)
                resultSet.close();
        }

        return table;
    }

    public static TableView ReadDataFromDB_Reader(boolean del) throws SQLException, ClassNotFoundException {
        Class.forName(JDBC_DRIVER);
        TableView<ReaderData> table = new TableView<>();

        Connection con = null;
        Statement stat = null;
        ResultSet resultSet = null;

        try{
            con = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
            stat = con.createStatement();
            resultSet = stat.executeQuery("SELECT * FROM Reader;");
            while(resultSet.next()){
                table.getItems().add(new ReaderData(
                        String.valueOf(resultSet.getLong(1)),
                        resultSet.getString(2),
                        resultSet.getString(3)
                ));
            }

            if(del){
                Statement delStat = con.createStatement();
                delStat.executeUpdate("TRUNCATE TABLE Reader;");
                delStat.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if(con != null)
                con.close();
            if(stat != null)
                stat.close();
            if(resultSet != null)
                resultSet.close();
        }

        return table;
    }
}
