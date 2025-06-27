package com.infrium.core.player;

//import org.spongepowered.api.entity.living.player.Player;
//import org.spongepowered.api.network.ServerSideConnection;


public class SpongeInfriumPlayer/* extends AbstractInfriumPlayer<Player>*/ {

//    public SpongeInfriumPlayer(Player playerObject) {
//        super(playerObject);
//    }
//
//    @Override
//    public void sendMessage(@NonNull Component component) {
//        this.playerObject.sendMessage(component);
//    }
//
//    @Override
//    public void sendActionBar(@NonNull Component component) {
//        this.playerObject.sendActionBar(component);
//    }
//
//    @Override
//    public void sendTitle(
//            @NonNull Component title,
//            @NonNull Component subtitle,
//            @NonNull long fadeIn,
//            @NonNull long stay,
//            @NonNull long fadeOut) {
//        Title.Times times = Title.Times.of(
//                Duration.ofMillis(fadeIn * 50),
//                Duration.ofMillis(stay * 50),
//                Duration.ofMillis(fadeOut * 50)
//        );
//        Title titleToSend = Title.title(title, subtitle, times);
//        this.playerObject.showTitle(titleToSend);
//    }
//
//    @Override
//    public void disconnect(@NonNull Component component) {
//        this.playerObject.kick(component);
//    }
//
//    @Override
//    public String getUsername() {
//        return this.playerObject.name();
//    }
//
//    @Override
//    public UUID getUniqueId() {
//        return this.playerObject.uniqueId();
//    }
//
//    @Override
//    public boolean isOnline() {
//        return this.playerObject;
//    }
//
//    @Override
//    public Optional<InetSocketAddress> getAddress() {
//        return this.playerObject.connection().address();
//    }
}