package org.zerock.chain.jy.service;

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
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.mail.internet.MimeUtility;

@Service
@Log4j2
public class GmailService {

    // static 변수로 Singleton 인스턴스 선언
    private static Gmail service;
    private static Credential cachedCredential;
    private static long credentialExpiryTime;
    private static final long CACHE_DURATION_MS = 3600 * 1000; // 1시간 (3600초)

    private static final String UPLOAD_DIR = "C:/upload/";

    // Singleton 패턴 적용: 외부에서 객체 생성을 하지 못하도록 기본 생성자를 private으로 설정
    private GmailService() {
    }

    // Singleton 인스턴스를 가져오는 메서드
    public static synchronized Gmail getInstance() {
        if (service == null) {
            try {
                log.info("Gmail 서비스 객체 초기화 중...");
                NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
                service = new Gmail.Builder(httpTransport, jsonFactory, getCachedOrNewCredentials(httpTransport, jsonFactory))
                        .setApplicationName("CHAIN")
                        .build();
                log.info("Gmail 서비스 객체 초기화 완료.");
            } catch (Exception e) {
                log.error("Gmail 서비스 객체 초기화 중 오류 발생", e);
                throw new RuntimeException("Gmail 서비스 초기화에 실패했습니다.", e);
            }
        } else {
            log.info("기존의 Gmail 서비스 객체 반환.");
        }
        return service;
    }

