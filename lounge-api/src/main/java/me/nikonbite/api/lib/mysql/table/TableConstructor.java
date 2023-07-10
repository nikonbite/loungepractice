package me.nikonbite.api.lib.mysql.table;

import me.nikonbite.api.lib.mysql.HikariConnectionImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class TableConstructor {
  private final String name;
  
  private final List<TableColumn> tableColumns;
  
  public String getName() {
    return this.name;
  }
  
  private final List<String> columns = new ArrayList<>();
  
  public TableConstructor(String name, TableColumn... tableColumns) {
    this.name = name;
    this.tableColumns = Arrays.asList(tableColumns);
  }
  
  public void addIndex(String column) {
    this.columns.add(column);
  }
  
  public String toString() {
    String columnSql = this.tableColumns.stream().map(Object::toString).collect(Collectors.joining(", "));
    String primary = this.tableColumns.stream().filter(TableColumn::isPrimaryKey).map(TableColumn::getName).collect(Collectors.joining(", "));
    if (!primary.isEmpty())
      columnSql = columnSql + ", PRIMARY KEY (" + primary + ")"; 
    return "CREATE TABLE IF NOT EXISTS `" + this.name + "` (" + columnSql + ") ENGINE=InnoDB CHARACTER SET utf8 COLLATE utf8_general_ci;";
  }
  
  public void create(HikariConnectionImpl database) {
    database.execute(toString(), new Object[0]);
    for (String columnName : this.columns)
      database.execute("ALTER TABLE `" + this.name + "` ADD INDEX (`" + columnName + "`);", new Object[0]); 
  }
}
