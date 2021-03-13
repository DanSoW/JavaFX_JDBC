package programGUI.data;

import javafx.beans.property.SimpleStringProperty;

public class RegisterData {
    private SimpleStringProperty register;
    private SimpleStringProperty password;
    private SimpleStringProperty dateIssue;
    private SimpleStringProperty dateReturn;

    public RegisterData(String reg, String pas, String dateIssue, String dateReturn){
        this.register = new SimpleStringProperty(reg);
        this.password = new SimpleStringProperty(pas);
        this.dateIssue = new SimpleStringProperty(dateIssue);
        this.dateReturn = new SimpleStringProperty(dateReturn);
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
}
