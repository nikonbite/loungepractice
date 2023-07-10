package me.nikonbite.api.lib.mysql.query;

/**
 @author milansky on 21.07.22
 */
public enum QuerySymbol {
  EQUALLY("="),
  MORE_OR_EQUAL(">="),
  MORE(">"),
  LESS("<"),
  LESS_OR_EQUAL("<="),
  NOT_EQUAL("!=");
  
  private final String symbol;
  
  QuerySymbol(String string2) {
    this.symbol = string2;
  }
  
  public String getSymbol() {
    return this.symbol;
  }
}
