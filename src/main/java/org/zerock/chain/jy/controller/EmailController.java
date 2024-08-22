package org.zerock.chain.jy.controller;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.chain.jy.dto.MessageDTO;
import org.zerock.chain.jy.service.GmailService;
import com.google.api.services.gmail.model.MessagePartHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.*;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeMessage;
import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.apache.commons.codec.binary.Base64; // 이걸 임포트


import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.File;
import javax.activation.FileDataSource;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Controller
@RequestMapping("/mail")   // "/mail" 경로 하위에 있는 요청들을 처리.
public class EmailController {

    @Autowired  // GmailService를 자동으로 주입받음.
    private GmailService gmailService;


    @GetMapping("/compose")
    public String mailCompose() {
        return "mail/compose";
    }

    private static final String UPLOAD_DIR = "C:/upload/";

    @PostMapping("/send")
    public String sendEmail(
            @RequestParam("recipientEmail") String recipientEmail,
            @RequestParam("subject") String subject,
            @RequestParam("message") String message,
            @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments,
            Model model) {
        log.info("sendEmail called with recipient: {}, subject: {}", recipientEmail, subject);
        try {
            MimeMessage email = new MimeMessage(Session.getDefaultInstance(new Properties(), null));
            email.setFrom(new InternetAddress("your-email@gmail.com"));
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipientEmail));
            email.setSubject(subject);

            // 최종 메일을 담을 Multipart 객체
            Multipart multipart = new MimeMultipart();

            // HTML 본문을 담을 BodyPart 생성
            MimeBodyPart htmlBodyPart = new MimeBodyPart();
            htmlBodyPart.setContent(message, "text/html; charset=UTF-8");
            multipart.addBodyPart(htmlBodyPart);

            // 첨부파일 추가
            if (attachments != null && !attachments.isEmpty()) {
                for (MultipartFile attachment : attachments) {
                    if (!attachment.isEmpty()) {
                        MimeBodyPart attachmentPart = new MimeBodyPart();
                        String fileName = StringUtils.cleanPath(attachment.getOriginalFilename());
                        Path path = Paths.get("C:/upload/" + fileName);
                        Files.write(path, attachment.getBytes());

                        DataSource source = new FileDataSource(new File("C:/upload/" + fileName));
                        attachmentPart.setDataHandler(new DataHandler(source));
                        attachmentPart.setFileName(fileName);
                        multipart.addBodyPart(attachmentPart);
                    }
                }
            }

            // 이메일에 Multipart 설정
            email.setContent(multipart);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            String encodedEmail = Base64.encodeBase64URLSafeString(buffer.toByteArray());

            Gmail service = gmailService.getGmailService();
            Message gmailMessage = new Message();
            gmailMessage.setRaw(encodedEmail);
            service.users().messages().send("me", gmailMessage).execute();

