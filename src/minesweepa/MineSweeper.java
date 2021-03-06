package minesweepa;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;

//  @author RobertFlorence
//  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//  @@@@                                                  @@@@
//  @@@   @@@@     @@@    @@@@    @@@@@   @@@@   @@@@@@@   @@@
//  @@    @   @   @   @   @   @   @       @   @     @       @@
//  @     @@@@@  @     @  @@@@    @@@@    @@@@@     @        @
//  @@    @  @    @   @   @   @   @       @  @      @       @@
//  @@@   @   @    @@@    @@@@    @@@@@   @   @     @      @@@
//  @@@@                                                  @@@@
//  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

class MineSweeper extends JFrame implements ActionListener
{
    private static GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    private static double ScrnWid = gd.getDisplayMode().getWidth();
    private static double ScrnHght = gd.getDisplayMode().getHeight();
    private static int[][] board;
    private static final int defButtHght = 32;
    private static final int defButtWid = 32;
    private static final int MenuBarHght = 15;
    private static final int vertsqs = 20;
    private static final int horzsqs = 20;
    private static final int NO_MINE = 0;
    private static final int MINE_VAL = 101;
    private static final int NUM_MINES = 30;
    private static final int NUM_BLOCKS = vertsqs * horzsqs;
    private static final int win_y_size = (defButtWid * horzsqs), win_x_size = ((defButtHght * vertsqs) + MenuBarHght);
    private static final int win_x_pos = (int) ((ScrnWid / 2) - (win_x_size / 2)), win_y_pos = (int) ((ScrnHght / 2) - (win_y_size / 2));
    private static int BLOCKS_LEFT = NUM_BLOCKS;
    private static int BLOCKS_FLAGGED = 0;
    private static int winLabel_wid, winLabel_hght, lossLabel_wid, lossLabel_hght;
    private static int SCLD_ICON_HGHT = 24;
    private static int SCLD_ICON_WID = 24;
    private static int mins = 0, seconds = 0;

    private static final String NewGameStr = "New Game";
    private static final String ModeStr = "Mode";
    private static final String TitleBarDflt = "MineSweeper";
    private static final String OkStr = "Ok";

    private static boolean firstClick = true;
    private static boolean popUpOpen = false;
    private static boolean flagMode = false;

    private static Map<Integer, Color> color_list = new HashMap<>();

    private final Font regfont = new Font("TimesRoman", Font.BOLD, 15);

    private static JFrame frame = new JFrame(TitleBarDflt);

    private static JButton[] button = new JButton[horzsqs * vertsqs];
    private JButton newgame, Mode, WinGame, LoseGame;

    private static JPanel northpanel, nlleft, ncenter, nright, centerpanel, WinGamePanel, LoseGamePanel;
    private static JTextArea minesleft, gameTimer;
    private static JLabel winLabel, lossLabel;

    private static PopupFactory end_msg;
    private static Popup pop;
    private static Dimension Win_dimension, loss_dimension;

    private String gflagpath = "gflag.png";
    private String rflagpath = "flag.png";
    private String bombpath = "bomb.png";
    private BufferedImage rflagimg;
    private BufferedImage gflagimg;
    private BufferedImage bombimg;
    private Image rflagscaledimg;
    private Image gflagscaledimg;
    private Image bombscaledimg;
    private BufferedImage rflagbuff;
    private BufferedImage gflagbuff;
    private BufferedImage bombbuff;
    private ImageIcon redflagicon;
    private ImageIcon greyflagicon;
    private ImageIcon bombicon;

    private static Timer game_timer;

    public static void main(String[] args)
    {
        MineSweeper blah = new MineSweeper();
    }

