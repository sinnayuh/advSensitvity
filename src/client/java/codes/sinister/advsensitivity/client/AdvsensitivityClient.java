package codes.sinister.advsensitivity.client;

import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import java.text.DecimalFormat;

public class AdvsensitivityClient implements ClientModInitializer {
    private final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.######");
    private final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("#");

    @Override
    public void onInitializeClient() {
        registerCommands();
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("sens")
                    .then(ClientCommandManager.argument("value", FloatArgumentType.floatArg(0.0f))
                            .executes(context -> {
                                float input = FloatArgumentType.getFloat(context, "value");
                                double sensitivity;

                                if (input > 1) {
                                    sensitivity = input / 200.0;
                                } else {
                                    sensitivity = input;
                                }

                                MinecraftClient.getInstance().options.getMouseSensitivity().setValue(sensitivity);
                                MinecraftClient.getInstance().options.write();

                                sendMessage(String.format("§cSet sensitivity to %s (%s%%)",
                                        DECIMAL_FORMAT.format(sensitivity),
                                        PERCENTAGE_FORMAT.format(sensitivity * 200)));

                                return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                            }))
                    .executes(context -> {
                        double currentSens = MinecraftClient.getInstance().options.getMouseSensitivity().getValue();
                        sendMessage(String.format("§cCurrent sensitivity: %s (%s%%)",
                                DECIMAL_FORMAT.format(currentSens),
                                PERCENTAGE_FORMAT.format(currentSens * 200)));
                        sendMessage("§cUsage: /sens <int/float>");
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    }));
        });
    }

    private void sendMessage(String message) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal(message));
        }
    }
}