    // OAuth2 인증을 수행하여 자격 증명을 얻는 메서드
    private static Credential getCachedOrNewCredentials(final NetHttpTransport httpTransport, GsonFactory jsonFactory) throws Exception {
        if (isCachedCredentialValid()) {
            return cachedCredential;
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
                new InputStreamReader(GmailService.class.getResourceAsStream("/credentials.json")));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, Set.of(
                GmailScopes.GMAIL_SEND,
                GmailScopes.GMAIL_READONLY,
                GmailScopes.GMAIL_MODIFY,
                GmailScopes.GMAIL_COMPOSE,
                GmailScopes.GMAIL_INSERT,
                "https://mail.google.com/"))
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile()))
                .setAccessType("offline")
                .build();

        cachedCredential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver.Builder().setPort(8888).build()).authorize("user");
        credentialExpiryTime = System.currentTimeMillis() + CACHE_DURATION_MS;
        return cachedCredential;
    }

    // 캐시된 자격 증명이 유효한지 확인하는 메서드
    private static boolean isCachedCredentialValid() {
        return cachedCredential != null &&
                cachedCredential.getAccessToken() != null &&
                credentialExpiryTime > System.currentTimeMillis();
    }


    // Gmail 서비스 객체를 사용하는 메서드들
    public void sendMail(String recipientEmail, String subject, String messageText, List<String> filePaths) throws Exception {
        Gmail service = getInstance();

        log.info("Sending email to: {}", recipientEmail);
        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage email = new MimeMessage(session);

            email.setFrom(new InternetAddress("your-email@gmail.com"));
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipientEmail));
            email.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            // 본문 설정 (HTML 포맷)
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(messageText, "text/html; charset=UTF-8");
            multipart.addBodyPart(textPart);

            // CID 매핑을 위한 파일 처리
            if (filePaths != null && !filePaths.isEmpty()) {
                for (String filePath : filePaths) {
                    Path fileAbsolutePath = Paths.get(filePath).isAbsolute()
                            ? Paths.get(filePath)
                            : Paths.get(UPLOAD_DIR, filePath).normalize();

                    log.info("Adding attachment to email: {}", fileAbsolutePath.toString());
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(fileAbsolutePath.toString());
                    attachmentPart.setDataHandler(new DataHandler(source));
                    String cid = UUID.randomUUID().toString();
                    attachmentPart.setHeader("Content-ID", "<" + cid + ">");
                    attachmentPart.setDisposition(MimeBodyPart.INLINE); // CID로 참조된 이미지를 INLINE으로 설정
                    attachmentPart.setFileName(MimeUtility.encodeText(fileAbsolutePath.getFileName().toString(), "UTF-8", "B"));
                    multipart.addBodyPart(attachmentPart);

                    // CID를 이메일 본문에서 참조되도록 수정
                    messageText = messageText.replace("cid:" + fileAbsolutePath.getFileName().toString(), "cid:" + cid);
                }
            } else {
                log.info("No attachments to add to the email.");
            }

            // 이메일의 본문을 multipart로 설정
            email.setContent(multipart, "text/html; charset=UTF-8");

            // 이메일을 바이트 배열로 변환한 후 Base64로 인코딩
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            String encodedEmail = Base64.encodeBase64URLSafeString(buffer.toByteArray());

            // Gmail API를 사용하여 이메일을 전송
            Message message = new Message();
            message.setRaw(encodedEmail);

            service.users().messages().send("me", message).execute();  // "me"는 인증된 사용자를 나타냄
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



    public List<MessageDTO> listMessages(String userId) throws IOException {
        Gmail service = getInstance();
        List<Message> messages = service.users().messages().list(userId)
                .setFields("messages(id)") // 메일 ID만 가져오도록 제한
                .execute().getMessages();

        List<MessageDTO> messageDTOList = new ArrayList<>();

        for (Message message : messages) {
            Message summaryMessage = service.users().messages().get(userId, message.getId())
                    .setFields("id,payload(headers)") // 헤더 정보만 가져옴
                    .execute();
            MessagePart payload = summaryMessage.getPayload();

            if (payload != null) {
                List<MessagePartHeader> headers = payload.getHeaders();

                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setId(summaryMessage.getId());
                messageDTO.setFrom(getHeader(headers, "From").orElse("Unknown"));
                messageDTO.setSubject(getHeader(headers, "Subject").orElse("No Subject"));
                messageDTO.setDate(getHeader(headers, "Date").orElse("Unknown Date"));

                boolean isStarred = summaryMessage.getLabelIds() != null && summaryMessage.getLabelIds().contains("STARRED");
                messageDTO.setStarred(isStarred);

                messageDTOList.add(messageDTO);
            }
        }
        return messageDTOList;
    }


    public Message getMessage(String userId, String messageId) throws IOException {
        Gmail service = getInstance();
        return service.users().messages().get(userId, messageId).execute();
    }

    public String getMessageContent(String userId, String messageId) throws IOException {
        Gmail service = getInstance();
        Message message = getMessage(userId, messageId);
        MessagePart payload = message.getPayload();
        StringBuilder body = new StringBuilder();

        if (payload != null) {
            String htmlContent = getHtmlContent(payload);
            if (htmlContent != null) {
                body.append(htmlContent);
            } else {
                log.warn("이메일 메시지에서 HTML 내용을 찾을 수 없습니다.");
            }
        }

        String finalContent = body.toString();
        Map<String, String> cidMap = extractCidMap(payload);
        finalContent = parseEmailContent(finalContent, cidMap);

        return finalContent;
    }

    private String getHtmlContent(MessagePart part) throws IOException {
        String mimeType = part.getMimeType();
        MessagePartBody partBody = part.getBody();

        if (mimeType.equals("text/html")) {
            if (partBody != null && partBody.getData() != null) {
                return new String(Base64.decodeBase64(partBody.getData()), StandardCharsets.UTF_8);
            } else {
                log.warn("HTML 파트에 데이터가 없습니다.");
            }
        } else if (mimeType.startsWith("multipart/")) {
            return extractHtmlFromMultipart(part);
        }
        return null;
    }

    private String extractHtmlFromMultipart(MessagePart part) throws IOException {
        if (part.getParts() != null) {
            for (MessagePart subPart : part.getParts()) {
                String result = getHtmlContent(subPart);
                if (result != null) {
                    return result;
                }
            }
        } else {
            log.warn("Multipart 파트에 서브 파트가 없습니다.");
        }
        return null;
    }

    private Map<String, String> extractCidMap(MessagePart payload) throws IOException {
        Map<String, String> cidMap = new HashMap<>();

        if (payload != null) {
            log.info("CID 추출을 위한 Payload 처리 중...");
            processCidMap(payload, cidMap);
            log.info("CID 추출 완료: {}", cidMap);
        } else {
            log.warn("Payload가 null이므로 CID 추출을 건너뜀.");
        }

        return cidMap;
    }

    private void processCidMap(MessagePart part, Map<String, String> cidMap) throws IOException {
        String mimeType = part.getMimeType();
        MessagePartBody partBody = part.getBody();

        log.info("MIME 타입 처리 중: {}", mimeType);

        if (mimeType.startsWith("image/")) {
            processImagePartForCidMap(part, cidMap, mimeType, partBody);
        } else if (mimeType.startsWith("multipart/")) {
            processMultipartForCidMap(part, cidMap);
        } else {
            log.warn("처리되지 않은 MIME 타입: {}. 파트 스킵.", mimeType);
        }
    }

    private void processImagePartForCidMap(MessagePart part, Map<String, String> cidMap, String mimeType, MessagePartBody partBody) throws IOException {
        if (partBody != null) {
            log.info("이미지 파트 Body가 null이 아님. 데이터 확인 중...");
            if (partBody.getData() != null) {
                log.info("이미지 데이터 존재. CID 처리 중...");
                String cid = extractCid(part);
                String imageUrl = saveImageToFileSystem(partBody.getData(), mimeType, cid);
                cidMap.put(cid, imageUrl);
                log.info("CID: {}가 이미지 경로로 매핑됨: {}", cid, imageUrl);
            } else {
                log.warn("이미지 파트에 데이터가 없음. Attachment ID: {}", partBody.getAttachmentId());
                handleImageAttachmentForCidMap(part, cidMap, mimeType, partBody);
            }
        } else {
            log.warn("이미지 파트 Body가 null입니다.");
        }
    }

    private void handleImageAttachmentForCidMap(MessagePart part, Map<String, String> cidMap, String mimeType, MessagePartBody partBody) throws IOException {
        Gmail service = getInstance();
        if (partBody.getAttachmentId() != null) {
            log.info("Attachment ID: {}에 대한 첨부파일 데이터 가져오는 중...", partBody.getAttachmentId());
            MessagePartBody attachPart = service.users().messages().attachments()
                    .get("me", part.getPartId(), partBody.getAttachmentId()).execute();

            if (attachPart.getData() != null) {
                log.info("첨부파일 데이터 가져오기 성공. CID 처리 중...");
                String cid = extractCid(part);
                String imageUrl = saveImageToFileSystem(attachPart.getData(), mimeType, cid);
                cidMap.put(cid, imageUrl);
                log.info("CID: {}가 이미지 경로로 매핑됨: {}", cid, imageUrl);
            } else {
                log.warn("Attachment ID: {}에 대한 첨부파일 데이터를 가져오지 못했습니다.", partBody.getAttachmentId());
            }
        }
    }

    private void processMultipartForCidMap(MessagePart part, Map<String, String> cidMap) throws IOException {
        if (part.getParts() != null) {
            log.info("Multipart 콘텐츠 {}개 처리 중", part.getParts().size());
            for (MessagePart subPart : part.getParts()) {
                processCidMap(subPart, cidMap);
            }
        } else {
            log.warn("Multipart 파트에 서브 파트가 없습니다.");
        }
    }

    private String extractCid(MessagePart part) {
        String cid = "";
        if (part.getHeaders() != null) {
            for (MessagePartHeader header : part.getHeaders()) {
                if ("Content-ID".equalsIgnoreCase(header.getName())) {
                    cid = header.getValue();
                    break;
                }
            }
        }
        return cid.replace("<", "").replace(">", "").replaceAll("[^a-zA-Z0-9]", "_");
    }

    private String cleanFileName(String cid) {
        return cid.replaceAll("[^a-zA-Z0-9]", "_");
    }

    private String saveImageToFileSystem(String imageData, String mimeType, String cid) throws IOException {
        byte[] imageBytes = Base64.decodeBase64(imageData);
        String extension = mimeType.split("/")[1];

        // 기존 CID를 클린업하여 안전한 파일명을 생성
        String cleanCid = cleanFileName(cid);
        String imageName = "image_" + cleanCid + "." + extension;

        Path imagePath = Paths.get("C:/upload/", imageName);

        log.info("CID: {}에 대한 이미지를 경로: {}에 저장합니다.", cid, imagePath.toAbsolutePath());

        try {
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, imageBytes);
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

        return "/upload/" + imageName;
    }



    private String parseEmailContent(String emailHtml, Map<String, String> cidMap) {
        log.info("CID 대체 작업 시작...");
        for (Map.Entry<String, String> entry : cidMap.entrySet()) {
            String cid = entry.getKey();
            String imagePath = entry.getValue();
            log.info("CID: {}를 이미지 경로: {}로 대체 시도 중...", cid, imagePath);

            // 올바른 CID 대체를 위해 cid:CID 부분을 정확하게 대체
            emailHtml = emailHtml.replace("cid:" + cid, imagePath);
        }
        log.info("최종 이메일 본문: {}", emailHtml);
        return emailHtml;
    }

    public String replaceCidWithImagePath(String emailContent, Map<String, String> cidMap) {
        // CID와 이미지 경로 매핑을 이용하여 이메일 콘텐츠 내의 CID를 실제 경로로 대체
        for (Map.Entry<String, String> entry : cidMap.entrySet()) {
            String cid = entry.getKey();
            String imagePath = entry.getValue();
            // 올바르게 CID를 대체하기 위해 정확한 포맷으로 대체
            emailContent = emailContent.replace("cid:" + cid, imagePath);
        }
        return emailContent;
    }



    public List<MessageDTO> listSentMessages(String userId) throws IOException {
        Gmail service = getInstance();
        log.info("Fetching sent emails for user: {}", userId);

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

    public void moveToTrash(String userId, String messageId) throws IOException {
        Gmail service = getInstance();
        ModifyMessageRequest mods = new ModifyMessageRequest().setRemoveLabelIds(List.of("INBOX")).setAddLabelIds(List.of("TRASH"));
        service.users().messages().modify(userId, messageId, mods).execute();
    }

    public List<MessageDTO> listTrashMessages(String userId) throws IOException {
        Gmail service = getInstance();
        List<Message> messages = service.users().messages().list(userId)
                .setLabelIds(Collections.singletonList("TRASH"))
                .setFields("messages(id,labelIds,payload(headers))") // 라벨 정보도 함께 가져옴
                .execute().getMessages();

        List<MessageDTO> messageDTOList = new ArrayList<>();

        for (Message message : messages) {
            Message fullMessage = service.users().messages().get(userId, message.getId())
                    .setFields("id,labelIds,payload(headers)")
                    .execute();

            MessageDTO messageDTO = MessageDTO.fromMessage(fullMessage);

            // 읽음/안읽음 상태 설정
            boolean isUnread = fullMessage.getLabelIds() != null && fullMessage.getLabelIds().contains("UNREAD");
            messageDTO.setRead(!isUnread);

            messageDTOList.add(messageDTO);
        }
        return messageDTOList;
    }


    public void deleteMessagePermanently(String userId, String messageId) throws IOException {
        Gmail service = getInstance();
        log.info("Permanently deleting message with ID: {}", messageId);
        try {
            service.users().messages().delete(userId, messageId).execute();
            log.info("Message permanently deleted successfully with ID: {}", messageId);
        } catch (IOException e) {
            log.error("Failed to permanently delete message with ID: {}", messageId, e);
            throw e;
        }
    }

    public String saveDraft(String recipientEmail, String subject, String messageText, List<String> filePaths) throws Exception {
        Gmail service = getInstance();
        log.info("Saving draft for recipient: {}, subject: {}", recipientEmail, subject);
        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage email = new MimeMessage(session);

            email.setFrom(new InternetAddress("your-email@gmail.com"));
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipientEmail));
            email.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(messageText, "text/html; charset=UTF-8");
            multipart.addBodyPart(textPart);

            if (filePaths != null && !filePaths.isEmpty()) {
                for (String filePath : filePaths) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(filePath);
                    attachmentPart.setDataHandler(new DataHandler(source));
                    attachmentPart.setFileName(Paths.get(filePath).getFileName().toString());
                    multipart.addBodyPart(attachmentPart);
                }
            }

            email.setContent(multipart);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            String encodedEmail = Base64.encodeBase64URLSafeString(buffer.toByteArray());

            Message message = new Message();
            message.setRaw(encodedEmail);

            Draft draft = new Draft();
            draft.setMessage(message);

            Draft createdDraft = service.users().drafts().create("me", draft).execute();
            log.info("Draft saved successfully with ID: {}", createdDraft.getId());

            return createdDraft.getId();
        } catch (MessagingException e) {
            log.error("Failed to create email message for draft: {}", e.getMessage());
            throw new Exception("Failed to create email message for draft", e);
        } catch (Exception e) {
            log.error("Failed to save draft: {}", e.getMessage());
            throw new Exception("Failed to save draft", e);
        }
    }

    public List<MessageDTO> listDrafts(String userId) throws IOException {
        Gmail service = getInstance();
        log.info("Fetching drafts for user: {}", userId);

        ListDraftsResponse response = service.users().drafts().list(userId).execute();
        List<Draft> drafts = response.getDrafts();

        List<MessageDTO> messageDTOList = new ArrayList<>();

        if (drafts == null || drafts.isEmpty()) {
            log.info("No drafts found for user: {}", userId);
        } else {
            log.info("Number of drafts found: {}", drafts.size());

            for (Draft draft : drafts) {
                String messageId = draft.getMessage().getId();
                Message message = service.users().messages().get(userId, messageId).execute();
                MessagePart payload = message.getPayload();

                if (payload != null) {
                    List<MessagePartHeader> headers = payload.getHeaders();

                    MessageDTO messageDTO = new MessageDTO();
                    messageDTO.setId(message.getId());
                    messageDTO.setDraftId(draft.getId());
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

    public void deleteDraft(String userId, String draftId) throws IOException {
        Gmail service = getInstance();
        log.info("Deleting draft with ID: {}", draftId);
        try {
            service.users().drafts().delete(userId, draftId).execute();
            log.info("Draft deleted successfully with ID: {}", draftId);
        } catch (IOException e) {
            log.error("Failed to delete draft with ID: {}", draftId, e);
            throw e;
        }
    }

    private void extractAttachments(MessagePart part, List<String> attachments, Set<String> inlineImagesCIDs) {
        if (part.getParts() != null) {
            for (MessagePart subPart : part.getParts()) {
                String contentDisposition = subPart.getHeaders().stream()
                        .filter(header -> "Content-Disposition".equalsIgnoreCase(header.getName()))
                        .map(MessagePartHeader::getValue)
                        .findFirst()
                        .orElse(null);

                String contentId = subPart.getHeaders().stream()
                        .filter(header -> "Content-ID".equalsIgnoreCase(header.getName()))
                        .map(MessagePartHeader::getValue)
                        .findFirst()
                        .orElse(null);

                log.info("Processing part: Content-Disposition = {}, Content-ID = {}", contentDisposition, contentId);
                log.info("Part MIME Type: {}", subPart.getMimeType());
                log.info("Part Filename: {}", subPart.getFilename());

                if (contentId != null && (contentDisposition == null || contentDisposition.toLowerCase().contains("inline"))) {
                    inlineImagesCIDs.add(contentId.trim().replaceAll("<|>", ""));
                    log.info("Identified inline image with CID: {}", contentId);
                } else if (contentDisposition != null && contentDisposition.toLowerCase().contains("attachment")) {
                    attachments.add("C:/upload/" + subPart.getFilename());
                    log.info("Attachment identified and added: {}", subPart.getFilename());
                } else if (subPart.getMimeType().startsWith("multipart/")) {
                    extractAttachments(subPart, attachments, inlineImagesCIDs);
                }
            }
        }
    }

    public MessageDTO getDraftById(String userId, String draftId) throws IOException {
        Gmail service = getInstance();
        log.info(">> Fetching draft details for draft ID: {}", draftId);

        Draft draft = service.users().drafts().get(userId, draftId).execute();
        Message message = draft.getMessage();
        log.info(">> Draft fetched successfully. Message ID: {}", message.getId());

        MessagePart payload = message.getPayload();
        List<MessagePartHeader> headers = payload.getHeaders();

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(message.getId());
        messageDTO.setDraftId(draftId);
        messageDTO.setFrom(getHeader(headers, "From").orElse("Unknown"));
        messageDTO.setTo(getHeader(headers, "To").orElse("Unknown"));
        messageDTO.setSubject(getHeader(headers, "Subject").orElse("No Subject"));
        messageDTO.setDate(getHeader(headers, "Date").orElse("Unknown Date"));
        log.info(">> Draft headers processed. From: {}, To: {}, Subject: {}", messageDTO.getFrom(), messageDTO.getTo(), messageDTO.getSubject());

        Set<String> inlineImagesCIDs = new HashSet<>();
        List<String> attachments = new ArrayList<>();
        extractAttachments(payload, attachments, inlineImagesCIDs);
        messageDTO.setAttachments(attachments);
        log.info(">> Attachments and inline images extracted. Attachments count: {}, Inline Images count: {}", attachments.size(), inlineImagesCIDs.size());

        String bodyContent = getMessageContent(userId, message.getId());
        Map<String, String> cidMap = extractCidMap(payload);
        bodyContent = parseEmailContent(bodyContent, cidMap);
        messageDTO.setBody(bodyContent);

        if (bodyContent.length() > 1000) {
            log.info(">> Email body content processed. Content is too large to display in logs.");
        } else {
            log.info(">> Email body content processed: {}", bodyContent);
        }

        log.info(">> Email body content processed and CID mapped successfully.");

        log.info(">> Draft details retrieved: ID = {}, Subject = {}", messageDTO.getId(), messageDTO.getSubject());
        return messageDTO;
    }

    public List<MessageDTO> listStarredMessages(String userId) throws IOException {
        Gmail service = getInstance();
        log.info("Fetching starred emails for user: {}", userId);

        List<Message> messages = service.users().messages().list(userId)
                .setLabelIds(Collections.singletonList("STARRED"))
                .setFields("messages(id,labelIds,payload(headers))") // 라벨 정보도 함께 가져옴
                .execute()
                .getMessages();

        List<MessageDTO> messageDTOList = new ArrayList<>();

        if (messages != null && !messages.isEmpty()) {
            for (Message message : messages) {
                Message fullMessage = service.users().messages().get(userId, message.getId())
                        .setFields("id,labelIds,payload(headers)")
                        .execute();

                MessageDTO messageDTO = MessageDTO.fromMessage(fullMessage);

                // 읽음/안읽음 상태 설정
                boolean isUnread = fullMessage.getLabelIds() != null && fullMessage.getLabelIds().contains("UNREAD");
                messageDTO.setRead(!isUnread);

                messageDTOList.add(messageDTO);
            }
        } else {
            log.info("No starred emails found for user: {}", userId);
        }

        return messageDTOList;
    }


    public List<MessageDTO> listImportantMessages(String userId) throws IOException {
        Gmail service = getInstance();
        List<Message> messages = service.users().messages().list(userId)
                .setLabelIds(Collections.singletonList("IMPORTANT"))
                .setFields("messages(id,labelIds,payload(headers))") // 라벨 정보도 함께 가져옴
                .execute()
                .getMessages();

        List<MessageDTO> messageDTOList = new ArrayList<>();

        if (messages != null && !messages.isEmpty()) {
            for (Message message : messages) {
                Message fullMessage = service.users().messages().get(userId, message.getId())
                        .setFields("id,labelIds,payload(headers)")
                        .execute();

                MessageDTO messageDTO = MessageDTO.fromMessage(fullMessage);

                // 읽음/안읽음 상태 설정
                boolean isUnread = fullMessage.getLabelIds() != null && fullMessage.getLabelIds().contains("UNREAD");
                messageDTO.setRead(!isUnread);

                messageDTOList.add(messageDTO);
            }
        }

        return messageDTOList;
    }


    public void addStar(String userId, String messageId) throws IOException {
        Gmail service = getInstance();
        ModifyMessageRequest mods = new ModifyMessageRequest().setAddLabelIds(List.of("STARRED"));
        service.users().messages().modify(userId, messageId, mods).execute();
    }

    public void removeStar(String userId, String messageId) throws IOException {
        Gmail service = getInstance();
        ModifyMessageRequest mods = new ModifyMessageRequest().setRemoveLabelIds(List.of("STARRED"));
        service.users().messages().modify(userId, messageId, mods).execute();
    }

    // 이메일 읽음 상태로 변경
    public void markAsRead(String userId, String messageId) throws IOException {
        modifyLabel(userId, messageId, List.of("UNREAD"), null);  // UNREAD 라벨 제거
    }

    // 이메일 읽지 않음 상태로 변경
    public void markAsUnread(String userId, String messageId) throws IOException {
        modifyLabel(userId, messageId, null, List.of("UNREAD"));  // UNREAD 라벨 추가
    }

    // Message를 받아 MessageDTO로 변환하는 메서드 추가
    public MessageDTO getMessageDTO(String userId, String messageId) throws IOException {
        Message message = getMessage(userId, messageId);
        return MessageDTO.fromMessage(message);
    }

    // 라벨을 생성하는 메서드
    public String createLabel(String userId, String labelName) throws IOException {
        Gmail service = getInstance();
        Label label = new Label()
                .setName(labelName)
                .setLabelListVisibility("labelShow")
                .setMessageListVisibility("show");

        Label createdLabel = service.users().labels().create(userId, label).execute();
        return createdLabel.getId();
    }

    // 라벨을 추가하거나 제거하는 메서드
    private void modifyLabel(String userId, String messageId, List<String> labelsToRemove, List<String> labelsToAdd) throws IOException {
        Gmail service = getInstance();
        ModifyMessageRequest mods = new ModifyMessageRequest()
                .setRemoveLabelIds(labelsToRemove)
                .setAddLabelIds(labelsToAdd);
        service.users().messages().modify(userId, messageId, mods).execute();
    }

    // 라벨 추가 메서드
    public void addLabelToMessage(String userId, String messageId, String labelName) throws IOException {
        String labelId = getOrCreateLabelId(userId, labelName);
        modifyLabel(userId, messageId, null, List.of(labelId));
    }

    // 라벨 제거 메서드
    public void removeLabelFromMessage(String userId, String messageId, String labelName) throws IOException {
        String labelId = getOrCreateLabelId(userId, labelName);
        modifyLabel(userId, messageId, List.of(labelId), null);
    }

    // 공통적으로 메일을 DTO로 변환하는 메서드
    private List<MessageDTO> convertMessagesToDTOs(List<Message> messages, String userId) throws IOException {
        List<MessageDTO> messageDTOList = new ArrayList<>();
        for (Message message : messages) {
            Message fullMessage = service.users().messages().get(userId, message.getId())
                    .setFields("id,payload(headers),labelIds")  // 라벨 정보도 함께 가져옴
                    .execute();

            MessageDTO messageDTO = MessageDTO.fromMessage(fullMessage);
            messageDTOList.add(messageDTO);
        }
        return messageDTOList;
    }

    // 라벨 ID를 가져오거나 없으면 생성하는 메서드
    public String getOrCreateLabelId(String userId, String labelName) throws IOException {
        Gmail service = getInstance();
        ListLabelsResponse labelsResponse = service.users().labels().list(userId).execute();
        for (Label label : labelsResponse.getLabels()) {
            if (label.getName().equalsIgnoreCase(labelName)) {
                return label.getId();
            }
        }
        return createLabel(userId, labelName);
    }

    public boolean isMessageImportant(String userId, String messageId) throws IOException {
        Gmail service = getInstance();
        Message message = service.users().messages().get(userId, messageId).execute();
        return message.getLabelIds() != null && message.getLabelIds().contains("IMPORTANT");
    }

    public List<MessageDTO> listInboxMessages(String userId) throws IOException {
        Gmail service = getInstance();
        log.info("Fetching inbox emails for user: {}", userId);

        List<Message> messages = service.users().messages().list(userId)
                .setLabelIds(Collections.singletonList("INBOX"))
                .setFields("messages(id,labelIds,payload(headers))") // 필요한 필드만 가져옴
                .execute()
                .getMessages();

        List<MessageDTO> messageDTOList = new ArrayList<>();

        if (messages != null && !messages.isEmpty()) {
            for (Message message : messages) {
                // fullMessage를 가져와서 필요한 필드를 사용
                Message fullMessage = service.users().messages().get(userId, message.getId())
                        .setFields("id,labelIds,payload(headers)")
                        .execute();

                MessageDTO messageDTO = MessageDTO.fromMessage(fullMessage);
                messageDTOList.add(messageDTO);
            }
        } else {
            log.info("No inbox emails found for user: {}", userId);
        }

        return messageDTOList;
    }


    public String createMyselfLabel(String userId) throws IOException {
        Gmail service = getInstance();
        String labelName = "내게 쓴 메일함";

        ListLabelsResponse labelsResponse = service.users().labels().list(userId).execute();
        for (Label label : labelsResponse.getLabels()) {
            if (label.getName().equalsIgnoreCase(labelName)) {
                return label.getId();
            }
        }

        return createLabel(userId, labelName);
    }

    public List<MessageDTO> listMyselfMessages(String userId) throws IOException {
        Gmail service = getInstance();
        log.info("Fetching emails sent to myself for user: {}", userId);

        List<Message> messages = service.users().messages().list(userId)
                .setLabelIds(Collections.singletonList("SENT"))
                .setFields("messages(id,labelIds,payload(headers))") // 라벨 정보도 함께 가져옴
                .execute()
                .getMessages();

        List<MessageDTO> messageDTOList = new ArrayList<>();

        if (messages != null && !messages.isEmpty()) {
            for (Message message : messages) {
                Message fullMessage = service.users().messages().get(userId, message.getId())
                        .setFields("id,labelIds,payload(headers)")
                        .execute();

                MessagePart payload = fullMessage.getPayload();

                if (payload != null) {
                    List<MessagePartHeader> headers = payload.getHeaders();
                    String from = getHeader(headers, "From").orElse("");
                    String to = getHeader(headers, "To").orElse("");

                    if (from.equalsIgnoreCase(to)) {
                        String labelId = getOrCreateLabelId(userId, "내게 쓴 메일함");

                        MessageDTO messageDTO = MessageDTO.fromMessage(fullMessage);

                        // 읽음/안읽음 상태 설정
                        boolean isUnread = fullMessage.getLabelIds() != null && fullMessage.getLabelIds().contains("UNREAD");
                        messageDTO.setRead(!isUnread);

                        messageDTOList.add(messageDTO);

                        if (fullMessage.getLabelIds() == null || !fullMessage.getLabelIds().contains(labelId)) {
                            addLabelToMessage(userId, message.getId(), "내게 쓴 메일함");
                        }
                    }
                }
            }
        } else {
            log.info("No messages found with 'SENT' label.");
        }

        log.info("Number of messages processed: {}", messageDTOList.size());
        return messageDTOList;
    }

}
