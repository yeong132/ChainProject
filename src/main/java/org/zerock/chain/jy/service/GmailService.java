package org.zerock.chain.jy.service;

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
import org.zerock.chain.jy.dto.MessageDTO;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



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
                httpTransport, jsonFactory, clientSecrets, Set.of(
                GmailScopes.GMAIL_SEND,
                GmailScopes.GMAIL_READONLY,
                GmailScopes.GMAIL_MODIFY,
                GmailScopes.GMAIL_COMPOSE,
                GmailScopes.GMAIL_INSERT,
                "https://mail.google.com/")) // 모든 범위 설정
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

    public void sendMail(String recipientEmail, String subject, String messageText, List<String> filePaths) throws Exception {
        log.info("Sending email to: {}", recipientEmail);
        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage email = new MimeMessage(session);

            // 이메일의 발신자, 수신자, 제목, 내용을 설정.
            email.setFrom(new InternetAddress("your-email@gmail.com"));
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipientEmail));
            email.setSubject(subject);

            // 본문과 파일을 담을 Multipart 객체 생성
            Multipart multipart = new MimeMultipart();

            // 본문 설정
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(messageText);
            multipart.addBodyPart(textPart);

            // 첨부파일 추가
            if (filePaths != null && !filePaths.isEmpty()) {
                for (String filePath : filePaths) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(filePath);
                    attachmentPart.setDataHandler(new DataHandler(source));
                    attachmentPart.setFileName(Paths.get(filePath).getFileName().toString());
                    multipart.addBodyPart(attachmentPart);
                }
            }

            // 이메일의 본문을 multipart로 설정
            email.setContent(multipart);

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

    // 이메일 메시지를 가져오는 메서드
// 주어진 사용자 ID와 메시지 ID를 사용해 Gmail API에서 이메일 메시지를 가져옵니다.
    public Message getMessage(String userId, String messageId) throws IOException {
        return service.users().messages().get(userId, messageId).execute();
    }

    // 이메일 메시지의 본문 내용을 가져오는 메서드
// 이메일 메시지를 가져와서 그 안의 HTML 본문을 추출한 후, 이미지 CID를 실제 이미지 경로로 변환합니다.
    public String getMessageContent(String userId, String messageId) throws IOException {
        Message message = getMessage(userId, messageId); // 메시지를 가져옴
        MessagePart payload = message.getPayload(); // 메시지의 페이로드를 가져옴
        StringBuilder body = new StringBuilder(); // 본문을 저장할 StringBuilder 초기화

        if (payload != null) {
            String htmlContent = getHtmlContent(payload); // HTML 콘텐츠를 추출
            if (htmlContent != null) {
                body.append(htmlContent); // HTML 내용을 본문에 추가
            } else {
                log.warn("이메일 메시지에서 HTML 내용을 찾을 수 없습니다."); // HTML 본문이 없을 경우 경고 로그
            }
        }

        // 이미지 CID 변환 작업 수행
        String finalContent = body.toString(); // 본문을 문자열로 변환
        Map<String, String> cidMap = extractCidMap(payload); // CID와 이미지 경로의 매핑 추출
        finalContent = parseEmailContent(finalContent, cidMap); // CID를 실제 경로로 변환

        return finalContent; // 변환된 최종 본문 반환
    }

    // MIME 파트를 재귀적으로 처리하여 HTML 콘텐츠를 추출하는 메서드
// 주어진 메시지 파트를 검사하여, HTML 콘텐츠를 추출합니다.
    private String getHtmlContent(MessagePart part) throws IOException {
        String mimeType = part.getMimeType(); // MIME 타입을 가져옴
        MessagePartBody partBody = part.getBody(); // MIME 파트의 본문을 가져옴

        // MIME 타입이 text/html인 경우, 그 데이터를 디코딩하여 반환
        if (mimeType.equals("text/html")) {
            if (partBody != null && partBody.getData() != null) {
                return new String(Base64.decodeBase64(partBody.getData()), StandardCharsets.UTF_8);
            } else {
                log.warn("HTML 파트에 데이터가 없습니다."); // 데이터가 없을 경우 경고 로그
            }
        } else if (mimeType.startsWith("multipart/")) {
            return extractHtmlFromMultipart(part); // multipart/ 관련 MIME 타입의 경우, 재귀적으로 HTML 콘텐츠를 추출
        }
        return null; // HTML 콘텐츠를 찾지 못한 경우 null 반환
    }

    // Multipart 내에서 HTML 콘텐츠를 추출하는 메서드
