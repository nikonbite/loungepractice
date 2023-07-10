package me.nikonbite.api.lib.mysql;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.nikonbite.api.lib.mysql.query.Query;
import me.nikonbite.api.lib.mysql.response.ResponseHandler;
import me.nikonbite.api.lib.mysql.statement.StatementWrapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface HikariConnection {
  ExecutorService QUERY_EXECUTOR = Executors.newCachedThreadPool((new ThreadFactoryBuilder())
      
      .setNameFormat("MySQL-Worker #%s")
      .setDaemon(true)
      .build());
  
  int execute(String paramString, Object... paramVarArgs);
  
  int execute(Query paramQuery);
  
  int execute(StatementWrapper paramStatementWrapper, Object... paramVarArgs);
  
  <T> T executeQuery(String paramString, ResponseHandler<ResultSet, T> paramResponseHandler, Object... paramVarArgs);
  
  <T> T executeQuery(Query paramQuery, ResponseHandler<ResultSet, T> paramResponseHandler);
  
  <T> T executeQuery(StatementWrapper paramStatementWrapper, ResponseHandler<ResultSet, T> paramResponseHandler, Object... paramVarArgs);
  
  void close();
  
  Connection getConnection();
}
