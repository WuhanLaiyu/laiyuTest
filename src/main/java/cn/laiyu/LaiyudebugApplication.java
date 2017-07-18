package cn.laiyu;

import com.alibaba.druid.pool.DruidDataSource;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;


@SpringBootApplication(exclude = {}
)
public class LaiyudebugApplication {
    @Autowired
    private Environment env;

    public static Logger logger=LogManager.getLogger();

    public static void main(String[] args) {
        SpringApplication.run(LaiyudebugApplication.class, args);
    }

    public DataSource dataSource( ) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(env.getProperty("spring.datasource.url"));
        druidDataSource.setUsername(env.getProperty("spring.datasource.username"));
        druidDataSource.setPassword(env.getProperty("spirng.datasource.password"));
        druidDataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        druidDataSource.setInitialSize(2);//初始化时建立物理连接的个数
        druidDataSource.setMaxActive(20);//最大连接池数量
        druidDataSource.setMinIdle(0);//最小连接池数量
        druidDataSource.setMaxWait(60000);//获取连接时最大等待时间，单位毫秒。
        druidDataSource.setValidationQuery("SELECT 1");//用来检测连接是否有效的sql
        druidDataSource.setTestOnBorrow(false);//申请连接时执行validationQuery检测连接是否有效
        druidDataSource.setTestWhileIdle(true);//建议配置为true，不影响性能，并且保证安全性。
        druidDataSource.setPoolPreparedStatements(false);//是否缓存preparedStatement，也就是PSCache
        return druidDataSource;
    }
}
