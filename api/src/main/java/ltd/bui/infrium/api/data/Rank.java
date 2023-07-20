package ltd.bui.infrium.api.data;

import lombok.Getter;

@Getter
public enum Rank {
  OPERATOR("OPERATOR", "<bold><dark_aqua>OP</dark_aqua></bold><reset>", 0),
  CARETAKER("CARETAKER", "<bold><red>#</red></bold><reset>", 1),
  TRUSTED("TRUSTED", "<dark_purple>#</dark_purple><reset>", 5),
  VIP("VIP", "<gold>#</gold><reset>", 7),
  WHITELISTED("WHITELISTED", "<white>#</white><reset>", 9),
  DEFAULT("DEFAULT", "", 10),
  ;

  private final String prefix;
  private final String name;
  private final int ladder;

  Rank(String name, String prefix, int ladder) {
    this.name = name;
    this.prefix = prefix;
    this.ladder = ladder;
  }

  public boolean isStaff() {
    return this.ladder <= 5;
  }

  public boolean isDefault() {
    return this.ladder == 10;
  }

  public boolean isWhitelisted() { return this.ladder == 9 ;}

  public boolean isOpOrCaretaker() {
    return this.ladder <= 1;
  }
}
