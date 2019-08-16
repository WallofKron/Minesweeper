package minesweepa;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
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
    private static final int NUM_MINES = 2;
    private static final int NUM_BLOCKS = vertsqs * horzsqs;
    private static int BLOCKS_LEFT = NUM_BLOCKS;
    private static final int winxpos = 400, winypos = 120;
    private static final int win_x_size = 400, win_y_size = 400;
    private static final int pop_wid = 100, pop_hght = 100;

    private static final String NewGameStr = "New Game";
    private static final String QuitStr = "Quit";
    private static final String TitleBarDflt = "MineSweeper";

    private static Map<Integer, Color> color_list = new HashMap<Integer, Color>();

    private final Font regfont = new Font("TimesRoman", Font.BOLD, 15);

    private static JFrame frame = new JFrame(TitleBarDflt);
    private static JFrame popframe = new JFrame(TitleBarDflt);

    private static JButton[] button = new JButton[horzsqs * vertsqs];
    private JButton newgame, Exit, engGame;

    private JPanel northpanel, nlleft, nright, centerpanel, poppanel;
    private JTextArea minesleft, outputarea, EndgameMsg;

    PopupFactory win_msg;
    Popup pop;

    public static void main(String[] args)
    {
        MineSweeper blah = new MineSweeper();
    }

    private MineSweeper()
    {
        frame = this;
        frame.setLayout(new BorderLayout());
        frame.setSize(win_x_size, win_y_size);
        frame.setLocation(winxpos, winypos);
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

        placeMines();
        //int col = 0, row = 0;

        for (int i = 0; i < NUM_BLOCKS; i++)
        {
            button[i] = new JButton();
            button[i].addActionListener(this);
            button[i].setActionCommand("" + i);
            button[i].setFocusPainted(false);
            //col = (int) Math.floor(i / horzsqs);
            //row = (int) (Math.round((((i / vertsqs)) - (Math.floor(i / vertsqs))) * 10));
            //button[i].setForeground(buttonTextColor(col, row));
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
        printBoardtoConsole();
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

    private static void placeMines()
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

            if (board[x2][y2] != MINE_VAL)
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

    public static Color buttonTextColor(int col, int row)
    {
        Color color = color_list.get(board[col][row]);
        return color;
    }

    //check and 'press' every button around the 0 if its not a bomb.
    //if any of the non-bomb squares are a 0 then add it to a list and check those after....
    private static void expandAdjacentZeros(int col, int row)
    {
        for (int adj_y = -1; adj_y < 2; adj_y++)
        {
            for (int adj_x = -1; adj_x < 2; adj_x++)
            {
                // check if the position for checking adjacency is a valid position within the board and not adjacency checking our current square
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
                                BLOCKS_LEFT--;

                                if (board[col + adj_y][row + adj_x] != 0)
                                {
                                    button[spot].setText(Integer.toString(board[col + adj_y][row + adj_x]));
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
        placeMines();
        resetButtons();
        printBoardtoConsole();
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
            case QuitStr:
                dispose();
                System.exit(0);
                break;

            case NewGameStr:
                resetGame();
                break;

            default:
                double check = Double.parseDouble(checkString);
                int spot = Integer.parseInt(checkString);
                double k = (Math.floor(check / vertsqs));
                double q = (check / vertsqs);
                int i = (int) Math.floor(check / horzsqs);
                int j = (int) (Math.round((q - k) * 10));

                if (board[i][j] == MINE_VAL)
                {
                    //losing pop up?
                    resetGame();
                }
                else
                {
                    if (board[i][j] > 0)
                    {
                        button[spot].setText(Integer.toString(board[i][j]));
                        button[spot].setEnabled(false);
                        BLOCKS_LEFT--;
                    }
                    if (board[i][j] == 0)
                    {
                        button[spot].setEnabled(false);
                        BLOCKS_LEFT--;
                        expandAdjacentZeros(i, j);
                    }
                }
                System.out.println(BLOCKS_LEFT);

                if (BLOCKS_LEFT == NUM_MINES)
                {
                    //WINNER!
                    frame.setTitle("Winner!!!!");
                }
                break;
        }
    }
}

//      close the gap between the buttons.... make them more form fit.
//
//      fix button text color to represent the numbers.... not displaying anything but gray
//
//      comment code better, clean up code
//
//      change it to an end of game pop up window. the message will change depending on win/loss
//      not displaying correctly/at all. try to set up popframe to be similar to frame?
//
//      Flagging Mode?/getting rid of Mines Remaining?
//
//      game should generate bomb locations after first click, not before (can never click on bomb first click)