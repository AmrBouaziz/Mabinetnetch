package tn.rnu.issatso.fia2.gl.mabinetnech;

/**
 * Created by amrou on 20/05/15.
 */
public class Comment {

    int id;
    int user_id;
    int post_id;
    String content;
    String c_date;
    String c_time;

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

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getC_date() {
        return c_date;
    }

    public void setC_date(String c_date) {
        this.c_date = c_date;
    }

    public String getC_time() {
        return c_time;
    }

    public void setC_time(String c_time) {
        this.c_time = c_time;
    }

    public Comment() {
    }

    public Comment(int id, int user_id, int post_id, String content, String c_date, String c_time) {
        this.id = id;
        this.user_id = user_id;
        this.post_id = post_id;
        this.content = content;
        this.c_date = c_date;
        this.c_time = c_time;
    }
}
