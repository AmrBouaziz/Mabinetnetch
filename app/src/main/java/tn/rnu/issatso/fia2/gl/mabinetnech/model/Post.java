package tn.rnu.issatso.fia2.gl.mabinetnech.model;

import java.io.Serializable;

/**
 * Created by amrou on 16/05/15.
 */
public class Post implements Serializable{

    int id;
    int user_id;
    int category_id;
    String content;
    String p_date;
    String p_time;
    String username;

    public Post() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getP_date() {
        return p_date;
    }

    public void setP_date(String p_date) {
        this.p_date = p_date;
    }

    public String getP_time() {
        return p_time;
    }

    public void setP_time(String p_time) {
        this.p_time = p_time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Post(int id, int user_id, int category_id, String content, String p_date, String p_time, String username) {
        this.id = id;
        this.user_id = user_id;
        this.category_id = category_id;
        this.content = content;
        this.p_date = p_date;
        this.p_time = p_time;
        this.username = username;
    }
}
