package me.nikonbite.api.lib.mysql.query;

import java.util.*;
import java.util.stream.Collectors;

/**
 @author milansky on 21.07.22
 */
public class MysqlQuery {
  public static Select selectFrom(String var0) {
    return new Select(var0);
  }
  
  public static Insert insertTo(String var0) {
    return new Insert(var0);
  }
  
  public static Update update(String var0) {
    return new Update(var0);
  }
  
  public static Delete deleteFrom(String var0) {
    return new Delete(var0);
  }
  
  public static class Delete implements Query {
    private final String table;
    
    private final List<Where> wheres;
    
    private int limitSize;
    
    private Delete(String var1) {
      this.wheres = new ArrayList<>();
      this.limitSize = 0;
      this.table = var1;
    }
    
    Delete(String var1, Object var2) {
      this(var1);
    }
    
    public Delete where(String var1, QuerySymbol var2, Object var3) {
      this.wheres.add(new Where(var1, var2, var3));
      return this;
    }
    
    public Delete limit() {
      return limit(1);
    }
    
    public Delete limit(int var1) {
      if (var1 < 0)
        return this; 
      this.limitSize = var1;
      return this;
    }
    
    public String toString() {
      if (this.wheres.size() < 1)
        throw new NullPointerException("WHERE SIZE < 1"); 
      StringBuilder var1 = new StringBuilder();
      int var2 = 0;
      Iterator<Where> var3 = this.wheres.iterator();
      while (var3.hasNext()) {
        Where var4 = var3.next();
        Object var5 = var4.result;
        var1.append("`").append(var4.column).append("`").append(" ").append(var4.symbol.getSymbol()).append(" ").append((var5 instanceof String) ? ("\"" + var5 + "\"") : ("'" + var5 + "'"));
        var2++;
        if (var2 < this.wheres.size())
          var1.append(" AND "); 
      } 
      return "DELETE FROM `" + this.table + "` WHERE " + var1 + ((this.limitSize != 0) ? (" LIMIT " + this.limitSize) : "") + ";";
    }
  }
  
  public static class Update implements Query {
    private final String table;
    
    private final List<Where> wheres;
    
    private final Map<String, Object> sets;
    
    private final Map<String, Object> adds;
    
    private int limitSize;
    
    private Update(String var1) {
      this.wheres = new ArrayList<>();
      this.sets = new HashMap<>();
      this.adds = new HashMap<>();
      this.limitSize = 0;
      this.table = var1;
    }
    
    Update(String var1, Object var2) {
      this(var1);
    }
    
    public Update set(String var1, Object var2) {
      this.sets.put(var1, var2);
      return this;
    }
    
    public Update add(String var1, Object var2) {
      this.adds.put(var1, var2);
      return this;
    }
    
    public Update where(String var1, QuerySymbol var2, Object var3) {
      this.wheres.add(new Where(var1, var2, var3));
      return this;
    }
    
    public Update limit() {
      return limit(1);
    }
    
    public Update limit(int var1) {
      if (var1 < 0)
        return this; 
      this.limitSize = var1;
      return this;
    }
    
    public String toString() {
      if (this.wheres.size() < 1)
        throw new NullPointerException("WHERE SIZE < 1"); 
      StringBuilder var1 = new StringBuilder();
      int var2 = 0;
      Iterator<Map.Entry<String, Object>> var3 = this.sets.entrySet().iterator();
      while (true) {
        if (!var3.hasNext()) {
          var2 = 0;
          var3 = this.adds.entrySet().iterator();
          while (var3.hasNext()) {
            Map.Entry entry = var3.next();
            Object object = entry.getValue();
            var1.append("`").append((String)entry.getKey()).append("`").append(" = `").append((String)entry.getKey()).append("` + ").append((object instanceof String) ? ("\"" + object + "\"") : object);
            var2++;
            if (var2 < this.adds.size())
              var1.append(", "); 
          } 
          StringBuilder var7 = new StringBuilder();
          var2 = 0;
          Iterator<Where> var8 = this.wheres.iterator();
          while (var8.hasNext()) {
            Where var9 = var8.next();
            Object var6 = var9.result;
            var7.append("`").append(var9.column).append("`").append(" ").append(var9.symbol.getSymbol()).append(" ").append((var6 instanceof String) ? ("\"" + var6 + "\"") : var6);
            var2++;
            if (var2 < this.wheres.size())
              var7.append(" AND "); 
          } 
          return "UPDATE `" + this.table + "` SET " + var1 + " WHERE " + var7 + ((this.limitSize != 0) ? (" LIMIT " + this.limitSize) : "") + ";";
        } 
        Map.Entry var4 = var3.next();
        Object var5 = var4.getValue();
        var1.append("`").append((String)var4.getKey()).append("`").append(" = ").append((var5 instanceof String) ? ("\"" + var5 + "\"") : ("'" + var5 + "'"));
        var2++;
        if (var2 < this.sets.size() || this.adds.size() != 0)
          var1.append(", "); 
      } 
    }
  }
  
