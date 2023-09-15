package fi.dy.masa.malilib;

import com.ibm.icu.impl.Pair;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fi.dy.masa.malilib.event.InitializationHandler;

@Mod(MaLiLibReference.MOD_ID)
public class MaLiLib {
    public static final Logger logger = LogManager.getLogger(MaLiLibReference.MOD_ID);

    public MaLiLib() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::onClientSetup);
        modBus.addListener(this::onInterModProcess);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        ModLoadingContext ctx = ModLoadingContext.get();
        ctx.registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> NetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
        ctx.registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> {
                    var gui = new MaLiLibConfigGui();
                    gui.setParent(screen);
                    return gui;
                }));

        MinecraftForge.EVENT_BUS.register(new ForgeInputEventHandler());
        MinecraftForge.EVENT_BUS.register(new ForgeTickEventHandler());
        MinecraftForge.EVENT_BUS.register(new ForgeRenderEventHandler());

        InitializationHandler.getInstance().registerInitializationHandler(new MaLiLibInitHandler());
    }

    private void onInterModProcess(final InterModProcessEvent event) {
        ((InitializationHandler) InitializationHandler.getInstance()).onGameInitDone();
    }
}
