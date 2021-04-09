/*
 * Don't go off selling software that uses this.
 */

package lebrigand.bots;

public class BotCreationFailedException extends Exception {
    private final Class<? extends BrigandBot> botClass;
    
    public BotCreationFailedException(Class<? extends BrigandBot> botClass) {
        this.botClass = botClass;
    }
    
    public Class<? extends BrigandBot> getBotClass() {
        return this.botClass;
    }
}
