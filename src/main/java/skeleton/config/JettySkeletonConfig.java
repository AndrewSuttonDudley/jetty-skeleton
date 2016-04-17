package skeleton.config;

import java.io.File;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@Configuration
@ComponentScan({
	"skeleton.auth",
	"skeleton.connector",
	"skeleton.dao",
	"skeleton.delegate",
	"skeleton.resource",
	"skeleton.service"
})
public class JettySkeletonConfig implements ApplicationContextAware, InitializingBean {
	
	private static ApplicationContext ctx;
	
	private static Environment env;
	
	@Bean
	public static PropertyPlaceholderConfigurer propConfig() {
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		
		String etcConfigPath = "/etc/jetty-skeleton/config.properties";
		File f = new File(etcConfigPath);
		
		if (f.exists() && !f.isDirectory()) { 
			ppc.setLocation(new FileSystemResource(etcConfigPath));
			
		} else {
			ppc.setLocation(new ClassPathResource("config.properties"));
		}
		return ppc;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext context) {
		ctx = context;
	}
	
	public static void autowireBean(Object object) {
		AutowireCapableBeanFactory factory = ctx.getAutowireCapableBeanFactory();
		factory.autowireBean(object);
	}

	public static <T> Object getBean(Class<T> clazz) {
		return ctx.getBean(clazz);
	}

	public static String getProperty(String key) {
		return env.getProperty(key);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
}
