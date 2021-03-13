package programGUI.data;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class InfoReaderData {
    private SimpleStringProperty fio;
    private SimpleStringProperty register;
    private SimpleStringProperty password;
    private SimpleStringProperty dateIssue;
    private SimpleIntegerProperty countBook;

    public InfoReaderData(String fio, String reg, String pas, String dateIssue, String dateReturn, int count){
        this.fio = new SimpleStringProperty(fio);
        this.register = new SimpleStringProperty(reg);
        this.password = new SimpleStringProperty(pas);
        this.dateIssue = new SimpleStringProperty(dateIssue);
        this.dateReturn = new SimpleStringProperty(dateReturn);
        this.countBook = new SimpleIntegerProperty(count);
    }

    public void setFio(String fio) {
        this.fio.set(fio);
    }

    public void setRegister(String register) {
        this.register.set(register);
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public void setDateIssue(String dateIssue) {
        this.dateIssue.set(dateIssue);
    }

    public void setDateReturn(String dateReturn) {
        this.dateReturn.set(dateReturn);
    }

    public void setCountBook(int countBook) {
        this.countBook.set(countBook);
    }

    private SimpleStringProperty dateReturn;

    public String getFio() {
        return fio.get();
    }

    public SimpleStringProperty fioProperty() {
        return fio;
    }

    public String getRegister() {
        return register.get();
    }

    public SimpleStringProperty registerProperty() {
        return register;
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public String getDateIssue() {
        return dateIssue.get();
    }

    public SimpleStringProperty dateIssueProperty() {
        return dateIssue;
    }

    public String getDateReturn() {
        return dateReturn.get();
    }

    public SimpleStringProperty dateReturnProperty() {
        return dateReturn;
    }

    public int getCountBook() {
        return countBook.get();
    }

    public SimpleIntegerProperty countBookProperty() {
        return countBook;
    }

}
