package ru.krista.nbaranov.news76;

public class News {
    private String title;
    private String date;
    private String link;
    private String id;

    public String getTitle() {
        return this.title;
    }
    public String getLink() {
        return this.link;
    }
    public String getDate() {
        return this.date;
    }
    public String getId() {
        return this.id;
    }

    public void setTitle(String _title) {
        this.title = _title;
    }
    public void setLink(String _link) {
        this.link = _link;
    }
    public void setDate(String _date) {
        this.date = _date;
    }
    public void setId(String _id) {
        this.date = _id;
    }

    @Override
    public String toString() {
        return this.getTitle();
    }
}
