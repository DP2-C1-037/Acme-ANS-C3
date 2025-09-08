
package acme.realms.assistanceAgent;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AssistanceAgentRepository extends AbstractRepository {

	@Query("select m from AssistanceAgent m where m.employeeCode = :employeeCode")
	AssistanceAgent findAssistanceAgentByEmployeeCode(String employeeCode);
}
