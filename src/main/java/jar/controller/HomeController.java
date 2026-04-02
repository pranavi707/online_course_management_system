package jar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jar.service.CourseManagementService;
import jar.web.AdminAnnouncementForm;
import jar.web.AdminCourseForm;
import jar.web.AdminEnrollmentForm;

@Controller
public class HomeController {

    private final CourseManagementService courseManagementService;

    public HomeController(CourseManagementService courseManagementService) {
        this.courseManagementService = courseManagementService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("summary", courseManagementService.getDashboardSummary());
        model.addAttribute("courses", courseManagementService.getAllCourses());
        model.addAttribute("announcements", courseManagementService.getRecentAnnouncements());
        model.addAttribute("instructors", courseManagementService.getInstructors());
        model.addAttribute("students", courseManagementService.getStudents());
        return "index";
    }

    @GetMapping("/student/{studentId}")
    public String studentDashboard(@PathVariable Long studentId, Model model) {
        model.addAttribute("enrollments", courseManagementService.getStudentEnrollments(studentId));
        model.addAttribute("announcements", courseManagementService.getRecentAnnouncements());
        return "student-dashboard";
    }

    @GetMapping("/instructor/{instructorId}")
    public String instructorDashboard(@PathVariable Long instructorId, Model model) {
        model.addAttribute("courses", courseManagementService.getInstructorCourses(instructorId));
        return "instructor-dashboard";
    }

    @GetMapping("/courses/{courseId}")
    public String courseDetails(@PathVariable Long courseId, Model model) {
        model.addAttribute("course", courseManagementService.getCourse(courseId));
        model.addAttribute("enrollments", courseManagementService.getCourseEnrollments(courseId));
        return "course-detail";
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("adminData", courseManagementService.getAdminDashboardData());
        model.addAttribute("courseForm", new AdminCourseForm());
        model.addAttribute("announcementForm", new AdminAnnouncementForm());
        model.addAttribute("enrollmentForm", new AdminEnrollmentForm());
        return "admin-dashboard";
    }

    @PostMapping("/admin/courses")
    public String createCourse(@ModelAttribute("courseForm") AdminCourseForm form, RedirectAttributes redirectAttributes) {
        courseManagementService.createCourse(form);
        redirectAttributes.addFlashAttribute("message", "Course created successfully.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/announcements")
    public String createAnnouncement(
            @ModelAttribute("announcementForm") AdminAnnouncementForm form,
            RedirectAttributes redirectAttributes) {
        courseManagementService.createAnnouncement(form);
        redirectAttributes.addFlashAttribute("message", "Announcement published successfully.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/enrollments")
    public String createEnrollment(
            @ModelAttribute("enrollmentForm") AdminEnrollmentForm form,
            RedirectAttributes redirectAttributes) {
        courseManagementService.createEnrollment(form);
        redirectAttributes.addFlashAttribute("message", "Student enrolled successfully.");
        return "redirect:/admin";
    }
}
