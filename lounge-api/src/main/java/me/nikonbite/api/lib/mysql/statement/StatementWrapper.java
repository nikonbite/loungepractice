package me.nikonbite.api.lib.mysql.statement;

import me.nikonbite.api.lib.mysql.HikariConnection;
import me.nikonbite.api.lib.mysql.response.ResponseHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class StatementWrapper {
  private final HikariConnection database;
  
  private String query;
  
  private boolean sync;
  
  public StatementWrapper(HikariConnection database) {
    this.database = database;
  }
  
  public static StatementWrapper create(HikariConnection database, String query) {
    return (new StatementWrapper(database)).setQuery(query);
  }
  
  public StatementWrapper setQuery(String query) {
    this.query = query;
    return this;
  }
  
  public StatementWrapper sync() {
    this.sync = true;
    return this;
  }
  
  private PreparedStatement createStatement(int generatedKeys, Object... objects) throws SQLException {
    PreparedStatement ps = this.database.getConnection().prepareStatement(this.query, generatedKeys);
    if (objects != null)
      for (int i = 0; i < objects.length; i++)
        ps.setObject(i + 1, objects[i]);  
    if (objects == null || objects.length == 0)
      ps.clearParameters(); 
    return ps;
  }
  
  private void validateQuery() {
    if (this.query == null || this.query.isEmpty())
      throw new IllegalStateException("Ошибка, query = null"); 
  }
  
  public int execute(int generatedKeys, Object... objects) {
    validateQuery();
    Callable<Integer> callable = () -> {
        try (PreparedStatement ps = createStatement(generatedKeys, objects)) {
          ps.execute();
          ResultSet rs = ps.getGeneratedKeys();
          return rs.next() ? rs.getInt(1) : -1;
        } 
      };
    return handle(callable);
  }
  
  public <T> T executeQuery(ResponseHandler<ResultSet, T> handler, Object... objects) {
    validateQuery();
    Callable<T> callable = () -> {
        try (PreparedStatement ps = createStatement(2, objects)) {
          ResultSet rs = ps.executeQuery();
          return handler.handleResponse(rs);
        } 
      };
    return handle(callable);
  }
  
  private <T> T handle(Callable<T> callable) {
    if (!this.sync) {
      Future<T> future = HikariConnection.QUERY_EXECUTOR.submit(callable);
      try {
        return future.get();
      } catch (InterruptedException|java.util.concurrent.ExecutionException e) {
        throw new RuntimeException("Failed to execute async query ", e);
      } 
    } 
    try {
      return callable.call();
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute sync query ", e);
    } 
  }
}
