package me.nikonbite.api.lib.mysql.table;

/**
 @author milansky on 21.07.22
 */
public final class TableColumn {
  private final String name;
  
  private final ColumnType columnType;
  
  private boolean nullValue;
  
  private boolean primaryKey;
  
  private boolean unigue;
  
  private boolean autoIncrement;
  
  private Object defaultValue;
  
  public TableColumn(String name, ColumnType columnType) {
    this.name = name;
    this.columnType = columnType;
  }
  
  public TableColumn setNull(boolean nullValue) {
    this.nullValue = nullValue;
    return this;
  }
  
  public TableColumn primaryKey(boolean primaryKey) {
    this.primaryKey = primaryKey;
    return this;
  }
  
  public TableColumn unigue(boolean unigue) {
    this.unigue = unigue;
    return this;
  }
  
  public TableColumn autoIncrement(boolean autoIncrement) {
    this.autoIncrement = autoIncrement;
    return this;
  }
  
  private String getDefaultValueString() {
    return (this.defaultValue == null) ? "" : ("'" + this.defaultValue + "'");
  }
  
  public String toString() {
    return "`" + this.name + "` " + this.columnType.getSql() + (this.nullValue ? "" : " NOT NULL") + (!this.unigue ? "" : " UNIQUE") + ((this.defaultValue == null) ? "" : (" DEFAULT " + 
      
      getDefaultValueString())) + (!this.autoIncrement ? "" : " AUTO_INCREMENT");
  }
  
  public boolean isAutoIncrement() {
    return this.autoIncrement;
  }
  
  public void setAutoIncrement(boolean autoIncrement) {
    this.autoIncrement = autoIncrement;
  }
  
  public boolean isNullValue() {
    return this.nullValue;
  }
  
  public void setNullValue(boolean nullValue) {
    this.nullValue = nullValue;
  }
  
  public boolean isPrimaryKey() {
    return this.primaryKey;
  }
  
  public void setPrimaryKey(boolean primaryKey) {
    this.primaryKey = primaryKey;
  }
  
  public boolean isUnigue() {
    return this.unigue;
  }
  
  public void setUnigue(boolean unigue) {
    this.unigue = unigue;
  }
  
  public String getName() {
    return this.name;
  }
  
  public Object getDefaultValue() {
    return this.defaultValue;
  }
  
  public TableColumn setDefaultValue(Object defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }
  
  public ColumnType getColumnType() {
    return this.columnType;
  }
}