            log.info("Email sent successfully to: {}", recipientEmail);
            model.addAttribute("success", "Email sent successfully!");
        } catch (Exception e) {
            log.error("Error sending email", e);
            model.addAttribute("error", "Error sending email: " + e.getMessage());
        }
        return "mail/complete";
    }


    //이메일 목록을 표시하는 메서드 추가
    @GetMapping("/totalEmail")
    public String listEmails(Model model) {
        try {
            List<MessageDTO> messages = gmailService.listMessages("me");
            model.addAttribute("messages", messages);
            model.addAttribute("success", "Emails fetched successfully!");
        } catch (IOException e) {
            log.error("Error fetching emails", e); // 오류를 로깅.
            model.addAttribute("error", "Error fetching emails: " + e.getMessage());
        }
        return "mail/totalEmail";
    }

    // viewEmail 메서드 수정
    @GetMapping("/view")
    public String viewEmail(@RequestParam("messageId") String messageId, Model model) {
        log.info("viewEmail called with messageId: {}", messageId);
        try {
            Message message = gmailService.getMessage("me", messageId);

            List<MessagePartHeader> headers = message.getPayload().getHeaders();
            String subject = gmailService.getHeader(headers, "Subject").orElse("No Subject");

            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setId(messageId);
            messageDTO.setSubject(subject);
            messageDTO.setFrom(gmailService.getHeader(headers, "From").orElse("Unknown"));
            messageDTO.setTo(gmailService.getHeader(headers, "To").orElse("Unknown Recipient"));
            messageDTO.setDate(gmailService.getHeader(headers, "Date").orElse("Unknown Date"));

            String messageContent = gmailService.getMessageContent("me", messageId);

            // CID 기반 이미지를 실제 경로로 변경
            Pattern pattern = Pattern.compile("cid:([\\w\\-\\.]+)@\\w+\\.\\w+");
            Matcher matcher = pattern.matcher(messageContent);

            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String cid = matcher.group(1).replaceAll("[^a-zA-Z0-9]", "_");
                String imagePath = "/assets/img/mailimg/image_" + cid + ".jpeg";
                matcher.appendReplacement(sb, imagePath);
            }
            matcher.appendTail(sb);

            messageContent = sb.toString();


            log.info("Final message content: {}", messageContent);

            model.addAttribute("message", messageDTO);
            model.addAttribute("messageContent", messageContent);

        } catch (IOException e) {
            log.error("Error fetching email", e);
            model.addAttribute("error", "Error fetching email: " + e.getMessage());
            return "error";
        }
        return "mail/mailRead";
    }





    // 보낸 메일함을 표시하는 메서드 추가
    @GetMapping("/sent")
    public String listSentEmails(Model model) {
        log.info("listSentEmails called");
        try {
            // GmailService에서 SENT 라벨이 적용된 이메일 목록을 가져옴
            List<MessageDTO> sentMessages = gmailService.listSentMessages("me");
            model.addAttribute("messages", sentMessages);
            model.addAttribute("success", "Sent emails fetched successfully!");
        } catch (IOException e) {
            log.error("Error fetching sent emails", e);
            model.addAttribute("error", "Error fetching sent emails: " + e.getMessage());
        }
        return "mail/sent";  // 보낸 메일함 뷰로 반환
    }



    // 메시지를 휴지통으로 이동시키는 메서드 추가
    @PostMapping("/trash/{messageId}")
    public String moveToTrash(@PathVariable String messageId, Model model) {
        try {
            gmailService.moveToTrash("me", messageId);
            model.addAttribute("success", "Email moved to trash successfully!");
        } catch (IOException e) {
            model.addAttribute("error", "Failed to move email to trash: " + e.getMessage());
        }
        return "redirect:/mail/trash"; // 휴지통 페이지로 리다이렉트
    }

    // 휴지통에 있는 메시지 목록을 보여주는 메서드
    @GetMapping("/trash")
    public String listTrashEmails(Model model) {
        try {
            List<MessageDTO> messages = gmailService.listTrashMessages("me");
            model.addAttribute("messages", messages);
        } catch (IOException e) {
            model.addAttribute("error", "Failed to retrieve trash emails: " + e.getMessage());
        }
        return "mail/trash";
    }

    // 개별 메시지를 영구 삭제하는 메서드 추가
    @PostMapping("/trash/delete/{messageId}")
    public String deleteMessagePermanently(@PathVariable String messageId, RedirectAttributes redirectAttributes) {
        log.info("deleteMessagePermanently called with messageId: {}", messageId);
        try {
            gmailService.deleteMessagePermanently("me", messageId);
            redirectAttributes.addFlashAttribute("success", "Message permanently deleted successfully!");
        } catch (IOException e) {
            log.error("Error permanently deleting message", e);
            redirectAttributes.addFlashAttribute("error", "Error permanently deleting message: " + e.getMessage());
        }
        return "redirect:/mail/trash";  // 휴지통 페이지로 리다이렉트
    }

    // 선택된 메시지를 영구 삭제하는 메서드 추가
    @PostMapping("/trash/deleteSelected")
    public String deleteSelectedMessagesPermanently(@RequestParam("messageIds") List<String> messageIds, RedirectAttributes redirectAttributes) {
        log.info("deleteSelectedMessagesPermanently called with messageIds: {}", messageIds);
        try {
            for (String messageId : messageIds) {
                gmailService.deleteMessagePermanently("me", messageId);
            }
            redirectAttributes.addFlashAttribute("success", "Selected messages permanently deleted successfully!");
        } catch (IOException e) {
            log.error("Error permanently deleting selected messages", e);
            redirectAttributes.addFlashAttribute("error", "Error permanently deleting selected messages: " + e.getMessage());
        }
        return "redirect:/mail/trash";  // 휴지통 페이지로 리다이렉트
    }

