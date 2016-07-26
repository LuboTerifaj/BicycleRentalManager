/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.BicycleRental;

import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @author Samo
 */

@Configuration
@EnableTransactionManagement
public class SpringConfig {
    
    private static final String dbUrl = "jdbc:derby:memory:bicyclerentaldb";
    private String scriptPath = System.getProperty("user.dir") + "\\src\\main\\java\\cz\\muni\\fi\\BicycleRental\\customerTable.sql";

    @Bean
    public DataSource dataSource() {
        BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName("org.apache.derby.jdbc.ClientDriver");
        bds.setUrl(dbUrl);
        return bds;
    }
    
    @Bean
    public PlatformTransactionManager transactionManager(){
        return new DataSourceTransactionManager(dataSource());
    }
    
    @Bean
    public BicycleManager bicycleManager(){
        return new BicycleManagerImpl(dataSource());
    }
    
    @Bean
    public CustomerManager customerManager(){
        return new CustomerManagerImpl(dataSource());
    }
    
    @Bean
    public RentalManager rentalManager(){
        return new RentalManagerImpl(dataSource());
    }

}