    private MineSweeper()
    {
        frame = this;
        frame.setLayout(new BorderLayout());
        frame.setSize(win_x_size, win_y_size);
        frame.setLocation(win_x_pos, win_y_pos);
        frame.setResizable(false);
        frame.setTitle(TitleBarDflt);

        northpanel = new JPanel();
        northpanel.setBorder(BorderFactory.createLineBorder(Color.black));
        nlleft = new JPanel();
        ncenter = new JPanel();
        nright = new JPanel();
        northpanel.setLayout(new GridLayout());
        northpanel.add(nlleft);
        northpanel.add(ncenter);
        northpanel.add(nright);

        Mode = new JButton(ModeStr);
        Mode.addActionListener(this);

        nlleft.setBackground(Color.red);
        nlleft.setBorder(BorderFactory.createLineBorder(Color.white));
        nlleft.add(Mode);

        minesleft = new JTextArea("" + NUM_MINES, 1, 1);
        minesleft.setFont(regfont);
        minesleft.setOpaque(false);
        minesleft.setEditable(false);
        minesleft.setBorder(BorderFactory.createLineBorder(Color.black));

        gameTimer = new JTextArea("0:00", 1, 2);
        gameTimer.setFont(regfont);
        gameTimer.setOpaque(false);
        gameTimer.setEditable(false);
        gameTimer.setBorder(BorderFactory.createLineBorder(Color.black));

        ncenter.setBackground(Color.white);
        ncenter.setBorder(BorderFactory.createLineBorder(Color.red));
        ncenter.add(minesleft);
        ncenter.add(gameTimer);

        newgame = new JButton(NewGameStr);
        newgame.addActionListener(this);

        nright.setBackground(Color.red);
        nright.setBorder(BorderFactory.createLineBorder(Color.white));
        nright.add(newgame);

        centerpanel = new JPanel();
        centerpanel.setLayout(new GridLayout(horzsqs, vertsqs));
        centerpanel.setBorder(BorderFactory.createLineBorder(Color.red));

        color_list.put(0, Color.black);
        color_list.put(1, Color.blue);
        color_list.put(2, Color.green);
        color_list.put(3, Color.magenta);
        color_list.put(4, Color.cyan);
        color_list.put(5, Color.pink);
        color_list.put(6, Color.yellow);
        color_list.put(7, Color.orange);
        color_list.put(8, Color.red);

        end_msg = new PopupFactory();
        winLabel = new JLabel("YOU WIN!");
        winLabel.setFont(new Font("BOLD", Font.BOLD, 24));
        Win_dimension = winLabel.getPreferredSize();
        winLabel_hght = Win_dimension.height;
        winLabel_wid = Win_dimension.width;
        WinGame = new JButton(OkStr);
        WinGame.addActionListener(this);
        WinGamePanel = new JPanel();
        WinGamePanel.setBackground(Color.red);
        WinGamePanel.add(winLabel);
        WinGamePanel.add(WinGame);
        WinGamePanel.setLayout(new GridLayout(2, 1));

        lossLabel = new JLabel("YOU LOSE!");
        lossLabel.setFont(new Font("BOLD", Font.BOLD, 24));
        loss_dimension = lossLabel.getPreferredSize();
        lossLabel_hght = loss_dimension.height;
        lossLabel_wid = loss_dimension.width;
        LoseGame = new JButton(OkStr);
        LoseGame.addActionListener(this);
        LoseGamePanel = new JPanel();
        LoseGamePanel.setBackground(Color.white);
        LoseGamePanel.add(lossLabel);
        LoseGamePanel.add(LoseGame);
        LoseGamePanel.setLayout(new GridLayout(2, 1));

        for (int i = 0; i < NUM_BLOCKS; i++)
        {
            button[i] = new JButton();
            button[i].addActionListener(this);
            button[i].setActionCommand("" + i);
            button[i].setFocusPainted(false);
            button[i].setName("noFlag");
            centerpanel.add(button[i]);
        }
        getContentPane().add("North", northpanel);
        getContentPane().add("Center", centerpanel);

        try
        {
            rflagimg = ImageIO.read(getClass().getResource(rflagpath));
            gflagimg = ImageIO.read(getClass().getResource(gflagpath));
            bombimg = ImageIO.read(getClass().getResource(bombpath));
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }

        rflagscaledimg = rflagimg.getScaledInstance(SCLD_ICON_WID, SCLD_ICON_HGHT, java.awt.Image.SCALE_SMOOTH);
        gflagscaledimg = gflagimg.getScaledInstance(SCLD_ICON_WID, SCLD_ICON_HGHT, java.awt.Image.SCALE_SMOOTH);
        bombscaledimg = bombimg.getScaledInstance(SCLD_ICON_WID, SCLD_ICON_HGHT, java.awt.Image.SCALE_SMOOTH);
        rflagbuff = new BufferedImage(SCLD_ICON_WID, SCLD_ICON_HGHT, BufferedImage.TYPE_INT_ARGB);
        gflagbuff = new BufferedImage(SCLD_ICON_WID, SCLD_ICON_HGHT, BufferedImage.TYPE_INT_ARGB);
        bombbuff = new BufferedImage(SCLD_ICON_WID, SCLD_ICON_HGHT, BufferedImage.TYPE_INT_ARGB);
        rflagbuff.getGraphics().drawImage(rflagscaledimg, 0, 0, null);
        gflagbuff.getGraphics().drawImage(gflagscaledimg, 0, 0, null);
        bombbuff.getGraphics().drawImage(bombscaledimg, 0, 0, null);
        final BufferedImage rflag_trans = new BufferedImage(rflagbuff.getWidth(), rflagbuff.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final BufferedImage gflag_trans = new BufferedImage(gflagbuff.getWidth(), gflagbuff.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final BufferedImage bomb_trans = new BufferedImage(bombbuff.getWidth(), bombbuff.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Color cTrans = new Color(255, 0, 0, 0);

        for (int x = 0; x < rflagbuff.getWidth(); x++)
        {
            for (int y = 0; y < rflagbuff.getHeight(); y++)
            {
                Color c = new Color(rflagbuff.getRGB(x, y), true);
                Color cNew = (c.equals(Color.WHITE) ? cTrans : c);
                rflag_trans.setRGB(x, y, cNew.getRGB());
            }
        }

        for (int x = 0; x < gflagbuff.getWidth(); x++)
        {
            for (int y = 0; y < gflagbuff.getHeight(); y++)
            {
                Color c = new Color(gflagbuff.getRGB(x, y), true);
                Color cNew = (c.equals(Color.WHITE) ? cTrans : c);
                gflag_trans.setRGB(x, y, cNew.getRGB());
            }
        }

        for (int x = 0; x < bombbuff.getWidth(); x++)
        {
            for (int y = 0; y < bombbuff.getHeight(); y++)
            {
                Color c = new Color(bombbuff.getRGB(x, y), true);
                Color cNew = (c.equals(Color.WHITE) ? cTrans : c);
                bomb_trans.setRGB(x, y, cNew.getRGB());
            }
        }


        //WHY DOES IMAGEICON use white when the values are transparent/nonexistant.
        //rflag_trans and gflag_trans images dont have white background. Why does imageicon
        redflagicon = new ImageIcon(rflag_trans);
        greyflagicon = new ImageIcon(gflag_trans);
        bombicon = new ImageIcon(bomb_trans);

        game_timer = new Timer(1000, evt ->
        {
            String clock_val = getClockVal();
            gameTimer.setText(clock_val);
        });

        frame.setVisible(true);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
    }

    private static void printBoardtoConsole()
    {
        System.out.println();
        for (int col = 0; col < vertsqs; col++)
        {
            for (int row = 0; row < horzsqs; row++)
            {
                System.out.print(board[col][row]);
                System.out.print("\t");
            }
            System.out.println();
        }
    }

    private static void placeMines(int i, int j)
    {
        Random r = new Random();
        int x2, y2;
        board = new int[horzsqs][vertsqs];

        for (int x = 0; x < horzsqs; x++)
        {
            Arrays.fill(board[x], NO_MINE);
        }

        for (int idx = 0; idx < NUM_MINES; idx++)
        {
            x2 = r.nextInt(horzsqs);
            y2 = r.nextInt(vertsqs);

            if (board[x2][y2] != MINE_VAL && x2 != i && y2 != j)
            {
                board[x2][y2] = MINE_VAL;

                for (int adj_y = -1; adj_y < 2; adj_y++)
                {
                    for (int adj_x = -1; adj_x < 2; adj_x++)
                    {
                        if (adj_x != 0 || adj_y != 0)
                        {
                            if ((x2 + adj_y >= 0 && x2 + adj_y < horzsqs) && (y2 + adj_x >= 0 && y2 + adj_x < vertsqs))
                            {
                                if (board[x2 + adj_y][y2 + adj_x] != MINE_VAL)
                                {
                                    board[x2 + adj_y][y2 + adj_x] += 1;
                                }
                            }
                        }
                    }
                }
            }
            else
            {
                idx--;
            }
        }

        if (game_timer.isRunning())
        {
            game_timer.restart();
        }
        else
        {
            game_timer.start();
        }

        printBoardtoConsole();
    }

    private static Color buttonTextColor(int col, int row)
    {
        return color_list.get(board[col][row]);
    }

    //check and 'press' every button around the 0 if its not a bomb.
    //if any of the non-bomb squares are a 0 then add it to a list and check those after....
    private static void expandAdjacentZeros(int col, int row)
    {
        for (int adj_y = -1; adj_y < 2; adj_y++)
        {
            for (int adj_x = -1; adj_x < 2; adj_x++)
            {
                // check if the position is a valid position within the board and not our current square
                if (adj_x != 0 || adj_y != 0)
                {
                    if ((col + adj_y >= 0 && col + adj_y < horzsqs) && (row + adj_x >= 0 && row + adj_x < vertsqs))
                    {
                        if (board[col + adj_y][row + adj_x] != MINE_VAL)
                        {
                            int spot = calcSpot(col + adj_y, row + adj_x);

                            if (button[spot].isEnabled())
                            {
                                button[spot].setEnabled(false);
                                //button[spot].setBorderPainted(false);
                                --BLOCKS_LEFT;

                                if (board[col + adj_y][row + adj_x] != 0)
                                {
                                    button[spot].setText(Integer.toString(board[col + adj_y][row + adj_x]));
                                    button[spot].setForeground(buttonTextColor((col + adj_y), (row + adj_x)));
                                }
                                else
                                {
                                    expandAdjacentZeros((col + adj_y), (row + adj_x));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static int calcSpot(int col, int row)
    {
        return ((col * horzsqs) + row);
    }

    private static void resetGame()
    {
        frame.setTitle(TitleBarDflt);
        firstClick = true;
        resetButtons();
        reset_clock();
        BLOCKS_LEFT = NUM_BLOCKS;
        BLOCKS_FLAGGED = 0;
        minesleft.setText("" + (NUM_MINES - BLOCKS_FLAGGED));
        flagMode = false;
    }

    private String getClockVal()
    {
        String clock_val;
        if (seconds < 59 && seconds >= 0)
        {
            seconds++;
        }
        else
        {
            if (mins < 99 && mins >= 0)
            {
                seconds = 0;
                mins++;
            }
            else
            {
                //should never be the case. Zero out the clock
                seconds = 0;
                mins = 0;
            }
        }

        if (seconds > 9)
        {
            clock_val = mins + ":" + seconds;
        }
        else
        {
            clock_val = mins + ":0" + seconds;
        }
        return clock_val;
    }

    private static void disableAllButtons()
    {
        for (int col = 0; col < horzsqs; col++)
        {
            for (int row = 0; row < vertsqs; row++)
            {
                int localCheck = calcSpot(col, row);
                if (button[localCheck].isEnabled())
                {
                    button[localCheck].setEnabled(false);
                }
            }
        }
    }

    private void show_bomb_icons()
    {
        for (int col = 0; col < horzsqs; col++)
        {
            for (int row = 0; row < vertsqs; row++)
            {
                int localCheck = calcSpot(col, row);
                if (board[col][row] == MINE_VAL && button[localCheck].isEnabled() && !(button[localCheck].getName().equals("redFlag")))
                {
                    button[localCheck].setIcon(bombicon);
                    button[localCheck].setOpaque(false);
                    button[localCheck].setContentAreaFilled(true);
                    button[localCheck].setBorderPainted(false);
                    button[localCheck].setName("bomb");
                }
            }
        }
    }

    private static void reset_clock()
    {
        mins = 0;
        seconds = 0;
        game_timer.stop();
        gameTimer.setText("0:00");
    }

    private static void resetButtons()
    {
        for (int idx = 0; idx < NUM_BLOCKS; idx++)
        {
            button[idx].setEnabled(true);
            //button[idx].setBorderPainted(true);
            button[idx].setText("");
            button[idx].setName("noFlag");
            button[idx].setIcon(null);
            button[idx].setOpaque(false);
            button[idx].setBorderPainted(true);
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        String checkString = e.getActionCommand();
        switch (checkString)
        {
            case OkStr:
                pop.hide();
                popUpOpen = false;
                disableAllButtons();
                break;

            case ModeStr:
                flagMode ^= true;
                if (flagMode)
                {
                    for (int q = 0; q < NUM_BLOCKS; q++)
                    {
                        if (button[q].getName().length() > 0)
                        {
                            if (button[q].isEnabled() && !(button[q].getName().equals("redFlag")))
                            {
                                button[q].setIcon(greyflagicon);
                                button[q].setOpaque(false);
                                button[q].setContentAreaFilled(true);
                                button[q].setBorderPainted(false);
                                button[q].setName("greyFlag");
                            }
                        }
                    }
                }
                else
                {
                    for (int z = 0; z < NUM_BLOCKS; z++)
                    {
                        if (button[z].getName().length() > 0)
                        {
                            if (button[z].isEnabled() && button[z].getName().equals("greyFlag"))
                            {
                                button[z].setIcon(null);
                                button[z].setOpaque(false);
                                button[z].setContentAreaFilled(false);
                                button[z].setBorderPainted(true);
                            }
                        }
                    }
                }
                break;

            case NewGameStr:
                resetGame();
                if (popUpOpen)
                {
                    pop.hide();
                    popUpOpen = false;
                }
                break;

            default:
                if (popUpOpen)
                {
                    pop.hide();
                    popUpOpen = false;
                    disableAllButtons();
                    return;
                }

                double check = Double.parseDouble(checkString);
                int spot = Integer.parseInt(checkString);
                int i = (int) Math.floor(check / horzsqs);
                int j = (int) (Math.round(((check / vertsqs) - (Math.floor(check / vertsqs))) * vertsqs));

                if (firstClick)
                {
                    firstClick = false;
                    placeMines(i, j);
                }
                if (!flagMode)
                {
                    if (!(button[spot].getName().equals("redFlag")))
                    {
                        if (board[i][j] == MINE_VAL)
                        {
                            frame.setTitle("Loser!!!!");
                            game_timer.stop();
                            pop = end_msg.getPopup(frame, LoseGamePanel, (win_x_pos + (int) (win_x_size / 2.0) - (lossLabel_wid / 2)), (win_y_pos + (int) (win_y_size / 2.0)) - (lossLabel_hght / 2));
                            pop.show();
                            popUpOpen = true;
                            show_bomb_icons();
                        }
                        else
                        {
                            if (board[i][j] > 0)
                            {
                                button[spot].setText(Integer.toString(board[i][j]));
                                button[spot].setForeground(buttonTextColor(i, j));
                                button[spot].setEnabled(false);
                                //button[spot].setBorderPainted(false);
                                --BLOCKS_LEFT;
                            }
                            else
                            {
                                button[spot].setEnabled(false);
                                //button[spot].setBorderPainted(false);
                                --BLOCKS_LEFT;
                                expandAdjacentZeros(i, j);
                            }
                        }
                    }
                }
                else
                {
                    if (!(button[spot].getName().equals("redFlag")))
                    {
                        button[spot].setIcon(redflagicon);
                        button[spot].setOpaque(false);
                        button[spot].setContentAreaFilled(true);
                        button[spot].setBorderPainted(false);
                        BLOCKS_FLAGGED++;
                        minesleft.setText("" + (NUM_MINES - BLOCKS_FLAGGED));
                        button[spot].setName("redFlag");
                    }
                    else
                    {
                        button[spot].setIcon(greyflagicon);
                        button[spot].setOpaque(false);
                        button[spot].setContentAreaFilled(true);
                        button[spot].setBorderPainted(false);
                        BLOCKS_FLAGGED--;
                        minesleft.setText("" + (NUM_MINES - BLOCKS_FLAGGED));
                        button[spot].setName("greyFlag");
                    }
                }

                if (BLOCKS_LEFT == NUM_MINES)
                {
                    pop = end_msg.getPopup(frame, WinGamePanel, (win_x_pos + (int) (win_x_size / 2.0) - (winLabel_wid / 2)), (win_y_pos + (int) (win_y_size / 2.0)) - (winLabel_hght / 2));
                    pop.show();
                    popUpOpen = true;
                    frame.setTitle("Winner!!!!");
                    game_timer.stop();
                }
                break;
        }
    }
}

// TODO:
//      - Implement flag mode functionality
//          - Size icon larger?  also change/get rid of background behind image icon? currently is white. Makes icon look bad
//              - WHY DOES IMAGEICON use white when the values are transparent/nonexistant.
//              - possible solution is to tweak with:    button[q].setOpaque(bool);
//                                                       button[q].setContentAreaFilled(bool);
//                                                       button[q].setBorderPainted(bool);
//              - this seems to have gotten the transparent icon we're looking for but the edges of the buttons are gone.
//      - Pressed 0 Buttons should not have borders of button painted? or paint the borders with a lighter opacity?
//          - setBorderPainted(false) just leads to big open empty areas and 3 dots for adjacency number text
//          - could this be solved by making enabled buttons borders thicker and disabled ones thinner? cause more of a visual separation?
//      - fix button text color to represent the numbers.... not displaying anything but gray. This might be solved by Java LookAndFeel(?)
//      - clicking non-enabled numbered blocks in flag mode presses all adjacent enabled squares
//      - options menu? difficulty settings? Opening menu?
//          - handled as a popmenu???? breaking everything else?
//      - TEST TEST TEST all the new functionality
//      - comment code better, clean up code