package com.group13.reportsystem.controller;

import com.group13.reportsystem.model.LoginForm;
import com.group13.reportsystem.model.Report;
import com.group13.reportsystem.model.ReviewForm;
import com.group13.reportsystem.model.SubmissionForm;
import com.group13.reportsystem.model.User;
import com.group13.reportsystem.service.FileStorageService;
import com.group13.reportsystem.service.PortalService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
public class PortalController {
    private final PortalService portalService;
    private final FileStorageService fileStorageService;

    public PortalController(PortalService portalService, FileStorageService fileStorageService) {
        this.portalService = portalService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm loginForm,
                        BindingResult bindingResult,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "Enter username and password.");
            return "redirect:/";
        }

        User user = portalService.authenticate(loginForm);
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "Invalid username or password.");
            return "redirect:/";
        }

        session.setAttribute("currentUserId", user.getUserId());
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/student/submit")
    public String submitReport(@Valid @ModelAttribute("submissionForm") SubmissionForm submissionForm,
                               BindingResult bindingResult,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User student = getSessionUser(session);
        if (student == null || !"student".equals(student.getRole())) {
            redirectAttributes.addFlashAttribute("message", "You must sign in as a student to submit reports.");
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "Please complete title, instructor, and references.");
            return "redirect:/";
        }

        if (submissionForm.getReportFile() == null || submissionForm.getReportFile().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please choose a file to upload.");
            return "redirect:/";
        }

        try {
            portalService.submitReport(student, submissionForm);
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("message", "Please choose a valid instructor from your class.");
            return "redirect:/";
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("message", "Could not save the uploaded file.");
            return "redirect:/";
        }
        redirectAttributes.addFlashAttribute("message", "Report submitted and instructor notification created.");
        return "redirect:/";
    }

    @GetMapping("/instructor/select")
    public String selectReport(@RequestParam("reportId") Integer reportId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User instructor = getSessionUser(session);
        if (instructor == null || !"instructor".equals(instructor.getRole())) {
            redirectAttributes.addFlashAttribute("message", "You must sign in as an instructor to review reports.");
            return "redirect:/";
        }
        portalService.markReportUnderReview(instructor, reportId);
        return "redirect:/?selectedReport=" + reportId;
    }

    @PostMapping("/instructor/review")
    public String reviewReport(@Valid @ModelAttribute("reviewForm") ReviewForm reviewForm,
                               BindingResult bindingResult,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User instructor = getSessionUser(session);
        if (instructor == null || !"instructor".equals(instructor.getRole())) {
            redirectAttributes.addFlashAttribute("message", "You must sign in as an instructor to review reports.");
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "Please choose a report, decision, and feedback.");
            return "redirect:/";
        }

        try {
            portalService.reviewReport(instructor, reviewForm);
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("message", "Could not save the decision or feedback file.");
            return "redirect:/?selectedReport=" + reviewForm.getReportId();
        }
        redirectAttributes.addFlashAttribute("message", "Instructor decision saved and student notified.");
        return "redirect:/?selectedReport=" + reviewForm.getReportId();
    }

    @GetMapping("/reports/file")
    public ResponseEntity<Resource> openReportFile(@RequestParam("reportId") Integer reportId,
                                                   HttpSession session) throws Exception {
        User user = getSessionUser(session);
        Report report = portalService.getReportById(reportId);
        if (!portalService.canAccessReportFile(user, report)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = fileStorageService.loadAsResource(report.getFilePath());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileStorageService.probeContentType(report.getFilePath())))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + report.getOriginalFileName() + "\"")
                .body(resource);
    }

    @PostMapping("/student/withdraw")
    public String withdrawReport(@RequestParam("reportId") Integer reportId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User student = getSessionUser(session);
        Report report = portalService.getReportById(reportId);
        if (student == null || report == null || !report.getStudentId().equals(student.getUserId())) {
            redirectAttributes.addFlashAttribute("message", "Unauthorized action.");
            return "redirect:/";
        }
        if (!"pending".equals(report.getStatus())) {
            redirectAttributes.addFlashAttribute("message", "Cannot withdraw a report that is already reviewed.");
            return "redirect:/";
        }
        portalService.deleteReport(reportId);
        redirectAttributes.addFlashAttribute("message", "Report withdrawn successfully.");
        return "redirect:/";
    }

    @PostMapping("/instructor/withdraw")
    public String withdrawReview(@RequestParam("reportId") Integer reportId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User instructor = getSessionUser(session);
        Report report = portalService.getReportById(reportId);
        if (instructor == null || report == null || !report.getInstructorId().equals(instructor.getUserId())) {
            redirectAttributes.addFlashAttribute("message", "Unauthorized action.");
            return "redirect:/";
        }
        portalService.withdrawReview(reportId);
        redirectAttributes.addFlashAttribute("message", "Review withdrawn. Report is now pending again.");
        return "redirect:/?selectedReport=" + reportId;
    }

    @GetMapping("/student/bm03")
    public ResponseEntity<byte[]> downloadBM03(@RequestParam("reportId") Integer reportId,
                                               HttpSession session) {
        User student = getSessionUser(session);
        Report report = portalService.getReportById(reportId);
        if (student == null || report == null || !report.getStudentId().equals(student.getUserId())) {
            return ResponseEntity.notFound().build();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("BIỂU MẪU BM03 - PHIẾU ĐÁNH GIÁ BÁO CÁO\n");
        sb.append("========================================\n\n");
        sb.append("Tên báo cáo: ").append(report.getTitle()).append("\n");
        sb.append("Sinh viên: ").append(report.getStudentName()).append(" (").append(report.getStudentClass()).append(")\n");
        sb.append("Giảng viên hướng dẫn: ").append(report.getInstructorName()).append("\n");
        sb.append("Ngày nộp: ").append(report.getSubmittedAt()).append("\n");
        sb.append("Trạng thái: ").append(report.getStatus()).append("\n\n");
        sb.append("NHẬN XÉT CỦA GIẢNG VIÊN:\n");
        sb.append("------------------------\n");
        sb.append(report.getLatestFeedback()).append("\n\n");
        sb.append("Ký tên:\n\n\n");
        sb.append(report.getInstructorName());

        byte[] content = sb.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"bm03_" + reportId + ".txt\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(content);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "File is too large! Maximum allowed size exceeded.");
        return "redirect:/";
    }

    private User getSessionUser(HttpSession session) {
        Integer currentUserId = (Integer) session.getAttribute("currentUserId");
        if (currentUserId == null) {
            return null;
        }
        return portalService.getUserById(currentUserId);
    }
}
