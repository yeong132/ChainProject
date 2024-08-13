package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mail")
@Log4j2
public class MailController {

/*
    //메일수신함
    @GetMapping("/receive")
    public String mailRecive() {
        return "mail/receive";
    }
*/

    //메일즐겨찾기함
    @GetMapping("highlight")
    public String mailHighlight() {
        return "mail/highlight";
    }

    //메일중요수신함
    @GetMapping("/important")
    public String mailImportant() {
        return "mail/important";
    }

/*    //메일발신함
    @GetMapping("/send")
    public String mailSend() {
        return "mail/send";
    }*/

    //메일임시보관함
    @GetMapping("/temporaryStorage")
    public String mailTemporaryStorage() {
        return "mail/temporaryStorage";
    }

    /*//메일 휴지통
    @GetMapping("/trash")
    public String mailTrash() {
        return "mail/trash";
    }
*/

    //내게 쓴 메일함
    @GetMapping("/receivetome")
    public String mailReicevetome() {
        return "mail/receivetome";
    }


   /* //메일쓰기 작성함
    @GetMapping("/compose")
    public String mailCompose() {
        return "mail/compose";
    }
*/

    //내게쓰기 작성함
    @GetMapping("/composetome")
    public String mailComposetome() {
        return "mail/composetome";
    }

    //메일 송신 완료 화면
    @GetMapping("/complete")
    public String mailComplete() {
        return "mail/complete";
    }
/*    //메일 파일 읽기
    @GetMapping("/mailRead")
    public String mailRead() {
        return "mail/mailRead";
    }*/


}
