package lebrigand.core.ui;

import java.util.logging.*;
import java.io.PrintWriter;
import java.io.StringWriter;

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
		this.message_model.add(0, msg);
		if (this.message_model.size() > 200) {
			this.message_model.setSize(200);
		}
	}

	public void close() {

	}
	
	public void flush() {

	}

	public void publish(LogRecord record) {
		Formatter f = this.getFormatter();
		this.addLogMessage(f.format(record));
		if (record.getThrown() != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			record.getThrown().printStackTrace(pw);
			for (String s : sw.toString().split("\n")) {
				this.addLogMessage(s);
			}
		}
	}
}