// 주어진 multipart 파트를 재귀적으로 처리하여 HTML 콘텐츠를 추출합니다.
    private String extractHtmlFromMultipart(MessagePart part) throws IOException {
        if (part.getParts() != null) {
            for (MessagePart subPart : part.getParts()) {
                String result = getHtmlContent(subPart); // 각 서브 파트를 검사하여 HTML 콘텐츠를 추출
                if (result != null) {
                    return result; // HTML 콘텐츠를 찾으면 반환
                }
            }
        } else {
            log.warn("Multipart 파트에 서브 파트가 없습니다."); // 서브 파트가 없을 경우 경고 로그
        }
        return null; // HTML 콘텐츠를 찾지 못한 경우 null 반환
    }

    // CID와 이미지 경로의 매핑을 추출하는 메서드
// 메시지 페이로드를 검사하여, 이미지 CID와 그에 대응하는 경로를 매핑한 맵을 생성합니다.
    private Map<String, String> extractCidMap(MessagePart payload) throws IOException {
        Map<String, String> cidMap = new HashMap<>();

        if (payload != null) {
            log.info("CID 추출을 위한 Payload 처리 중...");
            processCidMap(payload, cidMap); // 페이로드를 처리하여 CID와 이미지 경로를 매핑
            log.info("CID 추출 완료: {}", cidMap); // 완료된 CID 맵을 로그로 출력
        } else {
            log.warn("Payload가 null이므로 CID 추출을 건너뜀."); // 페이로드가 null일 경우 경고 로그
        }

        return cidMap; // CID 맵 반환
    }

    // CID와 이미지 경로의 매핑을 처리하는 메서드
// 주어진 메시지 파트를 검사하여, CID와 이미지 경로를 매핑합니다.
    private void processCidMap(MessagePart part, Map<String, String> cidMap) throws IOException {
        String mimeType = part.getMimeType(); // MIME 타입을 가져옴
        MessagePartBody partBody = part.getBody(); // MIME 파트의 본문을 가져옴

        log.info("MIME 타입 처리 중: {}", mimeType);

        if (mimeType.startsWith("image/")) {
            processImagePartForCidMap(part, cidMap, mimeType, partBody); // 이미지 파트의 경우 CID와 경로를 처리
        } else if (mimeType.startsWith("multipart/")) {
            processMultipartForCidMap(part, cidMap); // multipart 파트의 경우 서브 파트를 재귀적으로 처리
        } else {
            log.warn("처리되지 않은 MIME 타입: {}. 파트 스킵.", mimeType); // 처리되지 않는 MIME 타입의 경우 경고 로그
        }
    }

    // CID와 이미지 경로의 매핑을 처리하는 메서드
// 이미지 MIME 타입의 메시지 파트를 처리하여 CID와 이미지 경로를 매핑합니다.
    private void processImagePartForCidMap(MessagePart part, Map<String, String> cidMap, String mimeType, MessagePartBody partBody) throws IOException {
        if (partBody != null) {
            log.info("이미지 파트 Body가 null이 아님. 데이터 확인 중...");
            if (partBody.getData() != null) {
                log.info("이미지 데이터 존재. CID 처리 중...");
                String cid = extractCid(part); // CID를 추출
                String imageUrl = saveImageToFileSystem(partBody.getData(), mimeType, cid); // 이미지를 파일 시스템에 저장하고 URL을 반환
                cidMap.put(cid, imageUrl); // CID와 이미지 경로를 매핑
                log.info("CID: {}가 이미지 경로로 매핑됨: {}", cid, imageUrl);
            } else {
                log.warn("이미지 파트에 데이터가 없음. Attachment ID: {}", partBody.getAttachmentId());
                handleImageAttachmentForCidMap(part, cidMap, mimeType, partBody); // 이미지 첨부파일 처리
            }
        } else {
            log.warn("이미지 파트 Body가 null입니다."); // Body가 null일 경우 경고 로그
        }
    }

    // 이미지 첨부파일을 처리하여 CID와 경로를 매핑하는 메서드
