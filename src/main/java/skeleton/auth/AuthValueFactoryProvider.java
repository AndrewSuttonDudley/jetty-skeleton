package skeleton.auth;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skeleton.config.JettySkeletonConfig;
import skeleton.model.rdbms.Token;
import skeleton.model.rdbms.User;
import skeleton.service.TokenService;
import skeleton.service.UserService;

@Singleton
public class AuthValueFactoryProvider extends AbstractValueFactoryProvider {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthValueFactoryProvider.class);
	
	@Singleton
	public static final class InjectionResolver extends ParamInjectionResolver<Auth> {
		
		public InjectionResolver() {
			super(AuthValueFactoryProvider.class);
		}
	}
	
	private static final class AuthValueFactory extends AbstractContainerRequestValueFactory<User> {
		
		@Context
		private ResourceContext context;
		
		@Override
		public User provide() {
			final HttpServletRequest request = context.getResource(HttpServletRequest.class);
			
	        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
	        if (header != null) {
	            final int space = header.indexOf(' ');
	            if (space > 0) {
	                final String method = header.substring(0, space);
	                System.out.println("method: " + method);
	                if ("bearer".equalsIgnoreCase(method)) {
	                	
	                	final String requestTokenValue = header.substring(space + 1);
	                	UserService userService = (UserService) JettySkeletonConfig.getBean(UserService.class);
	                	User user = userService.findOneByTokenValue(requestTokenValue);
	                	
	                	TokenService tokenService = (TokenService) JettySkeletonConfig.getBean(TokenService.class);
	                	Token responseToken = tokenService.getResponseToken(requestTokenValue);
	                	
	                	if (responseToken != null) {
	                		final HttpServletResponse response = context.getResource(HttpServletResponse.class);
	                		response.setHeader(HttpHeaders.AUTHORIZATION, responseToken.getValue());
	                	}
	                	return user;
	                }
	            }
	        }
			return null;
		}
	}
	
	@Inject
	public AuthValueFactoryProvider(MultivaluedParameterExtractorProvider mpep, ServiceLocator injector) {
		super(mpep, injector, Parameter.Source.UNKNOWN);
	}
	
	@Override
	public AbstractContainerRequestValueFactory<?> createValueFactory(Parameter parameter) {
		
		Class<?> classType = parameter.getRawType();
		
		if (classType == null || !classType.equals(User.class)) {
            logger.warn("IdentityParam annotation was placed on " + classType + " instead of User; Injection might not work correctly!");
            return null;
		}
		return new AuthValueFactory();
	}
}
