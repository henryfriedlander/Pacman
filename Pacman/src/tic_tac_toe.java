import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;




public class tic_tac_toe extends JPanel{


	int mouse_x; 
	int mouse_y;
	int SQUARE_SIZE=4;
	enum Things {
		EMPTY,
		BLOCK,
	}

	static Things board[][]= {
		{Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY},
		{Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY},
		{Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY},
		{Things.BLOCK,Things.BLOCK,Things.BLOCK,Things.BLOCK,Things.BLOCK,Things.BLOCK,Things.BLOCK,Things.BLOCK,Things.BLOCK,Things.BLOCK,Things.BLOCK},
		{Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY},
		{Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY},
		{Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY},
		{Things.BLOCK,Things.BLOCK,Things.BLOCK,Things.BLOCK,Things.BLOCK,Things.BLOCK,Things.BLOCK,Things.BLOCK,Things.BLOCK,Things.BLOCK,Things.BLOCK},
		{Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY},
		{Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY},
		{Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY,Things.BLOCK,Things.EMPTY,Things.EMPTY,Things.EMPTY},
	};
	public static void main(String[] args) {
		tic_tac_toe board = new tic_tac_toe();
		//board.addMouseMotionListener(board);
		//board.addMouseListener(board);
		@SuppressWarnings("unused")
		JFrame frame = new JFrame();
		frame.add(board);
	}

{
	for (int i = 0; i < board.length; i++) {
		for (int j = 0; j < board[i].length; j++) {
			if (board[i][j] == Things.BLOCK) {
				//g.setColor(Color.black);
				//g.fillRect(j * SQUARE_SIZE, i * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
				}
		}
	}
}
}