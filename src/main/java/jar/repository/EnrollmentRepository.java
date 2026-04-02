package jar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jar.model.Enrollment;
import jar.model.EnrollmentStatus;
import jar.model.UserAccount;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentOrderByEnrolledOnDesc(UserAccount student);
    List<Enrollment> findByCourseIdOrderByEnrolledOnDesc(Long courseId);
    long countByStatus(EnrollmentStatus status);
    long countByCourseId(Long courseId);
    long countByStudentId(Long studentId);
}
