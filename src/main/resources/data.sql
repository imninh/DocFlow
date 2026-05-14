MERGE INTO users (user_id, username, password_hash, email, full_name, role, class_code, created_at) KEY(user_id) VALUES
  (1, 'ninh', '$2a$10$fBx8eKpLAYafH9d2qqVRcegvzTNwlkIFxOMIFJsR6OoTkbhgEyoea', 'ninh@student.local', 'Tran The Ninh', 'student', 'ET4430E-K68', CURRENT_TIMESTAMP),
  (2, 'anh', '$2a$10$nk4wJY3jkywldVjXXqU4.uHpxyJLbDuthLnQkpJw7mwij5MpG5eGu', 'anh@student.local', 'Pham Minh Anh', 'student', 'ET4430E-K68', CURRENT_TIMESTAMP),
  (3, 'ha', '$2a$10$gWbeu3n7aX81N33l7rzzSuUoUQ6uWoiCjfVMlh8PI366.iVICOI9e', 'ha@student.local', 'Le Thu Ha', 'student', 'ET4430E-K68', CURRENT_TIMESTAMP),
  (4, 'linh', '$2a$10$fJ4nuZmuuG4FMKteRvMQzOnlc3NDl.xaP5uc.NvTSX6xPpFgIH0dq', 'linh@instructor.local', 'Dr. Doan Ngoc Linh', 'instructor', 'ET4430E-K68', CURRENT_TIMESTAMP),
  (5, 'bao', '$2a$10$DCya51dx84TUg4NGmkRIQu5G4o.iS.yWDKC6upXeb6Gtic991DqJO', 'bao@instructor.local', 'Prof. Nguyen Quoc Bao', 'instructor', 'ET4430E-K68', CURRENT_TIMESTAMP),
  (6, 'mao', '$2a$10$SKbkDGUP28AK7tHgldoS7O63QwnY.RF8gAEtA6/ltYnD63VLa3oLq', 'mao@admin.local', 'Dao Huu Mao', 'admin', 'SYSTEM', CURRENT_TIMESTAMP);

MERGE INTO reports (report_id, student_id, instructor_id, title, file_path, original_file_name, status, parent_report_id, submitted_at, updated_at) KEY(report_id) VALUES
  (1, 1, 4, 'Weekly Report v1', 'seed/weekly-report-v1.txt', 'weekly-report-v1.txt', 'approved', NULL, DATEADD('DAY', -12, CURRENT_TIMESTAMP), DATEADD('DAY', -12, CURRENT_TIMESTAMP)),
  (2, 1, 4, 'Weekly Report v2', 'seed/weekly-report-v2.txt', 'weekly-report-v2.txt', 'rejected', 1, DATEADD('DAY', -4, CURRENT_TIMESTAMP), DATEADD('DAY', -4, CURRENT_TIMESTAMP)),
  (3, 1, 4, 'Weekly Report v3', 'seed/weekly-report-v3.txt', 'weekly-report-v3.txt', 'under_review', 2, DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP)),
  (4, 2, 4, 'Dashboard Statistics Integration', 'seed/dashboard-statistics.txt', 'dashboard-statistics.txt', 'pending', NULL, DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP)),
  (5, 3, 5, 'Version Timeline UX Refinement', 'seed/timeline-refinement.txt', 'timeline-refinement.txt', 'pending', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO references_list (reference_id, report_id, citation_text, url, added_at) KEY(reference_id) VALUES
  (1, 1, 'Spring Boot Reference Guide', NULL, CURRENT_TIMESTAMP),
  (2, 1, 'MySQL 8 Documentation', NULL, CURRENT_TIMESTAMP),
  (3, 2, 'Thymeleaf Layout Tutorial', NULL, CURRENT_TIMESTAMP),
  (4, 3, 'Spring JDBC Guide', NULL, CURRENT_TIMESTAMP),
  (5, 4, 'Aggregate query notes', NULL, CURRENT_TIMESTAMP),
  (6, 5, 'Version history UX notes', NULL, CURRENT_TIMESTAMP);

MERGE INTO feedbacks (feedback_id, report_id, instructor_id, content, created_at) KEY(feedback_id) VALUES
  (1, 1, 4, 'Good baseline implementation, but the security section was still incomplete.', DATEADD('DAY', -12, CURRENT_TIMESTAMP)),
  (2, 2, 4, 'The structure is clear, but the database rationale needs stronger explanation and citation support.', DATEADD('DAY', -4, CURRENT_TIMESTAMP)),
  (3, 3, 4, 'Awaiting instructor review.', DATEADD('DAY', -1, CURRENT_TIMESTAMP)),
  (4, 4, 4, 'Pending review.', DATEADD('DAY', -1, CURRENT_TIMESTAMP)),
  (5, 5, 5, 'Pending review.', CURRENT_TIMESTAMP);

MERGE INTO notifications (notification_id, user_id, report_id, message, is_read, created_at) KEY(notification_id) VALUES
  (1, 1, 2, 'Report version 2 was rejected. Please revise the database explanation section.', FALSE, DATEADD('DAY', -3, CURRENT_TIMESTAMP)),
  (2, 1, 3, 'Instructor feedback is available for your latest submission.', FALSE, DATEADD('HOUR', -12, CURRENT_TIMESTAMP)),
  (3, 4, 3, 'A revised report from Tran The Ninh has been resubmitted.', FALSE, DATEADD('HOUR', -10, CURRENT_TIMESTAMP)),
  (4, 4, 4, 'A pending report needs review today.', FALSE, DATEADD('HOUR', -8, CURRENT_TIMESTAMP)),
  (5, 6, NULL, 'System database initialized successfully.', FALSE, DATEADD('HOUR', -2, CURRENT_TIMESTAMP));
