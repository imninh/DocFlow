CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    class_code VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS reports (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    instructor_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    status VARCHAR(30) DEFAULT 'pending',
    parent_report_id INT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reports_student FOREIGN KEY (student_id) REFERENCES users(user_id),
    CONSTRAINT fk_reports_instructor FOREIGN KEY (instructor_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS references_list (
    reference_id INT AUTO_INCREMENT PRIMARY KEY,
    report_id INT NOT NULL,
    citation_text TEXT NOT NULL,
    url VARCHAR(500),
    file_path VARCHAR(500),
    original_file_name VARCHAR(255),
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_references_report FOREIGN KEY (report_id) REFERENCES reports(report_id)
);

CREATE TABLE IF NOT EXISTS feedbacks (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    report_id INT NOT NULL,
    instructor_id INT NOT NULL,
    content TEXT NOT NULL,
    file_path VARCHAR(500),
    original_file_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_feedbacks_report FOREIGN KEY (report_id) REFERENCES reports(report_id),
    CONSTRAINT fk_feedbacks_instructor FOREIGN KEY (instructor_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    report_id INT,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_notifications_report FOREIGN KEY (report_id) REFERENCES reports(report_id)
);
