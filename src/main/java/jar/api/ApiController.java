package jar.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jar.model.Announcement;
import jar.model.Course;
import jar.model.CourseModule;
import jar.model.Enrollment;
import jar.model.UserAccount;
import jar.service.CourseManagementService;
import jar.web.AdminAnnouncementForm;
import jar.web.AdminCourseForm;
import jar.web.AdminEnrollmentForm;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final CourseManagementService courseManagementService;

    public ApiController(CourseManagementService courseManagementService) {
        this.courseManagementService = courseManagementService;
    }

    @GetMapping("/overview")
    public OverviewResponse overview() {
        CourseManagementService.DashboardSummary summary = courseManagementService.getDashboardSummary();
        return new OverviewResponse(
                summary.instructorCount(),
                summary.studentCount(),
                summary.activeCourseCount(),
                summary.enrollmentCount(),
                courseManagementService.getRecentAnnouncements().stream().map(this::toAnnouncementResponse).toList());
    }

    @GetMapping("/courses")
    public List<CourseResponse> courses() {
        return courseManagementService.getAllCourses().stream().map(this::toCourseResponse).toList();
    }

    @GetMapping("/courses/{courseId}")
    public CourseDetailResponse courseDetail(@PathVariable Long courseId) {
        Course course = courseManagementService.getCourse(courseId);
        return new CourseDetailResponse(
                course.getId(),
                course.getCode(),
                course.getTitle(),
                course.getDescription(),
                course.getCategory(),
                course.getStatus().name(),
                course.getStartDate().toString(),
                course.getEndDate().toString(),
                toUserResponse(course.getInstructor()),
                course.getModules().stream().map(this::toModuleResponse).toList(),
                courseManagementService.getCourseEnrollments(courseId).stream().map(this::toEnrollmentResponse).toList());
    }

    @GetMapping("/students/{studentId}/enrollments")
    public List<EnrollmentResponse> studentEnrollments(@PathVariable Long studentId) {
        return courseManagementService.getStudentEnrollments(studentId).stream().map(this::toEnrollmentResponse).toList();
    }

    @GetMapping("/instructors/{instructorId}/courses")
    public List<CourseResponse> instructorCourses(@PathVariable Long instructorId) {
        return courseManagementService.getInstructorCourses(instructorId).stream().map(this::toCourseResponse).toList();
    }

    @GetMapping("/admin")
    public AdminResponse admin() {
        CourseManagementService.AdminDashboardData adminData = courseManagementService.getAdminDashboardData();
        return new AdminResponse(
                adminData.totalCourseCount(),
                adminData.summary().activeCourseCount(),
                adminData.summary().enrollmentCount(),
                adminData.completedEnrollmentCount(),
                adminData.instructors().stream().map(this::toUserResponse).toList(),
                adminData.students().stream().map(this::toUserResponse).toList(),
                adminData.courses().stream().map(this::toCourseResponse).toList(),
                adminData.announcements().stream().map(this::toAnnouncementResponse).toList());
    }

    @PostMapping("/admin/courses")
    public ActionResponse createCourse(@RequestBody AdminCourseForm form) {
        courseManagementService.createCourse(form);
        return new ActionResponse("Course created successfully.");
    }

    @PostMapping("/admin/announcements")
    public ActionResponse createAnnouncement(@RequestBody AdminAnnouncementForm form) {
        courseManagementService.createAnnouncement(form);
        return new ActionResponse("Announcement published successfully.");
    }

    @PostMapping("/admin/enrollments")
    public ActionResponse createEnrollment(@RequestBody AdminEnrollmentForm form) {
        courseManagementService.createEnrollment(form);
        return new ActionResponse("Student enrolled successfully.");
    }

    private CourseResponse toCourseResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getCode(),
                course.getTitle(),
                course.getDescription(),
                course.getCategory(),
                course.getStatus().name(),
                course.getStartDate().toString(),
                course.getEndDate().toString(),
                course.getInstructor().getFullName(),
                course.getModules().size());
    }

    private EnrollmentResponse toEnrollmentResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                toUserResponse(enrollment.getStudent()),
                new CompactCourseResponse(enrollment.getCourse().getId(), enrollment.getCourse().getCode(), enrollment.getCourse().getTitle()),
                enrollment.getStatus().name(),
                enrollment.getProgressPercent(),
                enrollment.getEnrolledOn().toString());
    }

    private AnnouncementResponse toAnnouncementResponse(Announcement announcement) {
        return new AnnouncementResponse(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getMessage(),
                announcement.getPostedOn().toString(),
                new CompactCourseResponse(
                        announcement.getCourse().getId(),
                        announcement.getCourse().getCode(),
                        announcement.getCourse().getTitle()));
    }

    private ModuleResponse toModuleResponse(CourseModule module) {
        return new ModuleResponse(module.getId(), module.getTitle(), module.getSummary(), module.getWeekNumber());
    }

    private UserResponse toUserResponse(UserAccount userAccount) {
        return new UserResponse(
                userAccount.getId(),
                userAccount.getFullName(),
                userAccount.getEmail(),
                userAccount.getRole().name(),
                userAccount.getDepartment());
    }

    public record OverviewResponse(
            long instructorCount,
            long studentCount,
            long activeCourseCount,
            long enrollmentCount,
            List<AnnouncementResponse> recentAnnouncements) {
    }

    public record AdminResponse(
            long totalCourseCount,
            long activeCourseCount,
            long enrollmentCount,
            long completedEnrollmentCount,
            List<UserResponse> instructors,
            List<UserResponse> students,
            List<CourseResponse> courses,
            List<AnnouncementResponse> announcements) {
    }

    public record ActionResponse(String message) {
    }

    public record UserResponse(
            Long id,
            String fullName,
            String email,
            String role,
            String department) {
    }

    public record CourseResponse(
            Long id,
            String code,
            String title,
            String description,
            String category,
            String status,
            String startDate,
            String endDate,
            String instructorName,
            int moduleCount) {
    }

    public record CompactCourseResponse(Long id, String code, String title) {
    }

    public record ModuleResponse(Long id, String title, String summary, int weekNumber) {
    }

    public record EnrollmentResponse(
            Long id,
            UserResponse student,
            CompactCourseResponse course,
            String status,
            int progressPercent,
            String enrolledOn) {
    }

    public record AnnouncementResponse(
            Long id,
            String title,
            String message,
            String postedOn,
            CompactCourseResponse course) {
    }

    public record CourseDetailResponse(
            Long id,
            String code,
            String title,
            String description,
            String category,
            String status,
            String startDate,
            String endDate,
            UserResponse instructor,
            List<ModuleResponse> modules,
            List<EnrollmentResponse> enrollments) {
    }
}
