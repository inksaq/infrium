package ltd.bui.infrium.core.sponge.configuration;

import ltd.bui.infrium.api.configuration.ConfigurationContainer;
import ltd.bui.infrium.api.configuration.IConfigurationEnum;
import ltd.bui.infrium.core.sponge.InfriumCoreSponge;
import lombok.Getter;

@Getter
public enum CoreConfiguration implements IConfigurationEnum {
    CHAT_ENABLED("chat.enabled", true),
    CHAT_FILTER_REPETITION("chat.filter.repetition", true),
    CHAT_FILTER_SWEAR("chat.filter.swear", true),
    CHAT_FILTER_ONLY_STAFF("chat.filter.only-staff", false),
    CHAT_GLOBAL_ENABLED("chat.global.enabled", true),
    CHAT_GLOBAL_PREFIX("chat.global.prefix", "-");

    private final String key;
    private final Object defaultValue;

    CoreConfiguration(String key, Object defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override
    public final ConfigurationContainer<?> getConfig() {
        return (ConfigurationContainer<?>) InfriumCoreSponge.getInstance().getConfiguration();
    }
}
