package skeleton.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import skeleton.model.rdbms.Token;
import skeleton.model.rdbms.User;
import skeleton.repository.UserRepository;
import skeleton.service.i.IUserService;

@Service
public class UserService implements IUserService {
	
	@Autowired
	private UserRepository userRepository;
	
	public void delete(User user) {
		userRepository.delete(user);
	}
	
	public List<User> findAll() {
		return Lists.newArrayList(userRepository.findAll());
	}
	
	public List<User> findAll(int page, int size) {
		return Lists.newArrayList(userRepository.findAll(new PageRequest(page, size)));
	}
	
	public User findOne(Long id) {
		return userRepository.findOne(id);
	}
	
	public User save(User user) {
		return userRepository.save(user);
	}
	
	@Override
	public User findOneByTokenValue(String tokenValue) {
		return userRepository.findOneByTokenValue(tokenValue);
	}

	public User findOneByToken(Token token) {
		return userRepository.findOneByToken(token);
	}
}
