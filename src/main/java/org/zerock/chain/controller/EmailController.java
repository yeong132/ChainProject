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


import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Log4j2
@Controller
@RequestMapping("/mail")   // "/mail" 경로 하위에 있는 요청들을 처리합니다.
public class EmailController {

    @Autowired  // GmailService를 자동으로 주입받습니다.
    private GmailService gmailService;

    // GET 요청으로 "/mail/compose"에 접근할 때 호출됩니다. 이메일 작성 페이지로 이동합니다.
    @GetMapping("/compose")
    public String mailCompose() {
        return "mail/compose";
    }

    // POST 요청으로 "/mail/send"에 접근할 때 호출됩니다. 이메일을 보내는 메서드입니다.
    @PostMapping("/send")
    public String sendEmail(
            @RequestParam("recipientEmail") String recipientEmail,  // 요청 파라미터에서 수신자 이메일을 받습니다.
            @RequestParam("subject") String subject,   // 요청 파라미터에서 이메일 제목을 받습니다.
            @RequestParam("message") String message,  // 요청 파라미터에서 이메일 본문을 받습니다.
            Model model) {
        log.info("sendEmail called with recipient: {}, subject: {}", recipientEmail, subject);
        try {
            // GmailService를 이용해 이메일을 보냅니다.
            gmailService.sendMail(recipientEmail, subject, message);
            model.addAttribute("success", "Email sent successfully!");
        } catch (Exception e) {
            log.error("Error sending email", e); // 오류를 로깅합니다.
            model.addAttribute("error", "Error sending email: " + e.getMessage());
        }
        return "mail/compose"; // 다시 이메일 작성 페이지로 이동합니다.
    }


/*    // GET 요청으로 "/mail/list"에 접근할 때 호출됩니다. 이메일 목록을 표시하는 메서드입니다.
    @GetMapping("/list")
    public String listEmails(@RequestParam(value = "messageId", required = false) String messageId, Model model) {
        log.info("listEmails called");
        try {
            // GmailService를 이용해 이메일 목록을 가져옵니다.
            List<Message> messages = gmailService.listMessages("me");
            model.addAttribute("messages", messages); // 메시지 목록을 모델에 추가합니다.
            model.addAttribute("success", "Emails fetched successfully!");

            // 특정 메시지 ID가 제공된 경우 해당 메시지를 가져와 모델에 추가합니다.
            if (messageId != null && !messageId.isEmpty()) {
                log.info("getMessage called with messageId: {}", messageId);
                Message message = gmailService.getMessage("me", messageId);
                model.addAttribute("message", message); // 개별 메시지를 모델에 추가
            }
        } catch (IOException e) {
            log.error("Error fetching emails", e); // 오류를 로깅
            model.addAttribute("error", "Error fetching emails: " + e.getMessage());
        }
        return "mail/list"; // "mail/list" 뷰를 반환합니다.
    }*/

    // GET 요청으로 "/mail/list"에 접근할 때 호출됩니다. 이메일 목록을 표시하는 메서드입니다.
    @GetMapping("/receive")
    public String listEmails(Model model) {
        try {
            List<MessageDTO> messages = gmailService.listMessages("me");
            model.addAttribute("messages", messages);
            model.addAttribute("success", "Emails fetched successfully!");
        } catch (IOException e) {
            log.error("Error fetching emails", e); // 오류를 로깅합니다.
            model.addAttribute("error", "Error fetching emails: " + e.getMessage());
        }
        return "mail/receive"; // "mail/receive" 뷰를 반환합니다.
    }


    @GetMapping("/view")
    public String viewEmail(@RequestParam("messageId") String messageId, Model model) {
        log.info("viewEmail called with messageId: {}", messageId);
        try {
            // GmailService 이용 특정 메시지를 가져오기
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



    /*// GET 요청으로 "/label/create"에 접근할 때 라벨 생성 폼을 표시하는 메서드입니다.
    @GetMapping("/label/create")
    public String showCreateLabelForm() {
        return "mail/create_label";
    }

    // POST 요청으로 "/label/create"에 접근할 때 호출됩니다. 라벨을 생성하는 메서드입니다.
    @PostMapping("/label/create")
    public String createLabel(@RequestParam("labelName") String labelName,
                              @RequestParam("labelType") String labelType,
                              @RequestParam(value = "messageListVisibility", required = false) String messageListVisibility,
                              @RequestParam(value = "labelListVisibility", required = false) String labelListVisibility,
                              Model model) {
        try {
            // GmailService를 이용해 라벨을 생성합니다.
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
}