/*    // Draft를 저장하는 요청 처리 메서드 추가
    @PostMapping("/draft")
    public String saveDraft(
            @RequestParam("recipientEmail") String recipientEmail,
            @RequestParam("subject") String subject,
            @RequestParam("message") String message,
            Model model) {
        log.info("saveDraft called with recipient: {}, subject: {}", recipientEmail, subject);
        try {
            String draftId = gmailService.saveDraft(recipientEmail, subject, message);
            model.addAttribute("success", "Draft saved successfully with ID: " + draftId);
        } catch (Exception e) {
            log.error("Error saving draft", e);
            model.addAttribute("error", "Error saving draft: " + e.getMessage());
        }
        return "mail/compose";
    }*/

    // Draft 목록을 가져와 표시하는 메서드 추가
    @GetMapping("/draftsList")
    public String listDrafts(Model model) {
        log.info("listDrafts called");
        try {
            List<MessageDTO> drafts = gmailService.listDrafts("me");

            if (drafts == null || drafts.isEmpty()) {
                log.info("No drafts found");
            } else {
                log.info("Drafts found: {}", drafts.size());
            }

            model.addAttribute("messages", drafts);
            model.addAttribute("success", "Drafts fetched successfully!");
        } catch (IOException e) {
            log.error("Error fetching drafts", e);
            model.addAttribute("error", "Error fetching drafts: " + e.getMessage());
        }
        return "mail/draftsList";
    }

    // 임시보관함에서 초안을 삭제하는 메서드 추가
    @PostMapping("/drafts/delete/{draftId}")
    public String deleteDraft(@PathVariable String draftId, RedirectAttributes redirectAttributes) {
        log.info("deleteDraft called with draftId: {}", draftId);
        try {
            gmailService.deleteDraft("me", draftId);
            redirectAttributes.addFlashAttribute("success", "Draft deleted successfully!");
        } catch (IOException e) {
            log.error("Error deleting draft", e);
            redirectAttributes.addFlashAttribute("error", "Error deleting draft: " + e.getMessage());
        }
        return "redirect:/mail/draftsList";  // 초안 목록 페이지로 리다이렉트
    }
    //임시보관함에서 일괄 삭제를 위한 메서드
    @PostMapping("/drafts/deleteSelected")
    public String deleteSelectedDrafts(@RequestParam("draftIds") List<String> draftIds, RedirectAttributes redirectAttributes) {
        log.info("deleteSelectedDrafts called with draftIds: {}", draftIds);
        try {
            for (String draftId : draftIds) {
                gmailService.deleteDraft("me", draftId);
            }
            redirectAttributes.addFlashAttribute("success", "Selected drafts deleted successfully!");
        } catch (IOException e) {
            log.error("Error deleting drafts", e);
            redirectAttributes.addFlashAttribute("error", "Error deleting selected drafts: " + e.getMessage());
        }
        return "redirect:/mail/draftsList";  // 초안 목록 페이지로 리다이렉트
    }

    //별표 메일함을 표시하는 메서드 추가
    @GetMapping("/starred")
    public String listStarredEmails(Model model) {
        log.info("listStarredEmails called");
        try {
            // GmailService에서 STARRED 라벨이 적용된 이메일 목록을 가져옴
            List<MessageDTO> starredMessages = gmailService.listStarredMessages("me");
            model.addAttribute("messages", starredMessages);
            model.addAttribute("success", "Starred emails fetched successfully!");
        } catch (IOException e) {
            log.error("Error fetching starred emails", e);
            model.addAttribute("error", "Error fetching starred emails: " + e.getMessage());
        }
        return "mail/starred";
    }

    // 중요 메일함을 표시하는 메서드 추가
    @GetMapping("/important")
    public String listImportantEmails(Model model) {
        log.info("listImportantEmails called");
        try {
            // GmailService에서 IMPORTANT 라벨이 적용된 이메일 목록을 가져옴
            List<MessageDTO> importantMessages = gmailService.listImportantMessages("me");
            model.addAttribute("messages", importantMessages);
            model.addAttribute("success", "Important emails fetched successfully!");
        } catch (IOException e) {
            log.error("Error fetching important emails", e);
            model.addAttribute("error", "Error fetching important emails: " + e.getMessage());
        }
        return "mail/important";  // 중요 메일함 뷰로 반환
    }

    //별표를 위한 메서드 추가
    @PostMapping("/toggleStar")
    public ResponseEntity<String> toggleStar(@RequestBody Map<String, Object> payload) {
        String messageId = (String) payload.get("messageId");
        boolean starred = (Boolean) payload.get("starred");

        try {
            if (starred) {
                gmailService.addStar("me", messageId);  // 별표 추가
            } else {
                gmailService.removeStar("me", messageId);  // 별표 제거
            }
            return ResponseEntity.ok("Success");
        } catch (IOException e) {
            log.error("Failed to update starred status for messageId: {}", messageId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update star status");
        }
    }
    //중요(IMPORTANT) 라벨 메서드로 이동하는 메서드
    @PostMapping("/markAsImportant")
    public ResponseEntity<Map<String, List<String>>> markAsImportant(@RequestBody List<String> messageIds) {
        Map<String, List<String>> results = new HashMap<>();
        List<String> alreadyImportantTitles = new ArrayList<>();
        List<String> markedAsImportantTitles = new ArrayList<>();

        try {
            for (String messageId : messageIds) {
                Message message = gmailService.getMessage("me", messageId);
                String subject = gmailService.getHeader(message.getPayload().getHeaders(), "Subject").orElse("No Subject");

                if (gmailService.isMessageImportant("me", messageId)) {
                    alreadyImportantTitles.add(subject);
                } else {
                    gmailService.addLabelToMessage("me", messageId, "IMPORTANT");
                    markedAsImportantTitles.add(subject);
                }
            }
            results.put("alreadyImportant", alreadyImportantTitles);
            results.put("markedAsImportant", markedAsImportantTitles);
            return ResponseEntity.ok(results);
        } catch (IOException e) {
            log.error("Error marking messages as important", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", List.of("Failed to mark messages as important")));
        }
    }

    // 중요하지 않음(IMPORTANT 라벨 제거)으로 이동하는 메서드
    @PostMapping("/unmarkAsImportant")
    public ResponseEntity<Map<String, List<String>>> unmarkAsImportant(@RequestBody List<String> messageIds) {
        Map<String, List<String>> results = new HashMap<>();
        List<String> unmarkedAsImportantTitles = new ArrayList<>();

        try {
            for (String messageId : messageIds) {
                Message message = gmailService.getMessage("me", messageId);
                String subject = gmailService.getHeader(message.getPayload().getHeaders(), "Subject").orElse("No Subject");

                if (gmailService.isMessageImportant("me", messageId)) {
                    gmailService.removeLabelFromMessage("me", messageId, "IMPORTANT");
                    unmarkedAsImportantTitles.add(subject);
                }
            }
            results.put("unmarkedAsImportant", unmarkedAsImportantTitles);
            return ResponseEntity.ok(results);
        } catch (IOException e) {
            log.error("Error unmarking messages as important", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", List.of("Failed to unmark messages as important")));
        }
    }

    // 휴지통으로 이동하는 요청 처리 메서드
    @PostMapping("/moveToTrash")
    public ResponseEntity<String> moveToTrash(@RequestBody List<String> messageIds) {
        try {
            for (String messageId : messageIds) {
                gmailService.addLabelToMessage("me", messageId, "TRASH");
            }
            return ResponseEntity.ok("Messages moved to trash successfully");
        } catch (IOException e) {
            log.error("Error moving messages to trash", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to move messages to trash");
        }
    }





    // INBOX 메일함을 표시하는 메서드 추가
    @GetMapping("/inbox")
    public String listInboxEmails(Model model) {
        log.info("listInboxEmails called");
        try {
            // GmailService에서 INBOX 라벨이 적용된 이메일 목록을 가져옴
            List<MessageDTO> inboxMessages = gmailService.listInboxMessages("me");
            model.addAttribute("messages", inboxMessages);
            model.addAttribute("success", "Inbox emails fetched successfully!");
        } catch (IOException e) {
            log.error("Error fetching inbox emails", e);
            model.addAttribute("error", "Error fetching inbox emails: " + e.getMessage());
        }
        return "mail/inbox";  // 받은 메일함 뷰로 반환
    }

    // "내게 쓴 메일함"을 표시하는 메서드 추가
    @GetMapping("/myself")
    public String listMyselfEmails(Model model) {
        log.info("listMyselfEmails called");
        try {
            // GmailService에서 본인에게 보낸 메일 목록을 가져옴
            gmailService.createMyselfLabel("me"); // 먼저 라벨이 존재하는지 확인하고 없으면 생성

            // 여기에 myselfMessages 변수를 선언하고 초기화합니다.
            List<MessageDTO> myselfMessages = gmailService.listMyselfMessages("me");

            model.addAttribute("messages", myselfMessages);
            model.addAttribute("success", "Myself emails fetched successfully!");

            // 여기서 myselfMessages의 크기를 로그로 출력해보세요.
            log.info("Number of emails in '내게 쓴 메일함': {}", myselfMessages.size());

        } catch (IOException e) {
            log.error("Error fetching myself emails", e);
            model.addAttribute("error", "Error fetching myself emails: " + e.getMessage());
        }
        return "mail/myself";  // "내게 쓴 메일함" 뷰로 반환
    }

}