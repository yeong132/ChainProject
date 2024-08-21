package org.zerock.chain.service;

import java.nio.charset.StandardCharsets;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.chain.dto.MessageDTO;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.*;
import com.google.api.services.gmail.model.ModifyMessageRequest;


@Service
@Log4j2
public class GmailService {
    private final Gmail service;

    public GmailService(Gmail service) {
        this.service = service;
    }

    // GmailService 생성자: Gmail API 서비스 객체를 초기화시킴.
    public GmailService() throws Exception {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();   // HTTP 전송 객체 생성
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();  // JSON 처리 객체 생성
        // Gmail 서비스 객체 생성
        service = new Gmail.Builder(httpTransport, jsonFactory, getCredentials(httpTransport, jsonFactory))
                .setApplicationName("CHAIN")  // 애플리케이션 이름 설정
                .build();
    }

    // getCredentials 메서드: OAuth2 인증을 수행하여 자격 증명을 얻음.
    private static Credential getCredentials(final NetHttpTransport httpTransport, GsonFactory jsonFactory)
            throws Exception {
        // 클라이언트 비밀정보를 로드
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
                new InputStreamReader(GmailService.class.getResourceAsStream("/credentials.json")));
        // 인증 흐름을 설정
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, Set.of(GmailScopes.GMAIL_SEND, GmailScopes.GMAIL_READONLY)) // 필요한 권한 범위 설정
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile())) // 인증 토큰을 저장할 위치 설정
                .setAccessType("offline") // 오프라인 액세스를 허용하여 새로고침 토큰을 얻음.
                .build();

        // 로컬 서버 리시버를 설정.
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        // 인증 프로세스를 시작하고 사용자 자격 증명을 반환.
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    // getGmailService 메서드: Gmail 서비스 객체를 반환
    public Gmail getGmailService() {
        return service;
    }

    // sendMail 메서드: 지정된 수신자에게 이메일을 보내기.
    public void sendMail(String recipientEmail, String subject, String messageText) throws Exception {
        log.info("Sending email to: {}", recipientEmail);
        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage email = new MimeMessage(session);

            // 이메일의 발신자, 수신자, 제목, 내용을 설정.
            email.setFrom(new InternetAddress("your-email@gmail.com"));
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipientEmail));
            email.setSubject(subject);
            email.setText(messageText);

            // 이메일을 바이트 배열로 변환한 후 Base64로 인코딩.
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            String encodedEmail = Base64.encodeBase64URLSafeString(buffer.toByteArray());

            // Gmail API를 사용하여 이메일을 전송.
            Message message = new Message();
            message.setRaw(encodedEmail);

            service.users().messages().send("me", message).execute();  // "me"는 인증된 사용자를 나타냄.
            log.info("Email sent successfully to: {}", recipientEmail);
        } catch (MessagingException e) {
            log.error("Failed to create email message: {}", e.getMessage());
            throw new Exception("Failed to create email message", e);
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
            throw new Exception("Failed to send email", e);
        }
    }

    public Optional<String> getHeader(List<MessagePartHeader> headers, String name) {
        return headers.stream()
                .filter(header -> header.getName().equalsIgnoreCase(name))
                .map(MessagePartHeader::getValue)
                .findFirst();
    }


    // 메시지 리스트를 가져오는 메서드
    public List<MessageDTO> listMessages(String userId) throws IOException {
        List<Message> messages = service.users().messages().list(userId).execute().getMessages();
        List<MessageDTO> messageDTOList = new ArrayList<>();

        for (Message message : messages) {
            Message fullMessage = service.users().messages().get(userId, message.getId()).execute();
            MessagePart payload = fullMessage.getPayload();

            if (payload != null) {
                List<MessagePartHeader> headers = payload.getHeaders();

                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setId(message.getId());
                messageDTO.setFrom(getHeader(headers, "From").orElse("Unknown"));
                messageDTO.setSubject(getHeader(headers, "Subject").orElse("No Subject"));
                messageDTO.setDate(getHeader(headers, "Date").orElse("Unknown Date"));

                boolean isStarred = fullMessage.getLabelIds() != null && fullMessage.getLabelIds().contains("STARRED");
                messageDTO.setStarred(isStarred);

                messageDTOList.add(messageDTO);
            }
        }
        return messageDTOList;
    }

    // 특정 메시지를 가져오는 메서드
    public Message getMessage(String userId, String messageId) throws IOException {
        return service.users().messages().get(userId, messageId).execute();
    }

    // 메시지 본문을 가져오는 메서드
    public String getMessageContent(String userId, String messageId) throws IOException {
        Message message = getMessage(userId, messageId);
        MessagePart payload = message.getPayload();
        StringBuilder body = new StringBuilder();

        if (payload != null) {
            List<MessagePart> parts = payload.getParts();
            if (parts != null) {
                for (MessagePart part : parts) {
                    if (part.getMimeType().equals("text/plain")) {
                        body.append(new String(Base64.decodeBase64(part.getBody().getData()), StandardCharsets.UTF_8));
                    } else if (part.getMimeType().equals("text/html")) {
                        body.append(new String(Base64.decodeBase64(part.getBody().getData()), StandardCharsets.UTF_8));
                    }
                }
            } else if (payload.getBody().getData() != null) {
                body.append(new String(Base64.decodeBase64(payload.getBody().getData()), StandardCharsets.UTF_8));
            }
        }

        return body.toString();
    }


    // createLabel 메서드: 라벨을 생성
    public String createLabel(String userId, String labelName) throws IOException {
        // 새로운 라벨 객체를 생성하고 이름을 설정.
        Label label = new Label().setName(labelName);

        //  라벨을 생성
        Label createdLabel = service.users().labels().create(userId, label).execute();

        // 생성된 라벨의 ID를 반환.
        return createdLabel.getId();
    }

    //listSentMessages 메서드 : 내가 보낸 메일목록을 보여주는 메서드
    public List<MessageDTO> listSentMessages(String userId) throws IOException {
        List<Message> messages = service.users().messages().list(userId)
                .setLabelIds(Collections.singletonList("SENT"))  // SENT 라벨이 있는 메시지 필터링
                .execute()
                .getMessages();

        List<MessageDTO> messageDTOList = new ArrayList<>();

        for (Message message : messages) {
            Message fullMessage = service.users().messages().get(userId, message.getId()).execute();
            MessagePart payload = fullMessage.getPayload();

            if (payload != null) {
                List<MessagePartHeader> headers = payload.getHeaders();

                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setId(message.getId());
                messageDTO.setFrom(getHeader(headers, "From").orElse("Unknown"));
                messageDTO.setTo(getHeader(headers, "To").orElse("Unknown"));  // 수신자 추가
                messageDTO.setSubject(getHeader(headers, "Subject").orElse("No Subject"));
                messageDTO.setDate(getHeader(headers, "Date").orElse("Unknown Date"));

                boolean isStarred = fullMessage.getLabelIds() != null && fullMessage.getLabelIds().contains("STARRED");
                messageDTO.setStarred(isStarred);

                // 디버깅용 로그 출력
                log.info("MessageDTO created: {}", messageDTO);

                messageDTOList.add(messageDTO);
            }
        }
        return messageDTOList;
    }

    // 휴지통으로 이동하는 메서드 추가
    public void moveToTrash(String userId, String messageId) throws IOException {
        ModifyMessageRequest mods = new ModifyMessageRequest().setRemoveLabelIds(List.of("INBOX")).setAddLabelIds(List.of("TRASH"));
        service.users().messages().modify(userId, messageId, mods).execute();
    }

    // 휴지통의 메시지 목록을 가져오는 메서드
    public List<MessageDTO> listTrashMessages(String userId) throws IOException {
        List<Message> messages = service.users().messages().list(userId)
                .setLabelIds(Collections.singletonList("TRASH"))
                .execute().getMessages();

        List<MessageDTO> messageDTOList = new ArrayList<>();

        for (Message message : messages) {
            Message fullMessage = service.users().messages().get(userId, message.getId()).execute();
            MessagePart payload = fullMessage.getPayload();

            if (payload != null) {
                List<MessagePartHeader> headers = payload.getHeaders();

                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setId(message.getId());
                messageDTO.setFrom(getHeader(headers, "From").orElse("Unknown"));
                messageDTO.setTo(getHeader(headers, "To").orElse("Unknown"));
                messageDTO.setSubject(getHeader(headers, "Subject").orElse("No Subject"));
                messageDTO.setDate(getHeader(headers, "Date").orElse("Unknown Date"));

                boolean isStarred = fullMessage.getLabelIds() != null && fullMessage.getLabelIds().contains("STARRED");
                messageDTO.setStarred(isStarred);

                messageDTOList.add(messageDTO);
            }
        }
        return messageDTOList;
    }

}