  public static class Insert implements Query {
    private final String table;
    
    private final Map<String, Object> inserts;
    
    private final Map<String, Object> duplicate;
    
    private Insert(String var1) {
      this.inserts = new HashMap<>();
      this.duplicate = new HashMap<>();
      this.table = var1;
    }
    
    Insert(String var1, Object var2) {
      this(var1);
    }
    
    public Insert set(String var1, Object var2) {
      this.inserts.put(var1, var2);
      return this;
    }
    
    public Insert setDuplicate(String var1, Object var2) {
      this.duplicate.put(var1, var2);
      return this;
    }
    
    public String toString() {
      String var1 = this.inserts.keySet().stream().map(var0 -> "`" + var0 + "`").collect(Collectors.joining(", "));
      String var2 = this.inserts.values().stream().map(Object::toString).map(var0 -> "'" + var0 + "'").collect(Collectors.joining(", "));
      StringBuilder var3 = new StringBuilder();
      int var4 = 0;
      Iterator<Map.Entry<String, Object>> var5 = this.duplicate.entrySet().iterator();
      while (var5.hasNext()) {
        Map.Entry<String, Object> var6 = var5.next();
        Object var7 = var6.getValue();
        var3.append("`").append(var6.getKey()).append("` = ").append((var7 instanceof String) ? ("\"" + var7 + "\"") : ("'" + var7 + "'"));
        var4++;
        if (var4 < this.duplicate.size())
          var3.append(", "); 
      } 
      return "INSERT INTO `" + this.table + "` (" + var1 + ") VALUES (" + var2 + ")" + ((this.duplicate.size() > 0) ? (" ON DUPLICATE KEY UPDATE " + var3) : "") + ";";
    }
  }
  
  private static class Where {
    private final String column;
    
    private final QuerySymbol symbol;
    
    private final Object result;
    
    public Where(String var1, QuerySymbol var2, Object var3) {
      this.column = var1;
      this.symbol = var2;
      this.result = var3;
    }
  }
  
  public static class Select implements Query {
    private final String table;
    
    private final List<Where> wheres;
    
    private String result;
    
    private int limitSize;
    
    private Select(String var1) {
      this.wheres = new ArrayList<>();
      this.limitSize = 0;
      this.table = var1;
    }
    
    Select(String var1, Object var2) {
      this(var1);
    }
    
    public Select where(String var1, QuerySymbol var2, Object var3) {
      this.wheres.add(new Where(var1, var2, var3));
      return this;
    }
    
    public Select limit() {
      return limit(1);
    }
    
    public Select limit(int var1) {
      if (var1 < 0)
        return this; 
      this.limitSize = var1;
      return this;
    }
    
    public Select result(String var1) {
      if (!var1.equals("*"))
        this.result = var1; 
      return this;
    }
    
    public String toString() {
      if (this.wheres.size() < 1)
        throw new NullPointerException("WHERE SIZE < 1"); 
      StringBuilder var1 = new StringBuilder();
      int var2 = 0;
      for (Where var4 : this.wheres) {
        Object var5 = var4.result;
        var1.append("`").append(var4.column).append("` ").append(var4.symbol.getSymbol()).append(" ").append((var5 instanceof String) ? ("\"" + var5 + "\"") : ("'" + var5 + "'"));
        var2++;
        if (var2 < this.wheres.size())
          var1.append(" AND ");
      } 
      return "SELECT " + ((this.result == null) ? "*" : ("`" + this.result + "`")) + " FROM `" + this.table + "` WHERE " + var1 + ((this.limitSize != 0) ? (" LIMIT " + this.limitSize) : "") + ";";
    }
  }
}
