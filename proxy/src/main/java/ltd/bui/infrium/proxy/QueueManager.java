package ltd.bui.infrium.proxy;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import ltd.bui.infrium.api.hive.enums.ServerType;
import net.kyori.adventure.text.Component;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueManager {
    private final Map<ServerType, Queue<Player>> queues = new ConcurrentHashMap<>();
    private final Map<Player, ServerType> playerQueue = new ConcurrentHashMap<>();

    public void joinQueue(Player player, ServerType serverType) {
        queues.computeIfAbsent(serverType, k -> new ConcurrentLinkedQueue<>()).offer(player);
        playerQueue.put(player, serverType);
        updateQueuePosition(player);
    }

    public void leaveQueue(Player player) {
        ServerType serverType = playerQueue.remove(player);
        if (serverType != null) {
            queues.get(serverType).remove(player);
        }
    }

    public void processQueue(ServerType serverType, RegisteredServer server) {
        Queue<Player> queue = queues.get(serverType);
        if (queue != null && !queue.isEmpty()) {
            Player player = queue.poll();
            playerQueue.remove(player);
            player.createConnectionRequest(server).fireAndForget();
        }
    }

    private void updateQueuePosition(Player player) {
        ServerType serverType = playerQueue.get(player);
        if (serverType != null) {
            Queue<Player> queue = queues.get(serverType);
            int position = new LinkedList<>(queue).indexOf(player) + 1;
            player.sendMessage(Component.text("You are in position " + position + " for " + serverType.name()));
        }
    }

    public boolean isInQueue(Player player) {
        return playerQueue.containsKey(player);
    }

    public ServerType getQueueType(Player player) {
        return playerQueue.get(player);
    }
}