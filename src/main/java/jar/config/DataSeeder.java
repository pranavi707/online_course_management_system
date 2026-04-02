package jar.config;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jar.model.Announcement;
import jar.model.Course;
import jar.model.CourseModule;
import jar.model.CourseStatus;
import jar.model.Enrollment;
import jar.model.EnrollmentStatus;
import jar.model.Role;
import jar.model.UserAccount;
import jar.repository.AnnouncementRepository;
import jar.repository.CourseRepository;
import jar.repository.EnrollmentRepository;
import jar.repository.UserAccountRepository;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            UserAccountRepository userAccountRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            AnnouncementRepository announcementRepository) {
        return args -> {
            if (userAccountRepository.count() > 0) {
                return;
            }

            userAccountRepository.save(new UserAccount("Asha Reddy", "admin@ocms.edu", Role.ADMIN, "Academic Affairs"));
            UserAccount instructorOne = userAccountRepository.save(
                    new UserAccount("Dr. Vivek Rao", "vivek.rao@ocms.edu", Role.INSTRUCTOR, "Computer Science"));
            UserAccount instructorTwo = userAccountRepository.save(
                    new UserAccount("Prof. Meera Nair", "meera.nair@ocms.edu", Role.INSTRUCTOR, "Business Studies"));
            UserAccount studentOne = userAccountRepository.save(
                    new UserAccount("Ananya Sharma", "ananya@ocms.edu", Role.STUDENT, "Computer Science"));
            UserAccount studentTwo = userAccountRepository.save(
                    new UserAccount("Rahul Verma", "rahul@ocms.edu", Role.STUDENT, "Business Studies"));
            UserAccount studentThree = userAccountRepository.save(
                    new UserAccount("Neha Das", "neha@ocms.edu", Role.STUDENT, "Computer Science"));

            Course javaCourse = new Course(
                    "CS301",
                    "Enterprise Java Development",
                    "Hands-on backend development covering Spring architecture, APIs, persistence, and secure delivery.",
                    "Software Engineering",
                    LocalDate.now().minusWeeks(2),
                    LocalDate.now().plusWeeks(10),
                    CourseStatus.ACTIVE,
                    instructorOne);
            javaCourse.addModule(new CourseModule("Platform Setup", "Configure the development environment and project structure.", 1));
            javaCourse.addModule(new CourseModule("Web MVC", "Build controllers, templates, and request flows.", 2));
            javaCourse.addModule(new CourseModule("Persistence Layer", "Model entities and manage relational data using JPA.", 3));

            Course analyticsCourse = new Course(
                    "BM210",
                    "Digital Learning Analytics",
                    "Use dashboards and reporting to monitor learner participation, progress, and intervention needs.",
                    "Education Technology",
                    LocalDate.now().minusWeeks(1),
                    LocalDate.now().plusWeeks(8),
                    CourseStatus.ACTIVE,
                    instructorTwo);
            analyticsCourse.addModule(new CourseModule("Data Literacy", "Interpret participation and outcome metrics.", 1));
            analyticsCourse.addModule(new CourseModule("Student Progress", "Measure completion, risk, and pacing.", 2));

            Course uiCourse = new Course(
                    "CS240",
                    "User Experience for E-Learning",
                    "Design intuitive learning journeys for students, faculty, and administrators in educational systems.",
                    "Instructional Design",
                    LocalDate.now().plusWeeks(1),
                    LocalDate.now().plusWeeks(12),
                    CourseStatus.PLANNED,
                    instructorOne);
            uiCourse.addModule(new CourseModule("Learning Personas", "Map student and instructor needs into interface flows.", 1));

            courseRepository.saveAll(List.of(javaCourse, analyticsCourse, uiCourse));

            enrollmentRepository.saveAll(List.of(
                    new Enrollment(studentOne, javaCourse, EnrollmentStatus.IN_PROGRESS, 68, LocalDate.now().minusWeeks(2)),
                    new Enrollment(studentOne, analyticsCourse, EnrollmentStatus.ENROLLED, 22, LocalDate.now().minusDays(8)),
                    new Enrollment(studentTwo, analyticsCourse, EnrollmentStatus.IN_PROGRESS, 54, LocalDate.now().minusDays(10)),
                    new Enrollment(studentThree, javaCourse, EnrollmentStatus.ENROLLED, 35, LocalDate.now().minusWeeks(1))
            ));

            announcementRepository.saveAll(List.of(
                    new Announcement(javaCourse, "Assignment Window Open", "Submit your Spring MVC mini-project by Friday evening.", LocalDate.now().minusDays(1)),
                    new Announcement(analyticsCourse, "Dashboard Review", "Weekly analytics discussion has been moved to Thursday.", LocalDate.now().minusDays(2)),
                    new Announcement(uiCourse, "Pre-course Survey", "Complete the learner experience survey before onboarding week.", LocalDate.now().minusDays(4))
            ));
        };
    }
}
