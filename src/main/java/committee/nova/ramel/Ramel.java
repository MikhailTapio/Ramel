package committee.nova.ramel;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;

@Mod("ramel")
public class Ramel {
    private static final ForgeConfigSpec CFG;

    public static final ForgeConfigSpec.DoubleValue ramImpactRange;
    public static final ForgeConfigSpec.DoubleValue ramDamage;
    public static final ForgeConfigSpec.DoubleValue ramKnockBackMultiplier;

    static {
        final var builder = new ForgeConfigSpec.Builder();
        builder.comment("Ramel Settings").push("general");
        ramImpactRange = builder.comment("Additional radius of area affected by a camel's ramming. For adult camels, the radius is twice")
                .defineInRange("ramImpactRange", 0.5, 0.0, 2.5);
        ramDamage = builder.comment("The base damage of a camel's ramming, an adult camel's ramming can deal twice as much damage.")
                .defineInRange("ramDamage", 1.0, 0.0, 20.0);
        ramKnockBackMultiplier = builder.comment("The multiplier of the force of knockback dealt by a camel's ramming")
                .defineInRange("ramKnockBackMultiplier", 1.0, 0.0, 5.0);
        builder.pop();
        CFG = builder.build();
    }

    public Ramel() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CFG);
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (r, s) -> true));
    }
}
