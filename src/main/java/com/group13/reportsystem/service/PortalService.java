package com.group13.reportsystem.service;

import com.group13.reportsystem.model.DashboardStats;
import com.group13.reportsystem.model.LoginForm;
import com.group13.reportsystem.model.Report;
import com.group13.reportsystem.model.ReviewForm;
import com.group13.reportsystem.model.SubmissionForm;
import com.group13.reportsystem.model.User;
import com.group13.reportsystem.repository.FeedbackRepository;
import com.group13.reportsystem.repository.NotificationRepository;
import com.group13.reportsystem.repository.ReferenceRepository;
import com.group13.reportsystem.repository.ReportRepository;
import com.group13.reportsystem.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PortalService {
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final NotificationRepository notificationRepository;
    private final ReferenceRepository referenceRepository;
    private final FeedbackRepository feedbackRepository;
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;

    public PortalService(UserRepository userRepository,
                         ReportRepository reportRepository,
                         NotificationRepository notificationRepository,
                         ReferenceRepository referenceRepository,
                         FeedbackRepository feedbackRepository,
                         FileStorageService fileStorageService,
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.notificationRepository = notificationRepository;
        this.referenceRepository = referenceRepository;
        this.feedbackRepository = feedbackRepository;
        this.fileStorageService = fileStorageService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    void initSeedFiles() throws IOException {
        fileStorageService.ensureSeedFile("seed/weekly-report-v1.txt", "Weekly Report v1\n\nSecurity section still incomplete.");
        fileStorageService.ensureSeedFile("seed/weekly-report-v2.txt", "Weekly Report v2\n\nDatabase rationale needs stronger explanation.");
        fileStorageService.ensureSeedFile("seed/weekly-report-v3.txt", "Weekly Report v3\n\nRevised report ready for instructor review.");
        fileStorageService.ensureSeedFile("seed/dashboard-statistics.txt", "Dashboard statistics implementation draft.");
        fileStorageService.ensureSeedFile("seed/timeline-refinement.txt", "Timeline UX refinement proposal.");
    }

    public User authenticate(LoginForm loginForm) {
        User user = userRepository.findByUsernameAndClassCode(
                loginForm.getUsername().trim(),
                loginForm.getClassCode().trim()
        );
        if (user != null && passwordEncoder.matches(loginForm.getPassword().trim(), user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    public Report getReportById(Integer reportId) {
        return reportRepository.findById(reportId);
    }

    public void markReportUnderReview(User instructor, Integer reportId) {
        Report report = reportRepository.findById(reportId);
        if (report == null || instructor == null) {
            return;
        }
        if (!report.getInstructorId().equals(instructor.getUserId())) {
            return;
        }
        if ("pending".equals(report.getStatus())) {
            reportRepository.updateReview(reportId, "under_review");
        }
    }

    public Map<String, Object> buildLoginView() {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("loginForm", new LoginForm());
        return view;
    }

    public Map<String, Object> buildDashboardData(User currentUser, Integer selectedReportId) {
        Map<String, Object> view = new LinkedHashMap<>();
        List<Report> studentReports = "student".equals(currentUser.getRole())
                ? reportRepository.findByStudentId(currentUser.getUserId())
                : List.of();
        List<Report> instructorQueue = "instructor".equals(currentUser.getRole())
                ? reportRepository.findQueueByInstructorId(currentUser.getUserId())
                : List.of();
        List<Report> reviewedReports = "instructor".equals(currentUser.getRole())
                ? reportRepository.findReviewedByInstructorId(currentUser.getUserId())
                : List.of();

        Report selectedReport = selectedReportId == null ? null : reportRepository.findById(selectedReportId);
        if (selectedReport != null && !canAccessReportFile(currentUser, selectedReport)) {
            selectedReport = null;
        }
        if (selectedReport == null && !instructorQueue.isEmpty()) {
            selectedReport = instructorQueue.get(0);
        }

        view.put("currentUser", currentUser);
        view.put("notifications", notificationRepository.findByUserId(currentUser.getUserId()));
        view.put("summaryCards", buildSummaryCards(currentUser, studentReports));
        view.put("submissionForm", defaultSubmissionForm());
        view.put("reviewForm", defaultReviewForm(selectedReport));
        view.put("studentReports", studentReports);
        view.put("instructorQueue", instructorQueue);
        view.put("reviewedReports", reviewedReports);
        view.put("selectedReport", selectedReport);
        view.put("allUsers", userRepository.findAll());
        view.put("allReports", reportRepository.findAll());
        view.put("availableInstructors", "student".equals(currentUser.getRole())
                ? userRepository.findInstructorsByClassCode(currentUser.getClassCode())
                : List.of());
        return view;
    }

    public void submitReport(User student, SubmissionForm form) throws IOException {
        User instructor = userRepository.findById(form.getInstructorId());
        if (instructor == null || !"instructor".equals(instructor.getRole())
                || !student.getClassCode().equals(instructor.getClassCode())) {
            throw new IllegalArgumentException("Invalid instructor selection.");
        }

        String storedPath = fileStorageService.store(form.getReportFile());
        Report previousReport = reportRepository.findByStudentId(student.getUserId()).stream().findFirst().orElse(null);

        Report report = new Report();
        report.setStudentId(student.getUserId());
        report.setInstructorId(instructor.getUserId());
        report.setTitle(form.getTitle().trim());
        report.setFilePath(storedPath);
        report.setOriginalFileName(StringUtils.cleanPath(form.getReportFile().getOriginalFilename()));
        report.setStatus("pending");
        report.setParentReportId(previousReport == null ? null : previousReport.getReportId());
        Integer reportId = reportRepository.insert(report);

        for (String line : form.getReferencesText().split("\\R")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                referenceRepository.insert(reportId, trimmed);
            }
        }

        if (form.getReferenceFiles() != null) {
            for (org.springframework.web.multipart.MultipartFile refFile : form.getReferenceFiles()) {
                if (refFile != null && !refFile.isEmpty()) {
                    String refPath = fileStorageService.store(refFile);
                    referenceRepository.insertWithFile(reportId, refFile.getOriginalFilename(), refPath, refFile.getOriginalFilename());
                }
            }
        }

        notificationRepository.insert(student.getUserId(), reportId, "Your report was submitted successfully.");
        notificationRepository.insert(instructor.getUserId(), reportId, "A new report is waiting for your review.");
    }

    public void reviewReport(User instructor, ReviewForm form) throws IOException {
        Report report = reportRepository.findById(form.getReportId());
        if (report == null || !report.getInstructorId().equals(instructor.getUserId())) {
            throw new IllegalArgumentException("Unauthorized review action.");
        }
        reportRepository.updateReview(report.getReportId(), form.getDecision());
        
        String feedbackFilePath = null;
        String originalFileName = null;
        if (form.getFeedbackFile() != null && !form.getFeedbackFile().isEmpty()) {
            feedbackFilePath = fileStorageService.store(form.getFeedbackFile());
            originalFileName = StringUtils.cleanPath(form.getFeedbackFile().getOriginalFilename());
        }

        if (feedbackFilePath != null) {
            feedbackRepository.insertWithFile(report.getReportId(), instructor.getUserId(), form.getFeedbackContent().trim(), feedbackFilePath, originalFileName);
        } else {
            feedbackRepository.insert(report.getReportId(), instructor.getUserId(), form.getFeedbackContent().trim());
        }
        
        notificationRepository.insert(report.getStudentId(), report.getReportId(),
                "Your report \"" + report.getTitle() + "\" was marked as " + form.getDecision() + ".");
    }

    public void deleteReport(Integer reportId) {
        referenceRepository.deleteByReportId(reportId);
        feedbackRepository.deleteByReportId(reportId);
        notificationRepository.deleteByReportId(reportId);
        reportRepository.delete(reportId);
    }

    public void withdrawReview(Integer reportId) {
        reportRepository.updateReview(reportId, "pending");
        feedbackRepository.deleteByReportId(reportId);
    }

    public boolean canAccessReportFile(User user, Report report) {
        if (user == null || report == null) {
            return false;
        }
        return "admin".equals(user.getRole())
                || ("student".equals(user.getRole()) && report.getStudentId().equals(user.getUserId()))
                || ("instructor".equals(user.getRole()) && report.getInstructorId().equals(user.getUserId()));
    }

    private SubmissionForm defaultSubmissionForm() {
        SubmissionForm form = new SubmissionForm();
        form.setTitle("");
        form.setReferencesText("");
        return form;
    }

    private ReviewForm defaultReviewForm(Report selectedReport) {
        ReviewForm form = new ReviewForm();
        form.setDecision("approved");
        if (selectedReport != null) {
            form.setReportId(selectedReport.getReportId());
            form.setFeedbackContent(selectedReport.getLatestFeedback());
        } else {
            form.setFeedbackContent("");
        }
        return form;
    }

    private List<DashboardStats> buildSummaryCards(User currentUser, List<Report> studentReports) {
        if ("student".equals(currentUser.getRole())) {
            long rejected = studentReports.stream().filter(report -> "rejected".equals(report.getStatus())).count();
            long approved = studentReports.stream().filter(report -> "approved".equals(report.getStatus())).count();
            return List.of(
                    new DashboardStats("My reports", String.valueOf(studentReports.size())),
                    new DashboardStats("Approved", String.valueOf(approved)),
                    new DashboardStats("Need revision", String.valueOf(rejected)),
                    new DashboardStats("Class", currentUser.getClassCode())
            );
        }
        if ("instructor".equals(currentUser.getRole())) {
            long total = reportRepository.countByInstructor(currentUser.getUserId());
            long pending = reportRepository.countByInstructorAndStatus(currentUser.getUserId(), "pending")
                    + reportRepository.countByInstructorAndStatus(currentUser.getUserId(), "under_review");
            long approved = reportRepository.countByInstructorAndStatus(currentUser.getUserId(), "approved");
            long rejected = reportRepository.countByInstructorAndStatus(currentUser.getUserId(), "rejected");
            return List.of(
                    new DashboardStats("Assigned", String.valueOf(total)),
                    new DashboardStats("Pending", String.valueOf(pending)),
                    new DashboardStats("Approved", String.valueOf(approved)),
                    new DashboardStats("Rejected", String.valueOf(rejected))
            );
        }
        return List.of(
                new DashboardStats("Users", String.valueOf(userRepository.findAll().size())),
                new DashboardStats("Reports", String.valueOf(reportRepository.findAll().size())),
                new DashboardStats("Class", "SYSTEM"),
                new DashboardStats("Mode", "Monitor")
        );
    }
}
