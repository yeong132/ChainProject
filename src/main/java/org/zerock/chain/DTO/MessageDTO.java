package org.zerock.chain.dto;

public class MessageDTO {
    private String id;
    private String from;
    private String to;
    private String subject;
    private String date;
    private boolean starred;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {  // To 필드를 위한 getter 메서드
        return to;
    }

    public void setTo(String to) {  // To 필드를 위한 setter 메서드
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isStarred() {  // starred 필드를 위한 getter 메서드
        return starred;
    }

    public void setStarred(boolean starred) {  // starred 필드를 위한 setter 메서드
        this.starred = starred;
    }
}
