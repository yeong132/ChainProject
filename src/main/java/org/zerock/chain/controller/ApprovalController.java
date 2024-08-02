package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/approval")
@Log4j2
public class ApprovalController {

    @GetMapping("/adminRequest")
    public String approvalAdminRequest() { return "approval/adminRequest"; }

    @GetMapping("/completedRead")
    public String approvalCompletedRead() { return "approval/completedRead"; }

    @GetMapping("/draft")
    public String approvalDraft() { return "approval/draft"; }

    @GetMapping("/draftRead")
    public String approvalDraftRead() { return "approval/draftRead"; }

    @GetMapping("/main")
    public String approvalMain() { return "approval/main"; }

    @GetMapping("/process")
    public String approvalProcess() { return "approval/process"; }

    @GetMapping("/read")
    public String approvalRead() { return "approval/read"; }

    @GetMapping("/rejectionRead")
    public String approvalRejectionRead() { return "approval/rejectionRead"; }

    @GetMapping("/generalApproval")
    public String approvalGeneralApproval() { return "approval/generalApproval"; }

    @GetMapping("/expense")
    public String approvalExpense() { return "approval/expense"; }

    @GetMapping("/overTime")
    public String approvalOverTime() { return "approval/overTime"; }
}
