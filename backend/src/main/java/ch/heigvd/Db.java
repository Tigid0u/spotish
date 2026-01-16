package ch.heigvd;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class Db {

  private Db() {
  } // This class should not be instanciated

  public static DataSource createDataSource() {
    HikariConfig config = new HikariConfig();

    String jdbcUrl = getEnvOrThrow("JDBC_URL");
    String username = getEnvOrThrow("POSTGRES_USER_USERNAME");
    String password = getEnvOrThrow("POSTGRES_USER_PASSWORD");

    config.setJdbcUrl(jdbcUrl);
    config.setUsername(username);
    config.setPassword(password);

    config.setMaximumPoolSize(10);
    config.setMinimumIdle(2);
    config.setConnectionTimeout(10_000);

    return new HikariDataSource(config);
  }

  private static String getEnvOrThrow(String name) {
    String value = System.getenv(name);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException(
          "Missing required environment variable: " + name);
    }
    return value;
  }
}
