package programGUI;

import com.sun.javafx.image.IntPixelGetter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.converter.DateTimeStringConverter;
import org.w3c.dom.Text;
import programGUI.data.BookData;
import programGUI.data.InfoReaderData;
import programGUI.data.ReaderData;
import programGUI.data.RegisterData;
import programGUI.database.JDBCManager;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class RegisterMain {

    private MenuBar _menuBar = null;
    private TextField _password = null;
    private TextField _register = null;
    private TextField _dateIssue = null;
    private TextField _dateReturn = null;
    private Button _addData = null;
    private Button _refactorData = null;
    private Button _deleteData = null;
    private Button _inputDB = null;
    private Button _outputDB = null;
    private TableView<RegisterData> _table = null;
    private TableView<InfoReaderData> _tableInfo = null;
    private TextField _month = null;
    private Button _execute = null;
    private Button _execute2 = null;
    private TextField _fio = null;
    private TextArea _resultExecute2 = null;

    private Stage thisStage = null;
    public static Stage readerStage = null;
    public static Stage bookStage = null;

    public void Show(){
        if(this.thisStage == null)
            return;
        this.thisStage.show();
    }

    public void Hide(){
        if(this.thisStage == null)
            return;
        this.thisStage.hide();
    }

    public Stage GetStage(){
        return this.thisStage;
    }

    private boolean checkInputData() throws ParseException {
        ArrayList<TextField> fields = new ArrayList<TextField>();
        fields.add(_register);
        fields.add(_password);
        fields.add(_dateIssue);
        fields.add(_dateReturn);

        if(!ReaderMain.checkTextFields(fields)){
            ReaderMain.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: не все поля для ввода данных заполнены!");
            return false;
        }else if(!ReaderMain.passwordDataValidate(_register.getText().toString(), BookMain.MAX_LENGTH_REGNUM)){
            ReaderMain.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: не корректно введены регистрационные данные!");
            return false;
        }else if(!ReaderMain.passwordDataValidate(_password.getText().toString(), ReaderMain.MAX_LENGTH_PASSWORD)){
            ReaderMain.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: не корректно введены паспортные данные!");
            return false;
        }else if((!isDateValidate(_dateIssue.getText().toString())) ||
                (!isDateValidate(_dateReturn.getText().toString()))){
            ReaderMain.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: дата должна быть в формате ГГГГ-ММ-ДД!");
            return false;
        }

        DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        if(format.parse(_dateIssue.getText().toString()).after(
                format.parse(_dateReturn.getText().toString())
        )){
            ReaderMain.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: дата выдачи не может быть позже даты возврата!");
            return false;
        }

        return true;
    }

    public static boolean isDateValidate(String date){
        if((date == null) || (date.length() == 0))
            return false;
        int count = 0;
        for(int i = 0; i < date.length(); i++){
            if(date.charAt(i) == '-'){
                count++;
            }else if(!Character.isDigit(date.charAt(i))){
                return false;
            }
        }
        if(count != 2)
            return false;

        String[] dateSplit = date.split("\\-");
        if((dateSplit.length != 3) || (dateSplit[0].length() != 4)
        || (dateSplit[1].length() != 2) || (dateSplit[2].length() != 2))
            return false;

        int value = Integer.valueOf(dateSplit[2]);
        if((value <= 0) || (value > 31))
            return false;
        value = Integer.valueOf(dateSplit[1]);
        if((value <= 0) || (value > 12))
            return false;

        return true;
    }

    private int CountBook(String passwordData) throws SQLException, ClassNotFoundException {
        TableView<RegisterData> registerData = JDBCManager.ReadDataFromDB_Register(false);
        if((registerData == null) || (registerData.getItems().size() <= 0))
            return 0;
        int count = 0;
        for(int i = 0; i < registerData.getItems().size(); i++)
            if(registerData.getItems().get(i).getPassword().equals(passwordData))
                count++;
        return count;
    }

    private BookData GetBookByPasswordData(String passwordData) throws SQLException, ClassNotFoundException {
        TableView<BookData> books = JDBCManager.ReadDataFromDB_Book(false);
        TableView<RegisterData> registers = JDBCManager.ReadDataFromDB_Register(false);

        if((books == null) || (registers == null)
        || (books.getItems().size() == 0) || (registers.getItems().size() == 0)){
            return null;
        }

        for(int i = 0; i < registers.getItems().size(); i++){
            if(registers.getItems().get(i).getPassword().equals(passwordData)){
                for(int j = 0; j < books.getItems().size(); j++){
                    if(registers.getItems().get(i).getRegister().equals(
                            books.getItems().get(j).getRegister()
                    )){
                        return books.getItems().get(j);
                    }
                }
            }
        }

        return null;
    }

    public RegisterMain() throws IOException, SQLException, ClassNotFoundException {
        JDBCManager.CreateTable("Register");
        Parent root = FXMLLoader.load(getClass().getResource("markup/viewRegister.fxml"));
        Scene scene = new Scene(root);
        thisStage = new Stage();
        thisStage.setScene(scene);
        thisStage.setTitle("Регистрация");

        _menuBar = (MenuBar) scene.lookup("#_menuBar");
        _addData = (Button) scene.lookup("#_btnAdd");
        _refactorData = (Button) scene.lookup("#_btnRefactor");
        _deleteData = (Button) scene.lookup("#_btnDelete");
        _register = (TextField) scene.lookup("#_txtRegNumber");
        _inputDB = (Button) scene.lookup("#_btnInputDB");
        _outputDB = (Button) scene.lookup("#_btnOutputDB");
        _password = (TextField) scene.lookup("#_txtPassword");
        _dateIssue = (TextField) scene.lookup("#_txtDateIssue");
        _dateReturn = (TextField) scene.lookup("#_txtDateReturn");
        _table = (TableView) scene.lookup("#_table");
        _tableInfo = (TableView) scene.lookup("#_table2");
        _execute = (Button) scene.lookup("#_btnExecute1");
        _month = (TextField) scene.lookup("#_txtMonth");
        _execute2 = (Button) scene.lookup("#_btnExecute2");
        _fio = (TextField) scene.lookup("#_txtFio");
        _resultExecute2 = (TextArea) scene.lookup("#_txtBooks");
        _table.getColumns().clear();
        _tableInfo.getColumns().clear();
        _menuBar.getMenus().clear();

        Menu menu = new Menu("Переход");
        MenuItem reader = new MenuItem("Читатели");
        MenuItem book = new MenuItem("Книги");

        reader.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                thisStage.hide();
                readerStage.show();
            }
        });

        book.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                thisStage.hide();
                bookStage.show();
            }
        });

        menu.getItems().add(reader);
        menu.getItems().add(book);
        _menuBar.getMenus().add(0, menu);

        TableColumn<RegisterData, String> attrib = new TableColumn<RegisterData, String>("Регистрационный номер");
        attrib.setCellValueFactory(new PropertyValueFactory<RegisterData, String>("register"));
        _table.getColumns().add(attrib);

        attrib = new TableColumn<RegisterData, String>("Паспортные данные");
        attrib.setCellValueFactory(new PropertyValueFactory<RegisterData, String>("password"));
        _table.getColumns().add(attrib);

        attrib = new TableColumn<RegisterData, String>("Дата выдачи");
        attrib.setCellValueFactory(new PropertyValueFactory<RegisterData, String>("dateIssue"));
        _table.getColumns().add(attrib);

        attrib = new TableColumn<RegisterData, String>("Дата возврата");
        attrib.setCellValueFactory(new PropertyValueFactory<RegisterData, String>("dateReturn"));
        _table.getColumns().add(attrib);

        TableColumn<InfoReaderData, String> attribInfo = new TableColumn<InfoReaderData, String>("ФИО");
        attribInfo.setCellValueFactory(new PropertyValueFactory<InfoReaderData, String>("fio"));
        _tableInfo.getColumns().add(attribInfo);

        attribInfo = new TableColumn<InfoReaderData, String>("Регистрационный номер");
        attribInfo.setCellValueFactory(new PropertyValueFactory<InfoReaderData, String>("register"));
        _tableInfo.getColumns().add(attribInfo);

        attribInfo = new TableColumn<InfoReaderData, String>("Паспортные данные");
        attribInfo.setCellValueFactory(new PropertyValueFactory<InfoReaderData, String>("password"));
        _tableInfo.getColumns().add(attribInfo);

        attribInfo = new TableColumn<InfoReaderData, String>("Дата выдачи");
        attribInfo.setCellValueFactory(new PropertyValueFactory<InfoReaderData, String>("dateIssue"));
        _tableInfo.getColumns().add(attribInfo);

        attribInfo = new TableColumn<InfoReaderData, String>("Дата возврата");
        attribInfo.setCellValueFactory(new PropertyValueFactory<InfoReaderData, String>("dateReturn"));
        _tableInfo.getColumns().add(attribInfo);

        TableColumn<InfoReaderData, Integer> attribInfoCount = new TableColumn<>("Количество взятых книг");
        attribInfoCount.setCellValueFactory(new PropertyValueFactory<InfoReaderData, Integer>("countBook"));
        _tableInfo.getColumns().add(attribInfoCount);

        _month.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if ((!newValue.matches("\\d*")) ) {
                    _month.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        _fio.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if((oldValue.length() == ReaderMain.MAX_LENGTH_PASSWORD)
                        && (newValue.length() > ReaderMain.MAX_LENGTH_PASSWORD)){
                    _fio.setText(oldValue);
                    return;
                }
                if ((!newValue.matches("\\d*")) ) {
                    _fio.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        _execute.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                _tableInfo.getItems().clear();
                TableView<RegisterData> registerData = null;
                try {
                    registerData = JDBCManager.ReadDataFromDB_Register(false);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                TableView<ReaderData> readerData = null;
                try {
                    readerData = JDBCManager.ReadDataFromDB_Reader(false);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if(readerData.getItems().size() <= 0){
                    ReaderMain.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: нет зарегистрированных данных о взятии и возврате книг!");
                    return;
                }

                for(int i = 0; i < registerData.getItems().size(); i++){
                    if(((Integer.valueOf(registerData.getItems().get(i).getDateReturn().split("\\-")[1])
                    - Integer.valueOf(registerData.getItems().get(i).getDateIssue().split("\\-")[1])) >
                    Integer.valueOf(_month.getText())) ||
                            ((Integer.valueOf(registerData.getItems().get(i).getDateReturn().split("\\-")[0])
                            - Integer.valueOf(registerData.getItems().get(i).getDateIssue().split("\\-")[0])) >
                            (Integer.valueOf(_month.getText()) / 12))){
                        int index = (-1);
                        for(int j = 0; j < readerData.getItems().size(); j++){
                            if(readerData.getItems().get(j).getPassword().equals(
                                    registerData.getItems().get(i).getPassword()
                            )){
                                index = j;
                                break;
                            }
                        }

                        if(index >= 0){
                            try {
                                _tableInfo.getItems().add(new InfoReaderData(
                                        readerData.getItems().get(index).getFio(),
                                        registerData.getItems().get(i).getRegister(),
                                        registerData.getItems().get(i).getPassword(),
                                        registerData.getItems().get(i).getDateIssue(),
                                        registerData.getItems().get(i).getDateReturn(),
                                        CountBook(registerData.getItems().get(i).getPassword())
                                ));
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        _register.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if((oldValue.length() == BookMain.MAX_LENGTH_REGNUM)
                        && (newValue.length() > BookMain.MAX_LENGTH_REGNUM)){
                    _register.setText(oldValue);
                    return;
                }

                if ((!newValue.matches("\\d*")) ) {
                    _register.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        _password.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if((oldValue.length() == ReaderMain.MAX_LENGTH_PASSWORD)
                        && (newValue.length() > ReaderMain.MAX_LENGTH_PASSWORD)){
                    _password.setText(oldValue);
                    return;
                }

                if ((!newValue.matches("\\d*")) ) {
                    _password.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        _addData.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    if(!checkInputData())
                        return;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                _table.getItems().add(new RegisterData(_register.getText().toString(),
                        _password.getText().toString(),
                        _dateIssue.getText().toString(),
                        _dateReturn.getText().toString()));
            }
        });

        _deleteData.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try{
                    int row = _table.getSelectionModel().getSelectedIndex();
                    _table.getItems().remove(row);
                }catch (Exception e){
                    ReaderMain.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: строка таблицы для удаления не выделена!");
                }
            }
        });

        _table.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if((event.getButton() == MouseButton.SECONDARY) && (_table.getItems().size() > 0)){
                    int row = _table.getSelectionModel().getSelectedIndex();
                    RegisterData data = _table.getItems().get(row);
                    _password.setText(data.getPassword());
                    _register.setText(data.getRegister());
                    _dateIssue.setText(data.getDateIssue());
                    _dateReturn.setText(data.getDateReturn());
                }
            }
        });

        _refactorData.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    if(!checkInputData())
                        return;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try{
                    int row = _table.getSelectionModel().getSelectedIndex();
                    _table.getItems().set(row, new RegisterData(_register.getText().toString(),
                            _password.getText().toString(),
                            _dateIssue.getText().toString(),
                            _dateReturn.getText().toString()));
                }catch (Exception e){
                    ReaderMain.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: строка таблицы для изменения не выделена!");
                }
            }
        });

        _inputDB.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if((_table == null) || (_table.getItems().size() == 0)){
                    ReaderMain.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: нет данных для записи!");
                    return;
                }
                try {
                    JDBCManager.WriteDataToDB_Register(_table);
                    ReaderMain.MessageShow(Alert.AlertType.INFORMATION, "Информация", "Данные записаны в базу данных!");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                _table.getItems().clear();
            }
        });

        _outputDB.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    TableView<RegisterData> book = JDBCManager.ReadDataFromDB_Register(true);
                    _table.getItems().clear();
                    for(int i = 0; i < book.getItems().size(); i++)
                        _table.getItems().add(book.getItems().get(i));
                } catch (Exception e) {}
                ReaderMain.MessageShow(Alert.AlertType.INFORMATION, "Информация", "Данные считаны из базы данных!");
            }
        });

        _execute2.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                _resultExecute2.clear();
                if(_fio.getText().toString().length() != ReaderMain.MAX_LENGTH_PASSWORD){
                    ReaderMain.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Некорректные паспортные данные читателя!");
                    return;
                }

                TableView<RegisterData> registers = null;
                try {
                    registers = JDBCManager.ReadDataFromDB_Register(false);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if(registers.getItems().size() <= 0){
                    ReaderMain.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Нет данных о регистрации книг за читателями!");
                    return;
                }

                ArrayList<RegisterData> data = new ArrayList<>();
                for(int i = 0; i < registers.getItems().size(); i++){
                    if(registers.getItems().get(i).getPassword().equals(_fio.getText().toString())){
                        data.add(registers.getItems().get(i));
                    }
                }

                data.sort(new Comparator<RegisterData>() {
                    @Override
                    public int compare(RegisterData o1, RegisterData o2) {
                        DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                        try {
                            if(format.parse(o1.getDateIssue()).after(
                                    format.parse(o1.getDateReturn().toString())
                            )){
                                return 1;
                            }else if(format.parse(o1.getDateIssue()).before(
                                    format.parse(o1.getDateReturn().toString())
                            )){
                                return (-1);
                            }else{
                                return 0;
                            }
                        } catch (ParseException e) {
                            return 0;
                        }
                    }
                });

                for(int i = 0; i < data.size(); i++){
                    try {
                        BookData bookData = GetBookByPasswordData(data.get(i).getPassword());
                        if(bookData != null){
                            _resultExecute2.setText(_resultExecute2.getText().toString() + "\n" +
                                    bookData.getName() + ", " + data.get(i).getDateIssue());
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
