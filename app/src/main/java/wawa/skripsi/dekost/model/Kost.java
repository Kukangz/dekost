package wawa.skripsi.dekost.model;

import org.json.JSONObject;

public class Kost {
    private String title, thumbnailUrl, last_update, room, description;
    private String id = null;
    private JSONObject allresult;


    public Kost() {
    }

    public Kost(String name, String thumbnailUrl, String year, String room,
                String description, String id) {
        this.title = name;
        this.thumbnailUrl = thumbnailUrl;
        this.last_update = year;
        this.room = room;
        this.description = description;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItem() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public JSONObject getAllresult() {
        return allresult;
    }

    public void setAllresult(JSONObject allresult) {
        this.allresult = allresult;
    }
}