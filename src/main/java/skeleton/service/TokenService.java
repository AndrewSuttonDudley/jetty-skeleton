package skeleton.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import skeleton.model.rdbms.Token;
import skeleton.model.rdbms.User;
import skeleton.repository.TokenRepository;
import skeleton.service.i.ITokenService;
import skeleton.service.i.IUserService;
import skeleton.util.DateUtil;

@Service
public class TokenService implements ITokenService {
	
	@Autowired
	private TokenRepository tokenRepository;
	
	@Autowired
	private IUserService userService;
	
	@Value("${token-security.refresh-threshold}")
	private Long refreshThreshold;
	
	@Value("${token-security.expiration-threshold}")
	private Long expirationThreshold;
	
	public void delete(Token token) {
		tokenRepository.delete(token);
	}
	
	public List<Token> findAll() {
		return Lists.newArrayList(tokenRepository.findAll());
	}
	
	public List<Token> findAll(int page, int size) {
		return Lists.newArrayList(tokenRepository.findAll(new PageRequest(page, size)));
	}
	
	public Token findOne(Long id) {
		return tokenRepository.findOne(id);
	}
	
	public Token save(Token token) {
		return tokenRepository.save(token);
	}
	
	@Override
    public Token getResponseToken(String requestAccessToken) {
		
		Token token = tokenRepository.findOneByValue(requestAccessToken);
		if (token == null || isTokenExpired(token)) {
			return null;
		}
		User user = userService.findOneByToken(token);
		Token latestToken = findMostRecentByUser(user);
		
		if (!isRefreshThresholdPassed(latestToken)) {
			return latestToken;
			
		} else if (!isTokenExpired(latestToken)) {
			return createNewToken(user);
		}
		return null;
	}
	
	@Override
    public Boolean isTokenExpired(Token token) {
		return DateUtil.timeDiff(new Date(), token.getCreatedAt()) > expirationThreshold;
    }
	
	@Override
	public Token findMostRecentByUser(User user) {
		List<Token> tokens = tokenRepository.findAllByUserOrderByCreatedAtDesc(user, new PageRequest(0, 1));
		if (tokens == null || tokens.isEmpty()) {
			return null;
		} else {
			return tokens.get(0);
		}
	}
	
	private Boolean isRefreshThresholdPassed(Token token) {
		return DateUtil.timeDiff(new Date(), token.getCreatedAt()) > refreshThreshold;
	}
	
	private Token createNewToken(User user) {
		Token token = new Token();
		token.setUser(user);
		token.setValue(UUID.randomUUID().toString());
		
		return tokenRepository.save(token);
	}
}
