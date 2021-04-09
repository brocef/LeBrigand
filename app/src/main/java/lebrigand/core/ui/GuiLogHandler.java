package lebrigand.core.ui;

import java.awt.Color;
import java.util.logging.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class GuiLogHandler extends Handler {
    private static Logger logger = Logger.getLogger(GuiLogHandler.class.getName());
    private StyledDocument logDoc;
    private Style timestampStyle, infoLevelStyle, warningLevelStyle, severeLevelStyle, logMessageStyle;
    private Map<Level, Style> logLevelMap;
    
    public GuiLogHandler(StyledDocument logDoc) {
        this.logDoc = logDoc;
        this.setFormatter(new SimpleFormatter());
        this.initializeStyles();
        GuiLogHandler.logger.info("GuiLogHandler initialized");
    }
    
    private void initializeStyles() {
        timestampStyle = this.logDoc.addStyle("timestamp", null);
        StyleConstants.setForeground(timestampStyle, Color.darkGray);
        
        infoLevelStyle = this.logDoc.addStyle("infoLevelStyle", null);
        StyleConstants.setForeground(infoLevelStyle, Color.BLACK);
        
        warningLevelStyle = this.logDoc.addStyle("warningLevelStyle", null);
        StyleConstants.setForeground(warningLevelStyle, Color.ORANGE);
        
        severeLevelStyle = this.logDoc.addStyle("severeLevelStyle", null);
        StyleConstants.setForeground(severeLevelStyle, Color.RED);
        
        logMessageStyle = this.logDoc.addStyle("logMessageStyle", null);
        StyleConstants.setForeground(logMessageStyle, Color.BLACK);
        
        this.logLevelMap = new HashMap<>();
        this.logLevelMap.put(Level.INFO, this.infoLevelStyle);
        this.logLevelMap.put(Level.WARNING, this.warningLevelStyle);
        this.logLevelMap.put(Level.SEVERE, this.severeLevelStyle);
    }
    
    private Style getStyleForLogLevel(Level l) {
        if (!this.logLevelMap.containsKey(l)) {
            return this.infoLevelStyle;
        }
        return this.logLevelMap.get(l);
    }
    
    @Override
    public void close() {
        
    }
    
    @Override
    public void flush() {
        
    }
    
    @Override
    public void publish(LogRecord record) {
        try {
            Formatter f = this.getFormatter();
            String formattedRecord = f.format(record);
            String[] recordParts = formattedRecord.split("[|]", 3);
            this.logDoc.insertString(this.logDoc.getLength(), recordParts[0], this.timestampStyle);
            this.logDoc.insertString(this.logDoc.getLength(), recordParts[1], this.getStyleForLogLevel(record.getLevel()));
            this.logDoc.insertString(this.logDoc.getLength(), recordParts[2], this.logMessageStyle);
        } catch (BadLocationException ex) {
            GuiLogHandler.logger.log(Level.SEVERE, null, ex);
        }
    }
}
