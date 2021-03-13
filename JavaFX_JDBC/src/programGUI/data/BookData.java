package programGUI.data;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class BookData {
    private SimpleStringProperty register;
    private SimpleStringProperty name;
    private SimpleIntegerProperty pages;
    private SimpleIntegerProperty year;
    private SimpleStringProperty section;

    public BookData(String reg, String name, int pages, int year, String section){
        this.register = new SimpleStringProperty(reg);
        this.name = new SimpleStringProperty(name);
        this.pages = new SimpleIntegerProperty(pages);
        this.year = new SimpleIntegerProperty(year);
        this.section = new SimpleStringProperty(section);
    }

    public String getRegister() {
        return register.get();
    }

    public SimpleStringProperty registerProperty() {
        return register;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public int getPages() {
        return pages.get();
    }

    public SimpleIntegerProperty pagesProperty() {
        return pages;
    }

    public int getYear() {
        return year.get();
    }

    public SimpleIntegerProperty yearProperty() {
        return year;
    }

    public String getSection() {
        return section.get();
    }

    public SimpleStringProperty sectionProperty() {
        return section;
    }

    public void setRegister(String register) {
        this.register.set(register);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setPages(int pages) {
        this.pages.set(pages);
    }

    public void setYear(int year) {
        this.year.set(year);
    }

    public void setSection(String section) {
        this.section.set(section);
    }
}
