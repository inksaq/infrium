package ltd.bui.infrium.api.data;

import lombok.Getter;

@Getter
public enum Rank {
  OWNER("OWNER", "<bold><gradient:#7D3FB1:#E55554:#F8821D>OWNER</gradient></bold>", 0),
  ADMIN("ADMIN", "&4&lADMIN", 1),
  MODERATOR("MODERATOR", "&b&lMODERATOR", 2),
  DEFAULT("DEFAULT", "", 3),
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
    return this.ladder <= 2;
  }

  public boolean isDefault() {
    return this.ladder == 3;
  }

  public boolean isOwnerOrAdmin() {
    return this.ladder <= 1;
  }
}
