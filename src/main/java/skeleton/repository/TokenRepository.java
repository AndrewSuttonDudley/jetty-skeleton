package skeleton.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import skeleton.model.rdbms.Token;
import skeleton.model.rdbms.User;

public interface TokenRepository extends PagingAndSortingRepository<Token, Long> {
	
	public Token findOneByValue(String value);
	
	public List<Token> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
