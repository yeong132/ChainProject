package org.zerock.chain.Service;

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

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;


@Service  // Spring의 서비스 클래스임을 나타냅니다.
@Log4j2   // Log4j2를 사용한 로깅을 지원합니다.
public class GmailService {
    private final Gmail service;

    // GmailService 생성자: Gmail API 서비스 객체를 초기화합니다.
    public GmailService() throws Exception {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();   // HTTP 전송 객체 생성
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();  // JSON 처리 객체 생성
        // Gmail 서비스 객체 생성
        service = new Gmail.Builder(httpTransport, jsonFactory, getCredentials(httpTransport, jsonFactory))
                .setApplicationName("Your Application Name")  // 애플리케이션 이름 설정
                .build();
    }

    // getCredentials 메서드: OAuth2 인증을 수행하여 자격 증명을 얻습니다.
    private static Credential getCredentials(final NetHttpTransport httpTransport, GsonFactory jsonFactory)
            throws Exception {
        // 클라이언트 비밀정보를 로드합니다.
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
                new InputStreamReader(GmailService.class.getResourceAsStream("/credentials.json")));
        // 인증 흐름을 설정합니다.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, Set.of(GmailScopes.GMAIL_SEND, GmailScopes.GMAIL_READONLY)) // 필요한 권한 범위 설정
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile())) // 인증 토큰을 저장할 위치 설정
                .setAccessType("offline") // 오프라인 액세스를 허용하여 새로고침 토큰을 얻습니다.
                .build();

        // 로컬 서버 리시버를 설정합니다.
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        // 인증 프로세스를 시작하고 사용자 자격 증명을 반환합니다.
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    // sendMail 메서드: 지정된 수신자에게 이메일을 보냅니다.
    public void sendMail(String recipientEmail, String subject, String messageText) throws Exception {
        log.info("Sending email to: {}", recipientEmail);
        try {
            Properties props = new Properties(); // 이메일 속성 설정
            Session session = Session.getDefaultInstance(props, null);  // 이메일 세션 생성
            MimeMessage email = new MimeMessage(session); // 이메일 메시지 객체 생성

            // 이메일의 발신자, 수신자, 제목, 내용을 설정합니다.
            email.setFrom(new InternetAddress("your-email@gmail.com"));
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipientEmail));
            email.setSubject(subject);
            email.setText(messageText);

            // 이메일을 바이트 배열로 변환한 후 Base64로 인코딩합니다.
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            String encodedEmail = Base64.encodeBase64URLSafeString(buffer.toByteArray());

            // Gmail API를 사용하여 이메일을 전송합니다.
            Message message = new Message();
            message.setRaw(encodedEmail);

            service.users().messages().send("me", message).execute();  // "me"는 인증된 사용자를 나타냅니다.
            log.info("Email sent successfully to: {}", recipientEmail);
        } catch (MessagingException e) {
            log.error("Failed to create email message: {}", e.getMessage());
            throw new Exception("Failed to create email message", e);
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
            throw new Exception("Failed to send email", e);
        }
    }

       // listMessages 메서드: 사용자의 메일함에 있는 모든 메시지를 가져옵니다.
    public List<Message> listMessages(String userId) throws IOException {
        ListMessagesResponse response = service.users().messages().list(userId).execute();
        List<Message> messages = new ArrayList<>();

        // 페이지 단위로 메시지를 가져옵니다.
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages()); // 현재 페이지의 메시지를 목록에 추가합니다.
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken(); // 다음 페이지 토큰을 가져옵니다.
                response = service.users().messages().list(userId).setPageToken(pageToken).execute();
            } else {
                break;  // 더 이상 페이지가 없으면 종료합니다.
            }
        }
        return messages;
    }

    // getMessage 메서드: 특정 메시지 ID에 해당하는 메시지를 가져옵니다.
    public Message getMessage(String userId, String messageId) throws IOException {
        // Gmail API를 사용하여 메시지를 가져옵니다.
        Message message = service.users().messages().get(userId, messageId).execute();
        // 필요한 경우, message.getPayload() 등을 이용해 추가 정보를 추출할 수 있습니다.
        return message;
    }

    // createLabel 메서드: Gmail API를 사용하여 라벨을 생성합니다.
    public String createLabel(String userId, String labelName) throws IOException {
        // 새로운 라벨 객체를 생성하고 이름을 설정합니다.
        Label label = new Label().setName(labelName);

        // Gmail API를 사용하여 라벨을 생성합니다.
        Label createdLabel = service.users().labels().create(userId, label).execute();

        // 생성된 라벨의 ID를 반환합니다.
        return createdLabel.getId();
    }
}