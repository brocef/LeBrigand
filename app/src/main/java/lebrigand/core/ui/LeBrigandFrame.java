package lebrigand.core.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.StyledDocument;

import lebrigand.LeLogger;
import lebrigand.core.Bridge;
import lebrigand.core.spyglass.GameState;
import lebrigand.core.spyglass.GameStateSubscriber;

public class LeBrigandFrame extends JFrame implements Messenger, GameStateSubscriber {

    private static Logger logger = Logger.getLogger(LeBrigandFrame.class.getName());
    public static final String AC_START_BILGE_BOT = "START_BILGE_BOT";
    public static final String AC_INIT_VM = "INIT_VM";
    public static final String AC_INPUT = "INPUT";
    public static final String AC_START_RIGGING_BOT = "START_RIGGING_BOT";

    private Bridge bridge;

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    //private Debugger debug;
    private JScrollPane scrollPane;
    private JTextPane logArea;
    private StyledDocument logDoc;
    private File log;
    private JPanel panel;
    private JLabel lblActiveBot;
    private JLabel lblDutyReportMonitor;
    private JLabel lblBotStatus;
    private JLabel lblActionSprites;
    private JPanel panel_1;
    private JButton btnInitializeVm;
    private JButton btnOtherButton;
    private JLabel lblActionAnimation;
    private JLabel lblActivePanel;
    private JLabel lblRiggingCursor;
    private JLabel lblNextPulley;
    private JLabel lblIsAddNotifyComplete;
    private JButton btnStartRiggingBot;
    private JLabel lblRiggingWisps;
    private JLabel lblRiggingPanelOverlay;
    private JLabel lblRiggingPanelStarsM;
    private JLabel lblRiggingPanelStarsK;
    private JLabel lblRiggingNextCoil;

    private GuiLogHandler logHandler;

    /**
     * Create the frame.
     */
    public LeBrigandFrame(Bridge bridge) {
        this.bridge = bridge;
        setTitle("LeBrigand");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 750, 550);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        scrollPane = new JScrollPane();
        scrollPane.setWheelScrollingEnabled(true);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        logArea = new JTextPane();
        logArea.setEditable(false);
        this.logDoc = logArea.getStyledDocument();
        scrollPane.setViewportView(logArea);

        panel = new JPanel();
        contentPane.add(panel, BorderLayout.EAST);
        panel.setLayout(new GridLayout(16, 1, 0, 0));

        lblActiveBot = new JLabel("Active Bot: None");
        panel.add(lblActiveBot);

        lblBotStatus = new JLabel("Bot Status: n/a");
        panel.add(lblBotStatus);

        lblDutyReportMonitor = new JLabel("Duty Report Active: n/a");
        panel.add(lblDutyReportMonitor);

        lblActionSprites = new JLabel("Action Sprite Size: n/a");
        panel.add(lblActionSprites);

        lblActionAnimation = new JLabel("Action Animation Size: n/a");
        panel.add(lblActionAnimation);

        lblActivePanel = new JLabel("Active Panel: n/a");
        panel.add(lblActivePanel);

        lblRiggingCursor = new JLabel("Rigging Cursor: n/a");
        panel.add(lblRiggingCursor);

        lblNextPulley = new JLabel("Next Pulley: n/a");
        panel.add(lblNextPulley);

        lblIsAddNotifyComplete = new JLabel("isAddNotifyComplete: n/a");
        panel.add(lblIsAddNotifyComplete);

        lblRiggingWisps = new JLabel("Rigging Wisps: n/a");
        panel.add(lblRiggingWisps);

        lblRiggingPanelOverlay = new JLabel("Panel Overlay: n/a");
        panel.add(lblRiggingPanelOverlay);

        lblRiggingPanelStarsM = new JLabel("Rigging Panel Stars M: n/a");
        panel.add(lblRiggingPanelStarsM);

        lblRiggingPanelStarsK = new JLabel("Rigging Panel Stars K: n/a");
        panel.add(lblRiggingPanelStarsK);

        lblRiggingNextCoil = new JLabel("Rigging Coil Count: n/a");
        panel.add(lblRiggingNextCoil);

        panel_1 = new JPanel();
        contentPane.add(panel_1, BorderLayout.SOUTH);
        panel_1.setLayout(new GridLayout(0, 4, 0, 0));

        JButton btnStartBilgeBot = new JButton("Start Bilge Bot");
        panel_1.add(btnStartBilgeBot);

