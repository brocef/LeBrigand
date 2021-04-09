/*
 * Don't go off selling software that uses this.
 */

package lebrigand.core;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import lebrigand.bots.BotCreationFailedException;
import lebrigand.bots.SpyglassBot;
import lebrigand.bots.bilge.BilgeBot;
import lebrigand.bots.rigging.RiggingBot;
import lebrigand.core.spyglass.LeBrigandActuator;
import lebrigand.core.spyglass.Spyglass;

public class BotManager {
    private static final Logger logger = Logger.getLogger(BotManager.class.getName());
    private SpyglassBot currentBot;
    private Thread currentBotThread;
    private final List<Class<? extends SpyglassBot>> availableBotClasses;
    private final Map<String, Class<? extends SpyglassBot>> botNameClassMap;
    
    private final Spyglass spy;
    private final LeBrigandActuator actuator;
    
    public BotManager(Spyglass spy, LeBrigandActuator actuator) {
        this.spy = spy;
        this.actuator = actuator;
        this.currentBot = null;
        this.currentBotThread = null;
        this.availableBotClasses = new ArrayList<>();
        this.botNameClassMap = new HashMap<>();
        this.addDefaultBots();
    }
    
    private void addDefaultBots() {
        this.addBot(BilgeBot.class);
        this.addBot(RiggingBot.class);
    }
    
    public void addBot(Class<? extends SpyglassBot> botClass) {
        String name = botClass.getSimpleName();
        if (this.botNameClassMap.containsKey(name))
            throw new RuntimeException();
        this.availableBotClasses.add(botClass);
        this.botNameClassMap.put(name, botClass);
    }

    public String getCurrentBotName() {
        if (!this.isBotLoaded())
            return "null";
        return this.currentBot.getBotName();
    }
    
    public String[] getAvailableBotClassNames() {
        return this.botNameClassMap.keySet().toArray(new String[this.botNameClassMap.size()]);
    }
    
    public boolean isBotRunning() {
        return this.isBotLoaded() && this.currentBotThread.isAlive();
    }
    
    public boolean isBotLoaded() {
        return this.currentBot != null;
    }
    
    public void stopBot() {
        // Interrupt first since killBot() might block due to syncronized section
        this.currentBotThread.interrupt();
        this.currentBot.killBot();
    }
    
    public void startBot() {
        this.currentBotThread.start();
    }
    
    public void loadBot(String botClassName) throws BotCreationFailedException {
        if (this.isBotRunning()) {
            BotManager.logger.log(Level.WARNING, "{0} was still running, signaling that bot to start before starting {1}", new Object[]{this.getCurrentBotName(), botClassName});
            this.stopBot();
        }
        this.currentBot = this.createBot(botClassName);
        this.currentBotThread = new Thread(this.currentBot);
    }
    
    private SpyglassBot createBot(String botClassName) throws BotCreationFailedException {
        Class<? extends SpyglassBot> botClass = this.botNameClassMap.get(botClassName);
        
        try {
            return botClass.getConstructor(Spyglass.class, LeBrigandActuator.class).newInstance(this.spy, this.actuator);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            BotManager.logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        throw new BotCreationFailedException(botClass);
    }
}
