package programGUI;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.AccessibleRole;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import programGUI.data.BookData;
import programGUI.data.ReaderData;
import programGUI.data.RegisterData;
import programGUI.database.JDBCManager;

import java.awt.print.Book;
import java.io.IOException;
import java.util.ArrayList;

public class ReaderMain extends Application {

    public static int MAX_LENGTH_PASSWORD = 10;

    TableView<ReaderData> _table = null;
    private TextField _passwordData = null;
    private TextField _homeAddress = null;
    private TextField _fio = null;
    private Button _addData = null;
    private Button _refactor = null;
    private Button _delete = null;
    private Button _inputDB = null;
    private Button _outputDB = null;
    private MenuBar _menuBar = null;

    //Экземпляры окон
    private BookMain _bookMain = null;
    private RegisterMain _registerMain = null;

    private Stage thisStage = null;

    public static boolean checkTextFields(ArrayList<TextField> fields){
        if(fields.size() == 0)
            return false;
        for(int i = 0; i < fields.size(); i++)
            if(fields.get(i).getText().toString().length() == 0)
                return false;
        return true;
    }

    public static boolean passwordDataValidate(String password, int maxLength){
        if(password.length() != maxLength)
            return false;
        for(int i = 0; i < password.length(); i++)
            if(!Character.isDigit(password.charAt(i)))
                return false;
        return true;
    }

    public static void MessageShow(Alert.AlertType type, String title, String message){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private boolean checkInputData(){
        ArrayList<TextField> fields = new ArrayList<TextField>();
        fields.add(_passwordData);
        fields.add(_homeAddress);
        fields.add(_fio);

        if(!checkTextFields(fields)){
            MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: не все поля для ввода данных заполнены!");
            return false;
        }else if(!passwordDataValidate(_passwordData.getText().toString(), MAX_LENGTH_PASSWORD)){
            MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: не корректно введены паспортные данные!");
            return false;
        }else if(isPasswordExist(fields.get(0).getText().toString())){
            MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: данные паспортные данные уже присутствуют в таблице!");
            return false;
        }

        return true;
    }

    private boolean isPasswordExist(String password){
        if((_table == null) || (_table.getItems().size() == 0))
            return false;
        for(int i = 0; i < _table.getItems().size(); i++){
            if(_table.getItems().get(i).getPassword().equals(password))
                return true;
        }
        return false;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        JDBCManager.CreateTable("Reader");
        Parent root = FXMLLoader.load(getClass().getResource("markup/viewReader.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Читатели");

        //Определение взаимосвязей
        this.thisStage = primaryStage;
        _bookMain = new BookMain();
        _registerMain = new RegisterMain();
        BookMain.readerStage = thisStage;
        BookMain.registerStage = _registerMain.GetStage();
        RegisterMain.readerStage = thisStage;
        RegisterMain.bookStage = _bookMain.GetStage();

        _passwordData = (TextField) scene.lookup("#_txtPasswordData");
        _homeAddress = (TextField) scene.lookup("#_txtHomeAddress");
        _fio = (TextField) scene.lookup("#_txtFio");
        _addData = (Button) scene.lookup("#_btnAddData");
        _refactor = (Button) scene.lookup("#_btnRefactor");
        _delete = (Button) scene.lookup("#_btnDelete");
        _inputDB = (Button) scene.lookup("#_btnInputDB");
        _outputDB = (Button) scene.lookup("#_btnOutputDB");
        _menuBar = (MenuBar) scene.lookup("#_menuBar");
        _table = (TableView) scene.lookup("#_tableReader");
        _table.getColumns().clear();

        _menuBar.getMenus().clear();

        Menu menu = (new Menu("Переход"));
        MenuItem item = new MenuItem("Книги");
        MenuItem reg = new MenuItem("Регистрация");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                thisStage.hide();
                _bookMain.Show();
            }
        });

        reg.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                thisStage.hide();
                _registerMain.Show();
            }
        });

        menu.getItems().add(item);
        menu.getItems().add(reg);

        _menuBar.getMenus().add(0, menu);


        TableColumn<ReaderData, String> attrib = new TableColumn<ReaderData, String>("Паспортные данные");
        attrib.setCellValueFactory(new PropertyValueFactory<ReaderData, String>("password"));
        _table.getColumns().add(attrib);

        attrib = new TableColumn<ReaderData, String>("Домашний адрес");
        attrib.setCellValueFactory(new PropertyValueFactory<ReaderData, String>("address"));
        _table.getColumns().add(attrib);

        attrib = new TableColumn<ReaderData, String>("ФИО");
        attrib.setCellValueFactory(new PropertyValueFactory<ReaderData, String>("fio"));
        _table.getColumns().add(attrib);

        _passwordData.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if((oldValue.length() == MAX_LENGTH_PASSWORD)
                && (newValue.length() > MAX_LENGTH_PASSWORD)){
                    _passwordData.setText(oldValue);
                    return;
                }

                if ((!newValue.matches("\\d*")) ) {
                    _passwordData.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        _delete.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try{
                    int row = _table.getSelectionModel().getSelectedIndex();
                    _table.getItems().remove(row);
                }catch (Exception e){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: строка таблицы для удаления не выделена!");
                }
            }
        });

        _refactor.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!checkInputData())
                    return;

                try{
                    int row = _table.getSelectionModel().getSelectedIndex();
                    _table.getItems().set(row, new ReaderData(
                            _passwordData.getText().toString(),
                            _homeAddress.getText().toString(),
                            _fio.getText().toString()));
                }catch (Exception e){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: строка таблицы для изменения не выделена!");
                }
            }
        });

        _addData.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!checkInputData())
                    return;

                _table.getItems().add(new ReaderData(_passwordData.getText().toString(),
                        _homeAddress.getText().toString(),
                        _fio.getText().toString()));
            }
        });

        _inputDB.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if((_table == null) || (_table.getItems().size() == 0)){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: нет данных для записи!");
                    return;
                }
                try {
                    JDBCManager.WriteDataToDB_Reader(_table);
                    MessageShow(Alert.AlertType.INFORMATION, "Информация", "Данные записаны в базу данных!");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        _outputDB.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    TableView<ReaderData> reader = JDBCManager.ReadDataFromDB_Reader(false);
                    _table.getItems().clear();
                    for(int i = 0; i < reader.getItems().size(); i++)
                        _table.getItems().add(reader.getItems().get(i));
                } catch (Exception e) {}
                MessageShow(Alert.AlertType.INFORMATION, "Информация", "Данные считаны из базы данных!");
            }
        });

        _table.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if((event.getButton() == MouseButton.SECONDARY) && (_table.getItems().size() > 0)){
                    int row = _table.getSelectionModel().getSelectedIndex();
                    ReaderData data = _table.getItems().get(row);
                    _passwordData.setText(data.getPassword());
                    _homeAddress.setText(data.getAddress());
                    _fio.setText(data.getFio());
                }
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