        btnInitializeVm = new JButton("Initialize VM");
        btnInitializeVm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                bridge.actionPerformed(evt);
            }
        });
        btnInitializeVm.setActionCommand(AC_INIT_VM);
        panel_1.add(btnInitializeVm);

        btnOtherButton = new JButton("Test Bot");
        panel_1.add(btnOtherButton);
        btnOtherButton.setActionCommand(AC_INPUT);
        btnOtherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                bridge.actionPerformed(evt);
            }
        });

        btnStartBilgeBot.setActionCommand(AC_START_BILGE_BOT);

        btnStartRiggingBot = new JButton("Start Rigging Bot");
        btnStartRiggingBot.setActionCommand(AC_START_RIGGING_BOT);
        btnStartRiggingBot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                bridge.actionPerformed(evt);
            }
        });
        panel_1.add(btnStartRiggingBot);
        btnStartBilgeBot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                bridge.actionPerformed(evt);
            }
        });
        this.logHandler = new GuiLogHandler(this.logDoc);
        LeLogger.handlers.add(this.logHandler);
        LeLogger.setUpLogger();
    }

    public void log(Object o) {
        log(o.toString());
    }

    public void log(String msg) {
        this.logger.info(msg);
    }

    public void log(String format, Object... args) {
        log(String.format(format, args));
    }

    public void updateActiveBot(String botName) {
        lblActiveBot.setText(String.format("Active Bot: %s", botName == null ? "None" : botName));
    }

    public void updateDutyReportMonitor(boolean dutyReportIsActive) {
        lblDutyReportMonitor.setText(String.format("Duty Report Active: %b", dutyReportIsActive));
    }

    public void updateBotStatus(String status) {
        lblBotStatus.setText(String.format("Bot Status: %s", status));
    }

    public void updateActionSprites(int sprites) {
        lblActionSprites.setText(String.format("Action Sprite Size: %d", sprites));
        if (sprites > 0) {
            lblActionSprites.setForeground(Color.GREEN);
        } else {
            lblActionSprites.setForeground(Color.RED);
        }
    }

    public void updateActionAnims(int anims) {
        lblActionAnimation.setText(String.format("Action Animation Size: %d", anims));
        if (anims > 0) {
            lblActionAnimation.setForeground(Color.GREEN);
        } else {
            lblActionAnimation.setForeground(Color.RED);
        }
    }

    public void updateActivePanel(String panel) {
        lblActivePanel.setText(String.format("Active Panel: %s", (panel != null ? panel : "None")));
        if (panel != null) {
            lblActivePanel.setForeground(Color.GREEN);
        } else {
            lblActivePanel.setForeground(Color.RED);
        }
    }

    public void updateRiggingCursor(int row, int col) {
        lblRiggingCursor.setText(String.format("Rigging Cursor: %s",
                (row == -1 || col == -1) ? "n/a"
                        : String.format("[%d][%d]", row, col)));
    }

    public void updateNextPulley(int pulley) {
        lblNextPulley.setText(String.format("Next Pulley: %s",
                pulley == -1 ? "n/a" : Integer.toString(pulley)));
    }

    public void updateIsAddNotifyComplete(boolean b) {
        lblIsAddNotifyComplete.setText(String.format("isAddNotifyComplete: %b", b));
    }

    public void updateRiggingWisps(int wisps) {
        lblRiggingWisps.setText(String.format("Rigging Wisps: %d", wisps));
    }

    public void updateRiggingOverlay(String overlay) {
        lblRiggingPanelOverlay.setText(String.format("Panel Overlay: %s", overlay));
    }

    public void updateRiggingStarsM(int mstars) {
        lblRiggingPanelStarsM.setText(String.format("Rigging Panel Stars M: %d", mstars));
    }

    public void updateRiggingStarsK(int kstars) {
        lblRiggingPanelStarsK.setText(String.format("Rigging Panel Stars K: %d", kstars));
    }

    public void updateRiggingCoilCount(int coils) {
        lblRiggingNextCoil.setText(String.format("Rigging Coil Count: %d", coils));
    }

    public void updateGameData(GameState state) {
        updateActiveBot(bridge.getActiveBotName());
        updateDutyReportMonitor(state.getDutyReportIsUp());
        updateActionSprites(-1);
        updateActionAnims(state.getRiggingActionAnims());
        updateActivePanel(state.getReadibleActivePlayingPanel());
        updateRiggingCursor(state.getRiggingCursorRow(), state.getRiggingCursorCol());
        updateNextPulley(state.getRiggingPulley());
        updateIsAddNotifyComplete(state.isAddNotifyComplete());
        updateRiggingWisps(state.getRiggingWisps());
        updateRiggingOverlay(state.getReadibleRiggingPanelOverlay());
        updateRiggingStarsM(state.getRiggingPanelStarsM());
        updateRiggingStarsK(state.getRiggingPanelStarsK());
        updateRiggingCoilCount(state.getRiggingCoilCount());
    }
}