// 이미지 첨부파일을 가져와서 CID와 경로를 매핑합니다.
    private void handleImageAttachmentForCidMap(MessagePart part, Map<String, String> cidMap, String mimeType, MessagePartBody partBody) throws IOException {
        if (partBody.getAttachmentId() != null) {
            log.info("Attachment ID: {}에 대한 첨부파일 데이터 가져오는 중...", partBody.getAttachmentId());
            // 첨부파일 데이터 가져오기 시도
            MessagePartBody attachPart = service.users().messages().attachments()
                    .get("me", part.getPartId(), partBody.getAttachmentId()).execute();

            if (attachPart.getData() != null) {
                log.info("첨부파일 데이터 가져오기 성공. CID 처리 중...");
                String cid = extractCid(part); // CID를 추출
                String imageUrl = saveImageToFileSystem(attachPart.getData(), mimeType, cid); // 이미지를 파일 시스템에 저장하고 URL을 반환
                cidMap.put(cid, imageUrl); // CID와 이미지 경로를 매핑
                log.info("CID: {}가 이미지 경로로 매핑됨: {}", cid, imageUrl);
            } else {
                log.warn("Attachment ID: {}에 대한 첨부파일 데이터를 가져오지 못했습니다.", partBody.getAttachmentId());
            }
        }
    }

    // Multipart 파트를 처리하여 CID와 이미지 경로를 추출하는 메서드
// multipart 파트를 재귀적으로 처리하여 CID와 이미지 경로를 추출합니다.
    private void processMultipartForCidMap(MessagePart part, Map<String, String> cidMap) throws IOException {
        if (part.getParts() != null) {
            log.info("Multipart 콘텐츠 {}개 처리 중", part.getParts().size());
            for (MessagePart subPart : part.getParts()) {
                processCidMap(subPart, cidMap); // 각 서브 파트를 처리하여 CID와 경로를 매핑
            }
        } else {
            log.warn("Multipart 파트에 서브 파트가 없습니다."); // 서브 파트가 없을 경우 경고 로그
        }
    }

    // CID(Content-ID)를 추출하는 메서드
// 메시지 파트의 헤더에서 CID(Content-ID)를 추출합니다.
    private String extractCid(MessagePart part) {
        String cid = "";
        if (part.getHeaders() != null) {
            for (MessagePartHeader header : part.getHeaders()) {
                if ("Content-ID".equalsIgnoreCase(header.getName())) {
                    cid = header.getValue(); // Content-ID 헤더의 값을 CID로 설정
                    break;
                }
            }
        }
        // CID에서 불필요한 문자 제거
        return cid.replace("<", "").replace(">", "").replaceAll("[^a-zA-Z0-9]", "_");
    }

    // 이미지 데이터를 파일 시스템에 저장하고 경로를 반환하는 메서드
// CID를 기반으로 파일 이름을 생성하여 이미지 데이터를 파일 시스템에 저장합니다.
    private String saveImageToFileSystem(String imageData, String mimeType, String cid) throws IOException {
        byte[] imageBytes = Base64.decodeBase64(imageData); // 이미지 데이터를 Base64 디코딩
        String extension = mimeType.split("/")[1]; // 확장자 추출 (예: jpeg)

        // CID에서 불필요한 부분 제거
        String cleanCid = cid.replaceAll("_cweb[0-9]+_nm", "");
        String imageName = "image_" + cleanCid + "." + extension; // 파일 이름 생성

        // 실제 파일 시스템 경로 설정
        Path imagePath = Paths.get("src/main/resources/static/assets/img/mailimg/", imageName);

        log.info("CID: {}에 대한 이미지를 경로: {}에 저장합니다.", cid, imagePath.toAbsolutePath());

        try {
            Files.createDirectories(imagePath.getParent()); // 디렉토리가 없으면 생성
            Files.write(imagePath, imageBytes); // 이미지 데이터를 파일에 기록
            log.info("이미지 경로에 저장됨: {}", imagePath.toAbsolutePath().toString());

            if (Files.exists(imagePath)) {
                log.info("이미지 파일이 존재함: {}", imagePath.toAbsolutePath().toString());
            } else {
                log.error("이미지 파일이 저장 후에도 발견되지 않음.");
            }
        } catch (IOException e) {
            log.error("이미지 파일 저장 실패: {}", imagePath.toAbsolutePath().toString(), e);
            throw e;
        }

        // 이메일 본문에서 참조할 웹 경로 반환
        return "/assets/img/mailimg/" + imageName;
    }

    // 이메일 본문 내의 CID를 실제 이미지 경로로 대체하는 메서드
