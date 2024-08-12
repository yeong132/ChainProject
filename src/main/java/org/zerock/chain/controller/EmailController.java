package org.zerock.chain.controller;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.Message;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.chain.DTO.MessageDTO;
import org.zerock.chain.Service.GmailService;
import com.google.api.services.gmail.model.MessagePartHeader;
import org.springframework.web.bind.annotation.PathVariable;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

    // 이메일을 발송하는 메서드 추가
    @PostMapping("/send")
    public String sendEmail(
            @RequestParam("recipientEmail") String recipientEmail,  // 수신자 이메일을 받기.
            @RequestParam("subject") String subject,   //이메일 제목을 받기.
            @RequestParam("message") String message,  // 이메일 본문을 받기.
            Model model) {
        log.info("sendEmail called with recipient: {}, subject: {}", recipientEmail, subject);
        try {
            gmailService.sendMail(recipientEmail, subject, message);
            model.addAttribute("success", "Email sent successfully!");
        } catch (Exception e) {
            log.error("Error sending email", e); // 오류를 로깅.
            model.addAttribute("error", "Error sending email: " + e.getMessage());
        }
        return "mail/compose";
    }


    //이메일 목록을 표시하는 메서드 추가
    @GetMapping("/receive")
    public String listEmails(Model model) {
        try {
            List<MessageDTO> messages = gmailService.listMessages("me");
            model.addAttribute("messages", messages);
            model.addAttribute("success", "Emails fetched successfully!");
        } catch (IOException e) {
            log.error("Error fetching emails", e); // 오류를 로깅.
            model.addAttribute("error", "Error fetching emails: " + e.getMessage());
        }
        return "mail/receive";
    }

    //이메일의 본문(상세 내용)을 보여주는 메서드 추가
    @GetMapping("/view")
    public String viewEmail(@RequestParam("messageId") String messageId, Model model) {
        log.info("viewEmail called with messageId: {}", messageId);
        try {
            Message message = gmailService.getMessage("me", messageId);

            // 메시지의 헤더 정보 추출
            List<MessagePartHeader> headers = message.getPayload().getHeaders();
            String subject = gmailService.getHeader(headers, "Subject").orElse("No Subject");

            // 메시지 DTO 생성
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setId(messageId);
            messageDTO.setSubject(subject);
            messageDTO.setFrom(gmailService.getHeader(headers, "From").orElse("Unknown"));
            messageDTO.setTo(gmailService.getHeader(headers, "To").orElse("Unknown Recipient")); // To 필드 추가
            messageDTO.setDate(gmailService.getHeader(headers, "Date").orElse("Unknown Date"));

            // 메시지 본문 가져오기
            String messageContent = gmailService.getMessageContent("me", messageId);

            // 모델에 messageDTO 객체 추가
            model.addAttribute("message", messageDTO);
            model.addAttribute("messageContent", messageContent);

        } catch (IOException e) {
            log.error("Error fetching email", e);
            model.addAttribute("error", "Error fetching email: " + e.getMessage());
            return "error";
        }
        return "mail/mailRead";
    }



    /*// 라벨 생성 폼을 표시하는 메서드.
    @GetMapping("/label/create")
    public String showCreateLabelForm() {
        return "mail/create_label";
    }

    // 라벨을 생성하는 메서드.
    @PostMapping("/label/create")
    public String createLabel(@RequestParam("labelName") String labelName,
                              @RequestParam("labelType") String labelType,
                              @RequestParam(value = "messageListVisibility", required = false) String messageListVisibility,
                              @RequestParam(value = "labelListVisibility", required = false) String labelListVisibility,
                              Model model) {
        try {
            // GmailService를 이용해 라벨을 생성.
            String labelId = gmailService.createLabel("me", labelName);
            model.addAttribute("labelId", labelId);
            model.addAttribute("labelName", labelName);
            return "mail/success";
        } catch (IOException e) {
            log.error("Error creating label", e); // 오류를 로깅
            model.addAttribute("error", "Failed to create label: " + e.getMessage());
            return "mail/create_label";
        }
    }*/

    // 보낸 메시지함을 표시하는 메서드
    @GetMapping("/send")
    public String listSentEmails(Model model) {
        log.info("listSentEmails called");
        try {
            List<MessageDTO> sentMessages = gmailService.listSentMessages("me");
            model.addAttribute("messages", sentMessages);
            model.addAttribute("success", "Sent emails fetched successfully!");
        } catch (IOException e) {
            log.error("Error fetching sent emails", e);
            model.addAttribute("error", "Error fetching sent emails: " + e.getMessage());
        }
        return "mail/send";
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
}