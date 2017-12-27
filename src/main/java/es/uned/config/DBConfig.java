package es.uned.config;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 */
@Configuration
@EnableJpaRepositories(basePackages = "es.uned.repositories")
@EnableTransactionManagement
public class DBConfig {

    // Recursos con scripts SQL para inicializar la BBDD
    @Value("classpath:db/*.sql")
    Resource[] sqlScripts;

    // Base de Datos H2 (en memoria)
    @Bean(name = "dataSource")
    public DataSource getDataSource() throws IOException {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(org.h2.Driver.class);
        dataSource.setUrl("jdbc:h2:mem:OpinionesPeliculas;MODE=Oracle;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE;");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        DatabasePopulatorUtils.execute(createDatabasePopulator(), dataSource);
        return dataSource;
    }

    // Inicializar BBDD con los scripts SQL
    private DatabasePopulator createDatabasePopulator() throws IOException {
        System.out.println(sqlScripts);
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.setSqlScriptEncoding("UTF-8");
        databasePopulator.setContinueOnError(true);
        databasePopulator.addScripts(sqlScripts);
        return databasePopulator;
    }

    // Iniciar consola web H2
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2WebServer() throws SQLException {
        return Server.createWebServer("-web", "-webAllowOthers", "-webDaemon", "-webPort", "8182");
    }

    // Iniciar escucha en puerto tcp
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2TCPServer() throws  SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9192");
    }

    /* Podemos acceder a sessionFactory de Hibernate mediante:
       Session session = entityManager.unwrap(Session.class); */
    @Autowired
    @Bean(name = "entityManagerFactory")
    LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan("es.uned.entities");

        Properties jpaProperties = new Properties();

        /* Configurar el dialecto utilizado por la BBDD. Esto permite que Hibernate cree
        consultas SQL optimizadas para la BBDD utilizada. */
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

        /* Indica la acción que se invoca en la BBDD cuando al crear o cerrar SessionFactory
        de Hibernate */
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");

        /* Configura la estrategia que sigue Hibernate para crear los nombres de
        las tablas de la BBDD */
        jpaProperties.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");

        /* Si el valor es true, Hibernate vuelca en la consola todas las SQL que
         genera */
        jpaProperties.put("hibernate.show_sql", "true");

        /* Si el valor es true, Hibernate dará formato al SQL que vuelca en la consola */
        jpaProperties.put("hibernate.format_sql", "true");

        jpaProperties.put("current_session_context_class", "thread");

        // Hibernate con codificación UTF8
        jpaProperties.put("hibernate.connection.CharSet", "utf8");
        jpaProperties.put("hibernate.connection.characterEncoding", "utf8");
        jpaProperties.put("hibernate.connection.useUnicode", "true");

        entityManagerFactoryBean.setJpaProperties(jpaProperties);
        return entityManagerFactoryBean;
    }

    @Autowired
    @Bean(name = "transactionManager")
    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

}
