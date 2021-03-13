package programGUI.data;

import javafx.beans.property.SimpleStringProperty;

public class ReaderData {
    private SimpleStringProperty  password;
    private SimpleStringProperty address;
    private SimpleStringProperty  fio;

    public ReaderData(String pas, String home, String fname){
        this.password = new SimpleStringProperty(pas);
        this.address = new SimpleStringProperty(home);
        this.fio = new SimpleStringProperty(fname);
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public void setFio(String fio) {
        this.fio.set(fio);
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public String getAddress() {
        return address.get();
    }

    public SimpleStringProperty addressProperty() {
        return address;
    }

    public String getFio() {
        return fio.get();
    }

    public SimpleStringProperty fioProperty() {
        return fio;
    }
}
