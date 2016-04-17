package skeleton.config;

import java.util.Properties;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableJpaRepositories({ "skeleton.repository" })
@EnableTransactionManagement
@PropertySource({
	"file:/etc/jetty-skeleton/persistence-mysql.properties",
	"file:/etc/jetty-skeleton/persistence-mysql-hikari.properties"
})
//"classpath:persistence-mysql.properties",
//"classpath:persistence-mysql-hikari.properties",

@ComponentScan({ "skeleton.repository" })
public class MySQLPersistenceConfig {

	private static final String PROPERTY_NAME_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
	private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
	private static final String PROPERTY_NAME_EJB_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
	private static final String PROPERTY_NAME_GLOBALLY_QUOTED_IDENTIFIERS = "hibernate.globally_quoted_identifiers";
	private static final String PROPERTY_NAME_ENVERS_AUDIT_TABLE_SUFFIX = "org.hibernate.envers.audit_table_suffix";
	private static final String PROPERTY_NAME_SHOW_SQL = "hibernate.show_sql";
	private static final String PROPERTY_NAME_LOGGER_LEVEL = "log4j.logger.org.hibernate";

	@Resource
	private Environment env;

	@Bean
	public DataSource dataSource() {
		
//		String etcConfigPath = "/etc/jetty-skeleton/persistence-mysql-hikari.properties";
//		File f = new File(etcConfigPath);
//		
//		if (f.exists() && !f.isDirectory()) { 
			return new HikariDataSource(new HikariConfig("/etc/jetty-skeleton/persistence-mysql-hikari.properties"));
			
//		} else {
//			return new HikariDataSource(new HikariConfig("src/main/resources/persistence-mysql-hikari.properties"));
//		}
	}

	@Bean
	public HibernateJpaVendorAdapter hibernateJpaVendorAdaptor() {
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		hibernateJpaVendorAdapter.setShowSql(false);
		hibernateJpaVendorAdapter.setGenerateDdl(true);
		hibernateJpaVendorAdapter.setDatabasePlatform(env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));

		return hibernateJpaVendorAdapter;
	}

	@Bean
	public EntityManagerFactory entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource((DataSource) JettySkeletonConfig.getBean(DataSource.class));
		factory.setPackagesToScan(new String[] { "skeleton.model.rdbms" });
		factory.setJpaVendorAdapter(hibernateJpaVendorAdaptor());
		factory.setJpaProperties(hibernateProperties());
		factory.afterPropertiesSet();

		return factory.getObject();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory());

		return txManager;
	}

	@Bean
	public TransactionTemplate transactionTemplate() {
		TransactionTemplate transactionTemplate = new TransactionTemplate();
		transactionTemplate.setTransactionManager(transactionManager());

		return transactionTemplate;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	private Properties hibernateProperties() {
		Properties props = new Properties();
		props.setProperty("hibernate.hbm2ddl.auto", env.getRequiredProperty(PROPERTY_NAME_HBM2DDL_AUTO));
		props.setProperty("hibernate.dialect", env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
		props.setProperty("hibernate.ejb.naming_strategy", env.getRequiredProperty(PROPERTY_NAME_EJB_NAMING_STRATEGY));
		props.setProperty("hibernate.globally_quoted_identifiers", env.getRequiredProperty(PROPERTY_NAME_GLOBALLY_QUOTED_IDENTIFIERS));
		props.setProperty("org.hibernate.envers.audit_table_suffix", env.getRequiredProperty(PROPERTY_NAME_ENVERS_AUDIT_TABLE_SUFFIX));
		props.setProperty("hibernate.show_sql", env.getRequiredProperty(PROPERTY_NAME_SHOW_SQL));
		props.setProperty("log4j.logger.org.hibernate", env.getRequiredProperty(PROPERTY_NAME_LOGGER_LEVEL));
		return props;
	}

	public String getProperty(String key) {
		return env.getProperty(key);
	}
}
