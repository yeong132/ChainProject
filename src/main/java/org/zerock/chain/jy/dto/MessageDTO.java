package org.zerock.chain.jy.dto;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDTO {
    private String id;        // messageId
    private String draftId;   // draftId
    private String from;
    private String to;
    private String subject;
    private String date;
    private boolean starred;
    private String body;      // email body
    private List<String> attachments; // 첨부파일 리스트 추가
    private boolean isRead;   // 읽음 상태를 나타내는 필드 추가

    // Getter and Setter for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and Setter for draftId
    public String getDraftId() {
        return draftId;
    }

    public void setDraftId(String draftId) {
        this.draftId = draftId;
    }

    // Getter and Setter for from
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    // Getter and Setter for to
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    // Getter and Setter for subject
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    // Getter and Setter for date
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Getter and Setter for starred
    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    // Getter and Setter for body
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    // Getter and Setter for attachments
    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    // Getter and Setter for isRead
    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    // Convert Gmail Message to MessageDTO
    public static MessageDTO fromMessage(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());

        MessagePart payload = message.getPayload();
        if (payload != null) {
            List<MessagePartHeader> headers = payload.getHeaders();
            dto.setFrom(getHeader(headers, "From").orElse("Unknown"));
            dto.setTo(getHeader(headers, "To").orElse("Unknown"));
            dto.setSubject(getHeader(headers, "Subject").orElse("No Subject"));
            dto.setDate(getHeader(headers, "Date").orElse("Unknown Date"));

            // Add logic to set body and attachments if needed
        }

        dto.setStarred(message.getLabelIds() != null && message.getLabelIds().contains("STARRED"));
        dto.setRead(!message.getLabelIds().contains("UNREAD")); // UNREAD 라벨이 없으면 읽음 상태로 표시
        return dto;
    }

    // Convert MessageDTO back to Gmail Message
    public Message toMessage() {
        Message message = new Message();
        message.setId(this.getId());
        // You can add more fields to the message if necessary
        return message;
    }

    private static Optional<String> getHeader(List<MessagePartHeader> headers, String name) {
        return headers.stream()
                .filter(header -> header.getName().equalsIgnoreCase(name))
                .map(MessagePartHeader::getValue)
                .findFirst();
    }
}
