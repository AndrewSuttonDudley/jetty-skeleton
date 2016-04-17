package skeleton.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import skeleton.model.rdbms.Token;
import skeleton.model.rdbms.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long>{
	
	@Query("select t.user from Token t where t.value = :tokenValue")
	public User findOneByTokenValue(@Param("tokenValue") String tokenValue);
	
	@Query("select t.user from Token t where t = :token")
	public User findOneByToken(@Param("token") Token token);
}
