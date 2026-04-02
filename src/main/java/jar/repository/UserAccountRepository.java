package jar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jar.model.Role;
import jar.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    List<UserAccount> findByRole(Role role);
}
