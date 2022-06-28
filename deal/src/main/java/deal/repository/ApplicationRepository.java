package deal.repository;

import deal.entity.ClientApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<ClientApplication, Long> {
}
