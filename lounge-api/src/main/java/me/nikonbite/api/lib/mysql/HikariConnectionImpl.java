package me.nikonbite.api.lib.mysql;

import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.NonNull;
import lombok.ToString;
import me.nikonbite.api.lib.mysql.query.Query;
import me.nikonbite.api.lib.mysql.response.ResponseHandler;
import me.nikonbite.api.lib.mysql.statement.StatementWrapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@ToString
public class HikariConnectionImpl implements HikariConnection {
  private final String host;
  
  private final String pass;
  
  private final String user;
  
  private final String data;
  
  private final HikariDataSource dataSource;
  
  private Connection connection = null;
  
  public static MySqlBuilder builder() {
    return new MySqlBuilder();
  }

  @ToString
  public static class MySqlBuilder {
    private String host;
    
    private String pass;
    
    private String user;
    private String data;
    
    public MySqlBuilder host(String host) {
      this.host = host;
      return this;
    }
    
    public MySqlBuilder pass(String pass) {
      this.pass = pass;
      return this;
    }
    
    public MySqlBuilder user(String user) {
      this.user = user;
      return this;
    }
    
    public MySqlBuilder data(String data) {
      this.data = data;
      return this;
    }
    
    public HikariConnectionImpl build() {
      return new HikariConnectionImpl(this.host, this.pass, this.user, this.data);
    }
  }
  
  public HikariConnectionImpl(String host, String pass, String user, String data) {
    this.host = host;
    this.pass = pass;
    this.user = user;
    this.data = data;
    this.dataSource = configureDataSource(new HikariConfig());
  }
  
  private HikariDataSource configureDataSource(HikariConfig source) {
    source.setJdbcUrl("jdbc:mysql://" + this.host + ":3306/" + this.data);
    source.setDriverClassName("com.mysql.cj.jdbc.Driver");
    source.setUsername(this.user);
    source.setPassword(this.pass);
    source.setMaximumPoolSize(10);
    source.setMinimumIdle(10);
    source.setMaxLifetime(1800000L);
    source.setKeepaliveTime(0L);
    source.setConnectionTimeout(5000L);
    source.setInitializationFailTimeout(-1L);
    Map<String, Object> properties = Maps.newConcurrentMap();
    properties.putIfAbsent("useSSL", "false");
    properties.putIfAbsent("autoReconnect", "true");
    properties.putIfAbsent("cachePrepStmts", "true");
    properties.putIfAbsent("prepStmtCacheSize", "250");
    properties.putIfAbsent("prepStmtCacheSqlLimit", "2048");
    properties.putIfAbsent("useServerPrepStmts", "true");
    properties.putIfAbsent("useLocalSessionState", "true");
    properties.putIfAbsent("rewriteBatchedStatements", "true");
    properties.putIfAbsent("cacheResultSetMetadata", "true");
    properties.putIfAbsent("cacheServerConfiguration", "true");
    properties.putIfAbsent("elideSetAutoCommits", "true");
    properties.putIfAbsent("maintainTimeStats", "false");
    properties.putIfAbsent("alwaysSendSetIsolation", "false");
    properties.putIfAbsent("cacheCallableStmts", "true");
    properties.putIfAbsent("serverTimezone", "UTC");
    for (Map.Entry<String, Object> entry : properties.entrySet())
      source.addDataSourceProperty(entry.getKey(), entry.getValue()); 
    return new HikariDataSource(source);
  }
  
  public int execute(String query, Object... objects) {
    return execute(StatementWrapper.create(this, query), objects);
  }
  
  public int execute(Query query) {
    return execute(query.toString());
  }
  
  public <T> T executeQuery(String query, ResponseHandler<ResultSet, T> handler, Object... objects) {
    return executeQuery(StatementWrapper.create(this, query), handler, objects);
  }
  
  public <T> T executeQuery(Query query, ResponseHandler<ResultSet, T> responseHandler) {
    return executeQuery(query.toString(), responseHandler);
  }

  public int createTable(@NonNull String mysqlTable, @NonNull String tableStructure) {

    return execute(String.format("CREATE TABLE IF NOT EXISTS `%s` (%s)", mysqlTable, tableStructure));

  }
  public int execute(StatementWrapper wrapper, Object... objects) {
    return wrapper.execute(1, objects);
  }
  
  public <T> Supplier<T> supplyQuery(Function<HikariConnectionImpl, T> function) {
    return () -> executeQuery(function);
  }
  
  public <T> T executeQuery(Function<HikariConnectionImpl, T> function) {
    T type = null;
    try {
      type = function.apply(this);
    } catch (Exception e) {
      e.printStackTrace();
    } 
    return type;
  }
  
  public <T> T executeQuery(StatementWrapper wrapper, ResponseHandler<ResultSet, T> handler, Object... objects) {
    return wrapper.executeQuery(handler, objects);
  }
  
  public Connection getConnection() {
    refreshConnection();
    return this.connection;
  }
  
  protected void refreshConnection() {
    try {
      if (this.connection != null && !this.connection.isClosed() && this.connection.isValid(1000))
        return; 
      this.connection = this.dataSource.getConnection();
    } catch (SQLException e) {
      throw new RuntimeException("Произошла ошибка - " + this.host + "/" + this.data + " ", e);
    } 
  }
  
  public void close() {
    try {
      this.connection.close();
    } catch (SQLException ignored) {}
  }

}
