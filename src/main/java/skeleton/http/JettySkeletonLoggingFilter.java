package skeleton.http;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import skeleton.config.JettySkeletonConfig;

public class JettySkeletonLoggingFilter implements Filter {
	
	private static final Logger logger = LoggerFactory.getLogger(JettySkeletonLoggingFilter.class);
	
	@SuppressWarnings("unused")
	private FilterConfig filterConfig;
	
	@Value("${verbose-http-log}")
	private Boolean verboseHttpLog;
	
	public JettySkeletonLoggingFilter() {
		JettySkeletonConfig.autowireBean(this);
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		Assert.notNull(filterConfig, "FilterConfig must not be null");
		if (logger.isDebugEnabled()) {
			logger.debug("Initializing filter '" + filterConfig.getFilterName() + "'");
		}
		this.filterConfig = filterConfig;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		if (verboseHttpLog) {
			
			JettySkeletonServletRequestWrapper jettySkeletonServletRequestWrapper = new JettySkeletonServletRequestWrapper((HttpServletRequest) request);
			JettySkeletonServletResponseWrapper jettySkeletonServletResponseWrapper = new JettySkeletonServletResponseWrapper((HttpServletResponse) response);
			
			StringBuilder sb = new StringBuilder();
			Enumeration<String> e = jettySkeletonServletRequestWrapper.getHeaderNames();
			while (e.hasMoreElements()) {
				String header = e.nextElement();
				if (header != null) {
					sb.append(header)
						.append(" :: ")
						.append(jettySkeletonServletRequestWrapper.getHeader(header))
						.append(", ");
				}
			}
			
			boolean requestJson = isContentJson(jettySkeletonServletRequestWrapper.getContentType());
			
			logger.info("\n -----------------REST Request Detail-------------------------" + " \n RequestURI :: "
					+ jettySkeletonServletRequestWrapper.getRequestURI() + " \n REMOTE ADDRESS :: " + jettySkeletonServletRequestWrapper.getRemoteAddr()
					+ " \n HEADERS :: [ " + sb.toString() + " ] " + " \n REQUEST BODY Size :: " + jettySkeletonServletRequestWrapper.getPayload().length()
					+ " bytes" + " \n REQUEST BODY :: " + ((requestJson) ? jettySkeletonServletRequestWrapper.getPayload() : "") + " \n HTTP METHOD :: "
					+ jettySkeletonServletRequestWrapper.getMethod() + " \n ContentType :: " + jettySkeletonServletRequestWrapper.getContentType());
			
			chain.doFilter(jettySkeletonServletRequestWrapper, jettySkeletonServletResponseWrapper);
			
			boolean responseJson = isContentJson(jettySkeletonServletResponseWrapper.getContentType());
			
			logger.info("\n -----------------REST Response Detail-------------------------" + " \n Response BODY Size :: "
					+ jettySkeletonServletResponseWrapper.getContent().length() + " bytes" + " \n Response BODY :: "
					+ ((responseJson) ? jettySkeletonServletResponseWrapper.getContent() : "") + " \n Content Type :: "
					+ jettySkeletonServletResponseWrapper.getContentType());
			
		} else {
			chain.doFilter(request, response);
		}
	}
	
	private boolean isContentJson(String contentType) {
		if (contentType != null) {
			return contentType.startsWith("application/json");
		}
		return false;
	}
	
	@Override
	public void destroy() {
	}
	
	public void setVerboseHttpLog(Boolean verboseHttpLog) {
		this.verboseHttpLog = verboseHttpLog;
	}
}
