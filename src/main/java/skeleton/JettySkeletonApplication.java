package skeleton;

import java.io.IOException;
import java.util.EnumSet;

import javax.inject.Singleton;
import javax.servlet.DispatcherType;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ch.qos.logback.access.jetty.RequestLogImpl;
import skeleton.auth.AuthValueFactoryProvider;
import skeleton.auth.AuthValueFactoryProvider.InjectionResolver;
import skeleton.config.JettySkeletonConfig;
import skeleton.config.MySQLPersistenceConfig;
import skeleton.http.JettySkeletonLoggingFilter;
import skeleton.http.JsonErrorHandler;
import skeleton.resource.UserResource;

public class JettySkeletonApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(JettySkeletonApplication.class);
	
	private AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
	
	private static Server server;
	
	public static void main(String[] args) throws Exception {
		
		new JettySkeletonApplication().execute(args);
	}
	
	private void execute(String[] args) throws Exception {
		
		logger.info("Initializing spring context.");
		
		initializeSpringContext(true);
		JettySkeletonConfig.autowireBean(this);
		run(args);
	}
	
	private void initializeSpringContext(Boolean server) {
		ctx.register(JettySkeletonConfig.class, MySQLPersistenceConfig.class);
		ctx.refresh();
	}
	
	private void run(String[] args) throws Exception {
		
		server = new Server(8888);
		
		RequestLogHandler requestLogHandler = new RequestLogHandler();
		RequestLogImpl requestLog = new RequestLogImpl();
		requestLog.setFileName("src/main/resources/logback-access.xml");
		requestLogHandler.setRequestLog(requestLog);
		requestLogHandler.setServer(server);
		
		ServletContainer servletContainer = new ServletContainer(getResourceConfig());
		ServletHolder entityBrowser = new ServletHolder(servletContainer);
		ServletContextHandler entityBrowserContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		entityBrowserContext.setContextPath("/");
		entityBrowserContext.addServlet(entityBrowser, "/*");
		
		entityBrowserContext.addFilter(getCorsFilter(), "/*", EnumSet.of(DispatcherType.REQUEST));
		addRoostLoggingFilters(entityBrowserContext);
		
		entityBrowserContext.setErrorHandler(new JsonErrorHandler());
		
		HandlerCollection serverHandlers = new HandlerCollection();
		serverHandlers.addHandler(entityBrowserContext);
		requestLogHandler.setHandler(serverHandlers);
		server.setHandler(requestLogHandler);
		
		server.start();
		server.join();
	}
	
	private void addRoostLoggingFilters(ServletContextHandler entityBrowserContext) {
		FilterHolder filterHolder = new FilterHolder(new JettySkeletonLoggingFilter());
		entityBrowserContext.addFilter(filterHolder, "/users/*", EnumSet.of(DispatcherType.REQUEST));
	}
	
	private static final class AuthInjectionBinder extends AbstractBinder {
		
		@Override
		protected void configure() {
			bind(AuthValueFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
			bind(InjectionResolver.class).to(new TypeLiteral<InjectionResolver>() { }).in(Singleton.class);
		}
	}
	
	public class CroISRequestFilter implements ContainerRequestFilter {
		
		@Override
		public void filter(ContainerRequestContext requestContext) throws IOException {
			
			if (requestContext.getHeaders().getFirst( "accept" ).equals( "*/*,image/webp" )) {
				requestContext.getHeaders().putSingle( "accept", "*/*" );
			}
		}
	}
	
	private class TestFeature implements DynamicFeature {
		
		@Override
		public void configure(ResourceInfo resourceInfo, FeatureContext context) {
			context.register(new CroISRequestFilter());
		}
	}
	
	private ResourceConfig getResourceConfig() {
		ResourceConfig config = new ResourceConfig();
		config.register(JettySkeletonConfig.getBean(UserResource.class));
		
		config.register(new AuthInjectionBinder());
		
		config.register(new TestFeature());
		
		return config;
	}
	
	private FilterHolder getCorsFilter() {
		FilterHolder holder = new FilterHolder(CrossOriginFilter.class);
		holder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		holder.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "X-Requested-With,Content-Type,Accept,Origin,Authorization");
		holder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
		holder.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization");
		holder.setInitParameter(CrossOriginFilter.EXPOSED_HEADERS_PARAM, "Authorization");
		holder.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
		
		holder.setName("cross-origin");
		return holder;
	}
}
