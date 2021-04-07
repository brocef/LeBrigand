package lebrigand.core.ui;

import java.util.logging.*;

import javax.swing.DefaultListModel;


public class GuiLogHandler extends Handler {
    private static Logger logger = Logger.getLogger(GuiLogHandler.class.getName());
    private DefaultListModel<String> message_model;
    
    public GuiLogHandler(DefaultListModel<String> model) {
        this.message_model = model;
        this.setFormatter(new SimpleFormatter());
        this.logger.info("GuiLogHandler initialized");
    }

    private void addLogMessage(String msg) {
		this.message_model.addElement(msg);
	}

	public void close() {

	}
	
	public void flush() {

	}

	public void publish(LogRecord record) {
		Formatter f = this.getFormatter();
		this.addLogMessage(f.format(record));
	}
}
