package programGUI;

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
import programGUI.data.BookData;
import programGUI.data.ReaderData;
import programGUI.data.RegisterData;
import programGUI.database.JDBCManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class BookMain {

    public static final int MAX_LENGTH_REGNUM = 10;

    private MenuBar _menuBar = null;
    private TextField _regNumber = null;
    private TextField _nameBook = null;
    private TextField _countPages = null;
    private TextField _yearPublishing = null;
    private TextField _section = null;
    private Button _addData = null;
    private Button _refactorData = null;
    private Button _deleteData = null;
    private Button _inputDB = null;
    private Button _outputDB = null;
    private TableView<BookData> _table = null;

    private Stage thisStage = null;
    public static Stage readerStage = null;
    public static Stage registerStage = null;

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

    private boolean checkInputData(){
        ArrayList<TextField> fields = new ArrayList<TextField>();
        fields.add(_regNumber);
        fields.add(_nameBook);
        fields.add(_countPages);
        fields.add(_yearPublishing);
        fields.add(_section);

        if(!ReaderMain.checkTextFields(fields)){
            ReaderMain.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: не все поля для ввода данных заполнены!");
            return false;
        }else if(!ReaderMain.passwordDataValidate(_regNumber.getText().toString(), MAX_LENGTH_REGNUM)){
            ReaderMain.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: не корректно введены регистрационные данные!");
            return false;
        }else if(isPasswordExist(fields.get(0).getText().toString())){
            ReaderMain.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Ошибка: данные регистрационные данные уже присутствуют в таблице!");
            return false;
        }

        return true;
    }

    private boolean isPasswordExist(String password){
        if((_table == null) || (_table.getItems().size() == 0))
            return false;
        for(int i = 0; i < _table.getItems().size(); i++){
            if(_table.getItems().get(i).getRegister().equals(password))
                return true;
        }
        return false;
    }

    public BookMain() throws IOException, SQLException, ClassNotFoundException {
        JDBCManager.CreateTable("Book");
        Parent root = FXMLLoader.load(getClass().getResource("markup/viewBook.fxml"));
        Scene scene = new Scene(root);
        thisStage = new Stage();
        thisStage.setScene(scene);
        thisStage.setTitle("Книги");

        _menuBar = (MenuBar) scene.lookup("#_menuBar");
        _addData = (Button) scene.lookup("#_btnAdd");
        _refactorData = (Button) scene.lookup("#_btnRefactor");
        _deleteData = (Button) scene.lookup("#_btnDelete");
        _regNumber = (TextField) scene.lookup("#_txtRegNumber");
        _nameBook = (TextField) scene.lookup("#_txtNameBook");
        _yearPublishing = (TextField) scene.lookup("#_txtYearPublishing");
        _countPages = (TextField) scene.lookup("#_txtCountPages");
        _section = (TextField) scene.lookup("#_txtSection");
        _inputDB = (Button) scene.lookup("#_btnInputDB");
        _outputDB = (Button) scene.lookup("#_btnOutputDB");
        _table = (TableView) scene.lookup("#_table");
        _table.getColumns().clear();
        _menuBar.getMenus().clear();
        Menu menu = new Menu("Переход");
        MenuItem reader = new MenuItem("Читатели");
        MenuItem register = new MenuItem("Регистрация");

        reader.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                thisStage.hide();
                readerStage.show();
            }
        });

        register.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                thisStage.hide();
                registerStage.show();
            }
        });

        menu.getItems().add(reader);
        menu.getItems().add(register);
        _menuBar.getMenus().add(0, menu);

        TableColumn<BookData, String> attrib = new TableColumn<BookData, String>("Регистрационный номер");
        attrib.setCellValueFactory(new PropertyValueFactory<BookData, String>("register"));
        _table.getColumns().add(attrib);

        attrib = new TableColumn<BookData, String>("Название книги");
        attrib.setCellValueFactory(new PropertyValueFactory<BookData, String>("name"));
        _table.getColumns().add(attrib);

        TableColumn<BookData, Integer> attrib1 = new TableColumn<BookData, Integer>("Количество страниц");
        attrib1.setCellValueFactory(new PropertyValueFactory<BookData, Integer>("pages"));
        _table.getColumns().add(attrib1);

        attrib1 = new TableColumn<BookData, Integer>("Год публикации");
        attrib1.setCellValueFactory(new PropertyValueFactory<BookData, Integer>("year"));
        _table.getColumns().add(attrib1);

        attrib = new TableColumn<BookData, String>("Раздел");
        attrib.setCellValueFactory(new PropertyValueFactory<BookData, String>("section"));
        _table.getColumns().add(attrib);

        _regNumber.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if((oldValue.length() == MAX_LENGTH_REGNUM)
                        && (newValue.length() > MAX_LENGTH_REGNUM)){
                    _regNumber.setText(oldValue);
                    return;
                }

                if ((!newValue.matches("\\d*")) ) {
                    _regNumber.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        _yearPublishing.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if ((!newValue.matches("\\d*")) ) {
                    _yearPublishing.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        _countPages.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if ((!newValue.matches("\\d*")) ) {
                    _countPages.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        _addData.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!checkInputData())
                    return;
                _table.getItems().add(new BookData(_regNumber.getText().toString(),
                        _nameBook.getText().toString(),
                        Integer.valueOf(_countPages.getText().toString()),
                        Integer.valueOf(_yearPublishing.getText().toString()),
                        _section.getText().toString()));
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

        _refactorData.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!checkInputData())
                    return;

                try{
                    int row = _table.getSelectionModel().getSelectedIndex();
                    _table.getItems().set(row, new BookData(_regNumber.getText().toString(),
                            _nameBook.getText().toString(),
                            Integer.valueOf(_countPages.getText().toString()),
                            Integer.valueOf(_yearPublishing.getText().toString()),
                            _section.getText().toString()));
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
                    JDBCManager.WriteDataToDB_Book(_table);
                    ReaderMain.MessageShow(Alert.AlertType.INFORMATION, "Информация", "Данные записаны в базу данных!");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        _outputDB.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    TableView<BookData> book = JDBCManager.ReadDataFromDB_Book(false);
                    _table.getItems().clear();
                    for(int i = 0; i < book.getItems().size(); i++)
                        _table.getItems().add(book.getItems().get(i));
                } catch (Exception e) {}
                ReaderMain.MessageShow(Alert.AlertType.INFORMATION, "Информация", "Данные считаны из базы данных!");
            }
        });

        _table.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if((event.getButton() == MouseButton.SECONDARY) && (_table.getItems().size() > 0)){
                    int row = _table.getSelectionModel().getSelectedIndex();
                    BookData data = _table.getItems().get(row);
                    _regNumber.setText(data.getRegister());
                    _nameBook.setText(data.getName());
                    _countPages.setText(String.valueOf(data.getPages()));
                    _yearPublishing.setText(String.valueOf(data.getYear()));
                    _section.setText(data.getSection());
                }
            }
        });
    }
}
