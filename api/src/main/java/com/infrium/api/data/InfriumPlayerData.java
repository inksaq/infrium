package com.infrium.api.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NonNull;
import com.infrium.api.punishments.Punishment;
import com.infrium.api.punishments.PunishmentType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
public class InfriumPlayerData {

  @SerializedName(value = "username")
  private String username;

  @SerializedName(value = "_id")
  private UUID uuid;

  @SerializedName(value = "firstLogin")
  private long firstLogin;

  @SerializedName(value = "lastLogin")
  private long lastLogin;

  @SerializedName(value = "rank")
  private Rank rank;

  @SerializedName(value = "punishments")
  private List<Punishment> punishmentList;



  public InfriumPlayerData(@NonNull UUID uuid, @NonNull String username) {
    this.uuid = uuid;
    this.username = username;
    this.firstLogin = this.lastLogin = System.currentTimeMillis();
    this.rank = Rank.DEFAULT;
    this.punishmentList = new ArrayList<>();
  }

  public InfriumPlayerData() { // default player data
    this.rank = Rank.DEFAULT;
    this.username = "";
    this.firstLogin = 0;
    this.uuid = null;
  }

  public List<Punishment> getPunishmentList() {
    if (this.punishmentList == null) {
      this.punishmentList = new ArrayList<>();
    }
    if (!(this.punishmentList
        instanceof ArrayList)) { // make sure it's an arraylist, so we can edit the list
      this.punishmentList = new ArrayList<>(this.punishmentList);
    }
    return punishmentList;
  }

  public Optional<Punishment> hasPunishmentActive(PunishmentType type) {
    return this.getPunishmentList().stream()
        .filter(punishment -> punishment.getType() == type)
        .filter(Punishment::isApplicable)
        .findFirst();
  }
}
