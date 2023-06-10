package ulb.infof307.g04.models;

import java.util.List;

public class StoreDeck {
    private String name;
    private List<String> tags;
    private String author;
    private int downloads;
    private int id;

    public int getId() {
        return id;
    }

    public StoreDeck(int id, String name, List<String> tags, String author, int downloads) {
        this.id = id;
        this.name = name;
        this.tags = tags;
        this.author = author;
        this.downloads = downloads;
    }

    public String getName(){
        return name;
    }

    public String getTags(){
        return String.join(",", tags);
    }

    public String getAuthor(){
        return author;
    }

    public String getDownloads(){
        return String.valueOf(downloads);
    }

}