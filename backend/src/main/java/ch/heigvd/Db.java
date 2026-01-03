package ch.heigvd;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class Db {

  private Db() {
  } // This class should not be instanciated

  public static DataSource createDataSource() {
    HikariConfig config = new HikariConfig();

    config.setJdbcUrl("jdbc:postgresql://localhost:5432/bdr");
    config.setUsername("bdr");
    config.setPassword("bdr");

    config.setMaximumPoolSize(10);
    config.setMinimumIdle(2);
    config.setConnectionTimeout(10_000);

    return new HikariDataSource(config);
  }
}
