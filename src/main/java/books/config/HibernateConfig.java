package books.config;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("books.model"); // Пакет с вашими сущностями
        factoryBean.setPersistenceProvider(new HibernatePersistenceProvider());

        // Настройки для Hibernate
        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.show_sql", "true");

        // Включение второго уровня кэширования
        hibernateProperties.put("hibernate.cache.use_second_level_cache", "true");
        hibernateProperties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.jcache.JCacheRegionFactory");
        hibernateProperties.put("hibernate.javax.cache.provider", "org.ehcache.jsr107.EhcacheCachingProvider");
        hibernateProperties.put("hibernate.javax.cache.missing_cache_strategy", "create");
        hibernateProperties.put("hibernate.cache.use_query_cache", "true");

        factoryBean.setJpaProperties(hibernateProperties);

        // Используем Hibernate как JPA-поставщика
        JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);

        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}

