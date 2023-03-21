package ltd.bui.infrium.api.punishments;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Punishment {

  @SerializedName("type")
  private PunishmentType type;

  @SerializedName("reason")
  private String reason;

  @SerializedName("created_at")
  private long createdAt;

  @SerializedName("duration")
  private long duration;

  @SerializedName("issuer")
  private String issuer;

  @SerializedName("player")
  private String player;

  @SerializedName("active")
  private boolean active;

  public boolean isPermanent() {
    return duration == -1;
  }

  public void setPermanent() {
    this.duration = -1;
  }

  public boolean isApplicable() {
    return (this.isPermanent() || (System.currentTimeMillis() >= (this.createdAt + this.duration)))
        && this.active;
  }
}
