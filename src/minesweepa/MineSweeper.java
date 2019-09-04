package minesweepa;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

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
    private static int[][] board;
    private static final int vertsqs = 10;
    private static final int horzsqs = 10;
    private static final int NO_MINE = 0;
    private static final int MINE_VAL = 101;
    private static final int NUM_MINES = 5;
    private static final int NUM_BLOCKS = vertsqs * horzsqs;
    private static final int win_x_pos = 400, win_y_pos = 120;
    private static final int win_x_size = 400, win_y_size = 400;
    private static int BLOCKS_LEFT = NUM_BLOCKS;
    private static int winLabel_wid, winLabel_hght, lossLabel_wid, lossLabel_hght;

    private static final String NewGameStr = "New Game";
    private static final String QuitStr = "Quit";
    private static final String TitleBarDflt = "MineSweeper";
    private static final String OkStr = "Ok";

    private static boolean firstClick = true;
    private static boolean popUpOpen = false;

    private static Map<Integer, Color> color_list = new HashMap<>();

    private final Font regfont = new Font("TimesRoman", Font.BOLD, 15);

    private static JFrame frame = new JFrame(TitleBarDflt);

    private static JButton[] button = new JButton[horzsqs * vertsqs];
    private JButton newgame, Exit, WinGame, LoseGame;

    private static JPanel northpanel, nlleft, nright, centerpanel, WinGamePanel, LoseGamePanel;
    private static JTextArea minesleft, outputarea;
    private static JLabel winLabel, lossLabel;

    private static PopupFactory end_msg;
    private static Popup pop;
    private static Dimension Win_dimension, loss_dimension;

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
        nright = new JPanel();
        northpanel.setLayout(new GridLayout());
        northpanel.add(nlleft);
        northpanel.add(nright);

        outputarea = new JTextArea("Mines Remaining:", 1, 10);
        outputarea.setFont(regfont);
        outputarea.setOpaque(false);
        outputarea.setEditable(false);

        minesleft = new JTextArea("" + NUM_MINES, 1, 1);
        minesleft.setFont(regfont);
        minesleft.setOpaque(false);
        minesleft.setEditable(false);
        minesleft.setBorder(BorderFactory.createLineBorder(Color.black));

        nlleft.setBackground(Color.white);
        nlleft.setBorder(BorderFactory.createLineBorder(Color.red));
        nlleft.add(outputarea);
        nlleft.add(minesleft);

        newgame = new JButton(NewGameStr);
        Exit = new JButton(QuitStr);
        newgame.addActionListener(this);
        Exit.addActionListener(this);

        nright.setBackground(Color.red);
        nright.setBorder(BorderFactory.createLineBorder(Color.white));
        nright.add(newgame);
        nright.add(Exit);

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
            button[i].setForeground(Color.black);
            centerpanel.add(button[i]);
        }

        getContentPane().add("North", northpanel);
        getContentPane().add("Center", centerpanel);

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
            }
            else
            {
                idx--;
            }
        }
        fillAdjacencyValuesOnBoard();
        printBoardtoConsole();
    }

    private static void fillAdjacencyValuesOnBoard()
    {
        //go through every square of the board
        for (int col = 0; col < horzsqs; col++)
        {
            for (int row = 0; row < vertsqs; row++)
            {
                if (board[col][row] != MINE_VAL)
                {
                    //adjacency value check for the 8 adjacent squares around the current square its on
                    for (int adj_y = -1; adj_y < 2; adj_y++)
                    {
                        for (int adj_x = -1; adj_x < 2; adj_x++)
                        {
                            //check if the position for checking adjacency is a valid position(within the board)
                            if (adj_x != 0 || adj_y != 0)
                            {
                                if ((col + adj_y >= 0 && col + adj_y < horzsqs) && (row + adj_x >= 0 && row + adj_x < vertsqs))
                                {
                                    //is the position a mine?
                                    if (board[col + adj_y][row + adj_x] == MINE_VAL)
                                    {
                                        board[col][row] += 1;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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
        placeMines(0, 0);
        resetButtons();
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

    private static void resetButtons()
    {
        for (int idx = 0; idx < NUM_BLOCKS; idx++)
        {
            button[idx].setEnabled(true);
            button[idx].setText("");
        }
        BLOCKS_LEFT = NUM_BLOCKS;
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

            case QuitStr:
                dispose();
                System.exit(0);
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
                int j = (int) (Math.round(((check / vertsqs) - (Math.floor(check / vertsqs))) * 10));

                if (firstClick)
                {
                    firstClick = false;
                    placeMines(i, j);
                }

                if (board[i][j] == MINE_VAL)
                {
                    frame.setTitle("Loser!!!!");
                    pop = end_msg.getPopup(frame, LoseGamePanel, (win_x_pos + (int) (win_x_size / 2.0) - (lossLabel_wid / 2)), (win_y_pos + (int) (win_y_size / 2.0)) - (lossLabel_hght / 2));
                    pop.show();
                    popUpOpen = true;
                }
                else
                {
                    if (board[i][j] > 0)
                    {
                        button[spot].setText(Integer.toString(board[i][j]));
                        button[spot].setForeground(buttonTextColor(i, j));
                        button[spot].setEnabled(false);
                        --BLOCKS_LEFT;
                    }
                    else
                    {
                        button[spot].setEnabled(false);
                        --BLOCKS_LEFT;
                        expandAdjacentZeros(i, j);
                    }
                }
                System.out.println(BLOCKS_LEFT);

                if (BLOCKS_LEFT == NUM_MINES)
                {
                    pop = end_msg.getPopup(frame, WinGamePanel, (win_x_pos + (int) (win_x_size / 2.0) - (winLabel_wid / 2)), (win_y_pos + (int) (win_y_size / 2.0)) - (winLabel_hght / 2));
                    pop.show();
                    popUpOpen = true;
                    frame.setTitle("Winner!!!!");
                }
                break;
        }
    }
}

//      close the gap between the buttons.... make them more form fit.
//      fix button text color to represent the numbers.... not displaying anything but gray             BOTH? LookAndFeel?
//
//      comment code better, clean up code
//
//      Flagging Mode on: top left display mines remaining regardless
//       if clicked, change button icon to flag. Flag mode has to be off to click square
//
//      options menu? difficulty settings? Opening menu?