// 이메일 본문에서 CID를 찾아서, 실제 이미지 경로로 변환합니다.
    private String parseEmailContent(String emailHtml, Map<String, String> cidMap) {
        log.info("CID 대체 작업 시작...");

        // CID와 이미지 경로를 대체하는 코드
        for (Map.Entry<String, String> entry : cidMap.entrySet()) {
            String cid = entry.getKey();
            String imagePath = entry.getValue();
            log.info("cid: {}를 이미지 경로: {}로 대체 시도 중...", cid, imagePath);

            // CID의 @ 이후 부분 제거 및 경로로 변환 시도
            String cleanCid = cid.split("@")[0];
            emailHtml = emailHtml.replaceAll("(?i)cid:" + Pattern.quote(cid), Matcher.quoteReplacement(imagePath));
            emailHtml = emailHtml.replaceAll("(?i)cid:" + Pattern.quote(cleanCid), Matcher.quoteReplacement(imagePath));
            log.info("대체 후 본문: {}", emailHtml);
        }

        log.info("최종 이메일 본문: {}", emailHtml);
        return emailHtml;
    }




    // createLabel 메서드: 라벨을 생성
    public String createLabel(String userId, String labelName) throws IOException {
        // 새로운 라벨 객체를 생성하고 이름을 설정.
        Label label = new Label()
                .setName(labelName)
                .setLabelListVisibility("labelShow")  // 라벨 목록에 보이도록 설정
                .setMessageListVisibility("show");    // 메시지 목록에 보이도록 설정

        //  라벨을 생성
        Label createdLabel = service.users().labels().create(userId, label).execute();

        // 생성된 라벨의 ID를 반환.
        return createdLabel.getId();
    }


    // SENT 라벨이 적용된 메시지 리스트를 가져오는 메서드
    public List<MessageDTO> listSentMessages(String userId) throws IOException {
        log.info("Fetching sent emails for user: {}", userId);

        // Gmail API를 사용하여 SENT 라벨이 있는 메시지 목록을 가져옴
        List<Message> messages = service.users().messages().list(userId)
                .setLabelIds(Collections.singletonList("SENT"))  // SENT 라벨 필터링
                .execute()
                .getMessages();

        List<MessageDTO> messageDTOList = new ArrayList<>();

        if (messages != null && !messages.isEmpty()) {
            for (Message message : messages) {
                Message fullMessage = service.users().messages().get(userId, message.getId()).execute();
                MessagePart payload = fullMessage.getPayload();

                if (payload != null) {
                    List<MessagePartHeader> headers = payload.getHeaders();

                    // MessageDTO 객체 생성 및 설정
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
        } else {
            log.info("No sent emails found for user: {}", userId);
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

    // 휴지통에 영구삭제 하는 메서드
    public void deleteMessagePermanently(String userId, String messageId) throws IOException {
        log.info("Permanently deleting message with ID: {}", messageId);
        try {
            service.users().messages().delete(userId, messageId).execute();
            log.info("Message permanently deleted successfully with ID: {}", messageId);
        } catch (IOException e) {
            log.error("Failed to permanently delete message with ID: {}", messageId, e);
            throw e;  // 에러를 던져서 호출한 쪽에서 처리하게 함
        }
    }

    //GmailService에 Draft 저장 기능 메서드
    public String saveDraft(String recipientEmail, String subject, String messageText) throws Exception {
        log.info("Saving draft for recipient: {}, subject: {}", recipientEmail, subject);
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

            // Gmail API를 사용하여 임시보관 메일로 저장.
            Message message = new Message();
            message.setRaw(encodedEmail);

            Draft draft = new Draft();
            draft.setMessage(message);

            // "me"는 인증된 사용자를 나타냄.
            Draft createdDraft = service.users().drafts().create("me", draft).execute();
            log.info("Draft saved successfully with ID: {}", createdDraft.getId());

            return createdDraft.getId();  // 생성된 Draft의 ID를 반환
        } catch (MessagingException e) {
            log.error("Failed to create email message for draft: {}", e.getMessage());
            throw new Exception("Failed to create email message for draft", e);
        } catch (Exception e) {
            log.error("Failed to save draft: {}", e.getMessage());
            throw new Exception("Failed to save draft", e);
        }
    }
    //GmailService에 Draft 목록 가져오기 기능 메서드
    public List<MessageDTO> listDrafts(String userId) throws IOException {
        log.info("Fetching drafts for user: {}", userId);

        // Gmail API를 사용하여 초안 목록을 가져옴
        ListDraftsResponse response = service.users().drafts().list(userId).execute();
        List<Draft> drafts = response.getDrafts();

        List<MessageDTO> messageDTOList = new ArrayList<>();

        if (drafts == null || drafts.isEmpty()) {
            log.info("No drafts found for user: {}", userId);
        } else {
            log.info("Number of drafts found: {}", drafts.size());

            // 각 초안에 대해 반복 처리
            for (Draft draft : drafts) {
                // 각 Draft의 메시지 ID를 사용하여 전체 메시지 정보를 가져옴
                String messageId = draft.getMessage().getId();
                Message message = service.users().messages().get(userId, messageId).execute();
                MessagePart payload = message.getPayload();

                if (payload != null) {
                    List<MessagePartHeader> headers = payload.getHeaders();

                    // MessageDTO 객체 생성 및 설정
                    MessageDTO messageDTO = new MessageDTO();
                    messageDTO.setId(message.getId());  // 메시지 ID 설정
                    messageDTO.setDraftId(draft.getId());  // Draft ID 설정
                    messageDTO.setFrom(getHeader(headers, "From").orElse("Unknown"));
                    messageDTO.setTo(getHeader(headers, "To").orElse("Unknown"));
                    messageDTO.setSubject(getHeader(headers, "Subject").orElse("No Subject"));
                    messageDTO.setDate(getHeader(headers, "Date").orElse("Unknown Date"));

                    boolean isStarred = message.getLabelIds() != null && message.getLabelIds().contains("STARRED");
                    messageDTO.setStarred(isStarred);

                    messageDTOList.add(messageDTO);
                }
            }
        }

        return messageDTOList;
    }


    // 초안을 영구적으로 삭제하는 메서드
    public void deleteDraft(String userId, String draftId) throws IOException {
        log.info("Deleting draft with ID: {}", draftId);
        try {
            service.users().drafts().delete(userId, draftId).execute();
            log.info("Draft deleted successfully with ID: {}", draftId);
        } catch (IOException e) {
            log.error("Failed to delete draft with ID: {}", draftId, e);
            throw e;  // 에러를 던져서 호출한 쪽에서 처리하게 함
        }
    }

    // STARRED 라벨이 적용된 메시지 리스트를 가져오는 메서드
    public List<MessageDTO> listStarredMessages(String userId) throws IOException {
        log.info("Fetching starred emails for user: {}", userId);

        // Gmail API를 사용하여 STARRED 라벨이 있는 메시지 목록을 가져옴
        List<Message> messages = service.users().messages().list(userId)
                .setLabelIds(Collections.singletonList("STARRED"))  // STARRED 라벨 필터링
                .execute()
                .getMessages();

        List<MessageDTO> messageDTOList = new ArrayList<>();

        if (messages != null && !messages.isEmpty()) {
            for (Message message : messages) {
                Message fullMessage = service.users().messages().get(userId, message.getId()).execute();
                MessagePart payload = fullMessage.getPayload();

                if (payload != null) {
                    List<MessagePartHeader> headers = payload.getHeaders();

                    // MessageDTO 객체 생성 및 설정
                    MessageDTO messageDTO = new MessageDTO();
                    messageDTO.setId(message.getId());
                    messageDTO.setFrom(getHeader(headers, "From").orElse("Unknown"));
                    messageDTO.setTo(getHeader(headers, "To").orElse("Unknown"));
                    messageDTO.setSubject(getHeader(headers, "Subject").orElse("No Subject"));
                    messageDTO.setDate(getHeader(headers, "Date").orElse("Unknown Date"));
                    messageDTO.setStarred(true);  // STARRED 라벨이 적용된 메일임을 명시

                    messageDTOList.add(messageDTO);
                }
            }
        } else {
            log.info("No starred emails found for user: {}", userId);
        }

        return messageDTOList;
    }
    // IMPORTANT 라벨이 적용된 메시지 리스트를 가져오는 메서드
    public List<MessageDTO> listImportantMessages(String userId) throws IOException {
        List<Message> messages = service.users().messages().list(userId)
                .setLabelIds(Collections.singletonList("IMPORTANT"))  // IMPORTANT 라벨 필터링
                .execute()
                .getMessages();

        List<MessageDTO> messageDTOList = new ArrayList<>();

        if (messages != null && !messages.isEmpty()) {
            for (Message message : messages) {
                Message fullMessage = service.users().messages().get(userId, message.getId()).execute();
                MessagePart payload = fullMessage.getPayload();

                if (payload != null) {
                    List<MessagePartHeader> headers = payload.getHeaders();

                    // MessageDTO 객체 생성 및 설정
                    MessageDTO messageDTO = new MessageDTO();
                    messageDTO.setId(message.getId());
                    messageDTO.setFrom(getHeader(headers, "From").orElse("Unknown"));
                    messageDTO.setTo(getHeader(headers, "To").orElse("Unknown"));
                    messageDTO.setSubject(getHeader(headers, "Subject").orElse("No Subject"));
                    messageDTO.setDate(getHeader(headers, "Date").orElse("Unknown Date"));

                    // STARRED 라벨이 있는지 확인
                    boolean isStarred = fullMessage.getLabelIds() != null && fullMessage.getLabelIds().contains("STARRED");
                    messageDTO.setStarred(isStarred);

                    messageDTOList.add(messageDTO);
                }
            }
        }

        return messageDTOList;
    }
    // 메일에 STARRED 라벨을 추가하는 메서드
    public void addStar(String userId, String messageId) throws IOException {
        ModifyMessageRequest mods = new ModifyMessageRequest().setAddLabelIds(List.of("STARRED"));
        service.users().messages().modify(userId, messageId, mods).execute();
    }

    // 메일에서 STARRED 라벨을 제거하는 메서드
    public void removeStar(String userId, String messageId) throws IOException {
        ModifyMessageRequest mods = new ModifyMessageRequest().setRemoveLabelIds(List.of("STARRED"));
        service.users().messages().modify(userId, messageId, mods).execute();
    }
    // 메일에 라벨을 추가하는 메서드 (라벨 ID를 사용하도록 수정)
    public void addLabelToMessage(String userId, String messageId, String labelName) throws IOException {
        String labelId = getOrCreateLabelId(userId, labelName);
        ModifyMessageRequest mods = new ModifyMessageRequest().setAddLabelIds(List.of(labelId));
        service.users().messages().modify(userId, messageId, mods).execute();
    }
    // 라벨을 생성하거나 존재하는 라벨의 ID를 가져오는 메서드
    public String getOrCreateLabelId(String userId, String labelName) throws IOException {
        // 기존 라벨을 검색하여 존재 여부 확인
        ListLabelsResponse labelsResponse = service.users().labels().list(userId).execute();
        for (Label label : labelsResponse.getLabels()) {
            if (label.getName().equalsIgnoreCase(labelName)) {
                return label.getId();  // 라벨이 이미 존재하면 해당 라벨의 ID를 반환
            }
        }

        // 라벨이 존재하지 않으면 생성
        return createLabel(userId, labelName);
    }

    // 이미 IMPORTANT 라벨이 적용된 메일인지 확인하는 메서드
    public boolean isMessageImportant(String userId, String messageId) throws IOException {
        Message message = service.users().messages().get(userId, messageId).execute();
        return message.getLabelIds() != null && message.getLabelIds().contains("IMPORTANT");
    }
    // 메일에서 라벨을 제거하는 메서드
    public void removeLabelFromMessage(String userId, String messageId, String labelName) throws IOException {
        // 라벨을 제거하기 위한 ModifyMessageRequest 객체를 생성합니다.
        ModifyMessageRequest mods = new ModifyMessageRequest().setRemoveLabelIds(List.of(labelName));
        // Gmail API를 사용하여 해당 메시지에서 라벨을 제거합니다.
        service.users().messages().modify(userId, messageId, mods).execute();
    }



    // INBOX 라벨이 적용된 메시지 리스트를 가져오는 메서드
    public List<MessageDTO> listInboxMessages(String userId) throws IOException {
        log.info("Fetching inbox emails for user: {}", userId);

        // Gmail API를 사용하여 INBOX 라벨이 있는 메시지 목록을 가져옴
        List<Message> messages = service.users().messages().list(userId)
                .setLabelIds(Collections.singletonList("INBOX"))  // INBOX 라벨 필터링
                .execute()
                .getMessages();

        List<MessageDTO> messageDTOList = new ArrayList<>();

        if (messages != null && !messages.isEmpty()) {
            for (Message message : messages) {
                Message fullMessage = service.users().messages().get(userId, message.getId()).execute();
                MessagePart payload = fullMessage.getPayload();

                if (payload != null) {
                    List<MessagePartHeader> headers = payload.getHeaders();

                    // MessageDTO 객체 생성 및 설정
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
        } else {
            log.info("No inbox emails found for user: {}", userId);
        }

        return messageDTOList;
    }

    // 사용자 라벨을 생성하는 메서드 (Gmail API에서 제공하는 기본 라벨 제외한 라벨 생성시 사용)
    public String createMyselfLabel(String userId) throws IOException {
        String labelName = "내게 쓴 메일함";

        // 기존 라벨을 검색하여 존재 여부 확인
        ListLabelsResponse labelsResponse = service.users().labels().list(userId).execute();
        for (Label label : labelsResponse.getLabels()) {
            if (label.getName().equalsIgnoreCase(labelName)) {
                return label.getId();  // 라벨이 이미 존재하면 해당 라벨의 ID를 반환
            }
        }

        // 라벨이 존재하지 않으면 생성
        return createLabel(userId, labelName);
    }


    // "내게 쓴 메일함" 메시지 리스트를 가져오는 메서드
    public List<MessageDTO> listMyselfMessages(String userId) throws IOException {
        log.info("Fetching emails sent to myself for user: {}", userId);

        List<Message> messages = service.users().messages().list(userId)
                .setLabelIds(Collections.singletonList("SENT"))
                .execute()
                .getMessages();

        List<MessageDTO> messageDTOList = new ArrayList<>();

        if (messages != null && !messages.isEmpty()) {
            for (Message message : messages) {
                Message fullMessage = service.users().messages().get(userId, message.getId()).execute();
                MessagePart payload = fullMessage.getPayload();

                if (payload != null) {
                    List<MessagePartHeader> headers = payload.getHeaders();
                    String from = getHeader(headers, "From").orElse("");
                    String to = getHeader(headers, "To").orElse("");

                    if (from.equalsIgnoreCase(to)) {
                        // 라벨 ID 확인 및 추가
                        String labelId = getOrCreateLabelId(userId, "내게 쓴 메일함");

                        // 이미 "내게 쓴 메일함" 라벨이 적용된 메일도 추가
                        MessageDTO messageDTO = new MessageDTO();
                        messageDTO.setId(message.getId());
                        messageDTO.setFrom(from);
                        messageDTO.setTo(to);
                        messageDTO.setSubject(getHeader(headers, "Subject").orElse("No Subject"));
                        messageDTO.setDate(getHeader(headers, "Date").orElse("Unknown Date"));

                        boolean isStarred = fullMessage.getLabelIds() != null && fullMessage.getLabelIds().contains("STARRED");
                        messageDTO.setStarred(isStarred);

                        messageDTOList.add(messageDTO);

                        if (fullMessage.getLabelIds() == null || !fullMessage.getLabelIds().contains(labelId)) {
                            // "내게 쓴 메일함" 라벨 추가
                            addLabelToMessage(userId, message.getId(), "내게 쓴 메일함");
                        }
                    }
                }
            }
        } else {
            log.info("No messages found with 'SENT' label.");
        }

        // 최종적으로 처리된 메시지 수만 출력
        log.info("Number of messages processed: {}", messageDTOList.size());
        return messageDTOList;
    }

}