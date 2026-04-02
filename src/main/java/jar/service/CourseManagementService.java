package jar.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jar.model.Announcement;
import jar.model.Course;
import jar.model.CourseStatus;
import jar.model.Enrollment;
import jar.model.Role;
import jar.model.UserAccount;
import jar.repository.AnnouncementRepository;
import jar.repository.CourseRepository;
import jar.repository.EnrollmentRepository;
import jar.repository.UserAccountRepository;
import jar.model.EnrollmentStatus;
import jar.web.AdminAnnouncementForm;
import jar.web.AdminCourseForm;
import jar.web.AdminEnrollmentForm;

@Service
public class CourseManagementService {

    private final UserAccountRepository userAccountRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AnnouncementRepository announcementRepository;

    public CourseManagementService(
            UserAccountRepository userAccountRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            AnnouncementRepository announcementRepository) {
        this.userAccountRepository = userAccountRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.announcementRepository = announcementRepository;
    }

    public DashboardSummary getDashboardSummary() {
        long instructors = userAccountRepository.findByRole(Role.INSTRUCTOR).size();
        long students = userAccountRepository.findByRole(Role.STUDENT).size();
        long activeCourses = courseRepository.countByStatus(CourseStatus.ACTIVE);
        long enrollments = enrollmentRepository.count();
        return new DashboardSummary(instructors, students, activeCourses, enrollments);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAllByOrderByStartDateAsc();
    }

    public List<Announcement> getRecentAnnouncements() {
        return announcementRepository.findTop5ByOrderByPostedOnDesc();
    }

    public List<UserAccount> getInstructors() {
        return userAccountRepository.findByRole(Role.INSTRUCTOR);
    }

    public List<UserAccount> getStudents() {
        return userAccountRepository.findByRole(Role.STUDENT);
    }

    public List<Enrollment> getStudentEnrollments(Long studentId) {
        UserAccount student = userAccountRepository.findById(studentId).orElseThrow();
        return enrollmentRepository.findByStudentOrderByEnrolledOnDesc(student);
    }

    public List<Course> getInstructorCourses(Long instructorId) {
        UserAccount instructor = userAccountRepository.findById(instructorId).orElseThrow();
        return courseRepository.findByInstructorOrderByStartDate(instructor);
    }

    public Course getCourse(Long courseId) {
        return courseRepository.findById(courseId).orElseThrow();
    }

    public List<Enrollment> getCourseEnrollments(Long courseId) {
        return enrollmentRepository.findByCourseIdOrderByEnrolledOnDesc(courseId);
    }

    @Transactional
    public void createCourse(AdminCourseForm form) {
        UserAccount instructor = userAccountRepository.findById(form.getInstructorId()).orElseThrow();
        Course course = new Course(
                form.getCode(),
                form.getTitle(),
                form.getDescription(),
                form.getCategory(),
                LocalDate.parse(form.getStartDate()),
                LocalDate.parse(form.getEndDate()),
                LocalDate.parse(form.getStartDate()).isAfter(LocalDate.now()) ? CourseStatus.PLANNED : CourseStatus.ACTIVE,
                instructor);
        courseRepository.save(course);
    }

    @Transactional
    public void createAnnouncement(AdminAnnouncementForm form) {
        Course course = courseRepository.findById(form.getCourseId()).orElseThrow();
        announcementRepository.save(new Announcement(course, form.getTitle(), form.getMessage(), LocalDate.now()));
    }

    @Transactional
    public void createEnrollment(AdminEnrollmentForm form) {
        UserAccount student = userAccountRepository.findById(form.getStudentId()).orElseThrow();
        Course course = courseRepository.findById(form.getCourseId()).orElseThrow();
        enrollmentRepository.save(new Enrollment(student, course, EnrollmentStatus.ENROLLED, 0, LocalDate.now()));
    }

    public AdminDashboardData getAdminDashboardData() {
        return new AdminDashboardData(
                getDashboardSummary(),
                getAllCourses(),
                getInstructors(),
                getStudents(),
                getRecentAnnouncements(),
                courseRepository.count(),
                enrollmentRepository.countByStatus(EnrollmentStatus.COMPLETED));
    }

    public record DashboardSummary(
            long instructorCount,
            long studentCount,
            long activeCourseCount,
            long enrollmentCount) {
    }

    public record AdminDashboardData(
            DashboardSummary summary,
            List<Course> courses,
            List<UserAccount> instructors,
            List<UserAccount> students,
            List<Announcement> announcements,
            long totalCourseCount,
            long completedEnrollmentCount) {
    }
}
