package jar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jar.model.Course;
import jar.model.CourseStatus;
import jar.model.UserAccount;

public interface CourseRepository extends JpaRepository<Course, Long> {
    long countByStatus(CourseStatus status);
    long countByInstructorId(Long instructorId);
    List<Course> findByInstructorOrderByStartDate(UserAccount instructor);
    List<Course> findAllByOrderByStartDateAsc();
}
