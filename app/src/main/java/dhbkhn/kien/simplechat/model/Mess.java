package dhbkhn.kien.simplechat.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kiend on 6/20/2017.
 */

public class Mess {
    private String author;
    private String text;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Mess(String author, String text) {
        this.author = author;
        this.text = text;
    }

    public Mess() {
    }

    public Map<String,Object> toMap(){
        HashMap<String, Object> results = new HashMap<>();
        results.put("author", author);
        results.put("text", text);
        return results;
    }
}
