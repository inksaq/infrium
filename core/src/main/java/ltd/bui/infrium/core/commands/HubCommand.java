package ltd.bui.infrium.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import ltd.bui.infrium.api.hive.enums.CloudChannels;
import ltd.bui.infrium.api.hive.enums.QueueChannels;
import ltd.bui.infrium.api.hive.enums.QueueLeftReason;
import ltd.bui.infrium.api.hive.pubsub.queue.RedisQueueConnect;
import ltd.bui.infrium.api.hive.pubsub.queue.RedisQueueJoin;
import ltd.bui.infrium.api.hive.servers.Server;
import ltd.bui.infrium.core.InfriumCore;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static ltd.bui.infrium.api.hive.ServerRepository.LAGGY_TPS;
import static ltd.bui.infrium.api.hive.enums.ServerType.LOBBY;

public class HubCommand extends BaseCommand
{

}
