package chessengine.system;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Stores the information of a single chess board.
 * 
 * @author Darcy McCoy
 * @since 1.0
 */
public class Board {
	
	/** The index of the A8 square on the board. */
	public static final int A8_SQR = 0;

	/** The index of the E8 square on the board. */
	public static final int E8_SQR = 4;

	/** The index of the H8 square on the board. */
	public static final int H8_SQR = 7;

	/** The index of the A7 square on the board. */
	public static final int A7_SQR = 8;

	/** The index of the H7 square on the board. */
	public static final int H7_SQR = 15;

	/** The index of the A2 square on the board. */
	public static final int A2_SQR = 48;

	/** The index of the H2 square on the board. */
	public static final int H2_SQR = 55;

	/** The index of the A1 square on the board. */
	public static final int A1_SQR = 56;

	/** The index of the E1 square on the board. */
	public static final int E1_SQR = 60;

	/** The index of the H1 square on the board. */
	public static final int H1_SQR = 63;
	
	/** The width of the board in number of squares */
	public static final int WIDTH = 8;
	
	/** The squares of the board stored as a 64 character <code>String</code>. */
	private String sqrs;

	/**
	 * Default constructor that sets the squares to the standard starting chess position.
	 */
	public Board() {
		this("rnbqkbnr" + "pppppppp" + "--------" + "--------" + "--------" + "--------" + "PPPPPPPP" + "RNBQKBNR");
	}
	
	/**
	 * Class constructor which specifies the squares.
	 * 
	 * @param sqrs the squares of the chess board
	 */
	public Board(String sqrs) {
		this.sqrs = sqrs;
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param otherBoard the <code>Board</code> to copy
	 */
	public Board(Board otherBoard) {
		this(otherBoard.sqrs);
	}
	
	@Override
	public Board clone() {
		return new Board(this);
	}
	
	/**
	 * Returns the contents of a square.
	 *
	 * @param sqr int value the square
	 * @return character contents of the square
	 */
	public char getSqr(int sqr) {
		return sqrs.charAt(sqr);
	}
	
	/**
	 * Sets this square on the board to either empty or to the piece that is
	 * passed.
	 *
	 * @param charToPut character representing the piece or an empty square
	 * @param sqr       int value of the square to be updated
	 */
	public void setSqr(char charToPut, int sqr) {
		sqrs = sqrs.substring(0, sqr) + charToPut + sqrs.substring(sqr + 1);
	}
	
	/**
	 * Updates the board so that the rook portion of a castling move has been made.
	 * 
	 * @param move the <code>Move</code> to update for
	 */
	public void updateRookForCastlingMove(Move move) {
		if (move.isKingsideCastling()) {
			setSqr(getSqr(move.getEndSqr() + Chess.EAST_1), move.getEndSqr() + Chess.WEST_1);
			setSqr(Chess.EMPTY, move.getEndSqr() + Chess.EAST_1);
		} else if (move.isQueensideCastling()) {
			setSqr(getSqr(move.getEndSqr() + Chess.WEST_2), move.getEndSqr() + Chess.EAST_1);
			setSqr(Chess.EMPTY, move.getEndSqr() + Chess.WEST_2);
		}
	}
	
	/**
	 * Updates the board so that the pawn capturing portion of an en passant move has been made.
	 * This is notable because the capturing pawn won't end up on the square of the pawn it is capturing.
	 * 
	 * @param move the <code>Move</code> to update for
	 */
	public void updateCapturedPawnForEnPassantMove(Move move) {
		if (move.isEnPassant()) {
			if (move.getPiece() == Chess.WH_PAWN)
				setSqr(Chess.EMPTY, move.getEndSqr() + Chess.SOUTH_1);
			else
				setSqr(Chess.EMPTY, move.getEndSqr() + Chess.NORTH_1);
		}
	}
	
	/**
	 * Updates the square that the piece originated from to be empty.
	 * 
	 * @param move the <code>Move</code> to update for
	 */
	public void updateStartSqrContents(Move move) {
		setSqr(Chess.EMPTY, move.getStartSqr());
	}
	
	/**
	 * Updates the square on the board that the piece ends for a <code>Move</code>.
	 * 
	 * @param move the <code>Move</code> to update for
	 */
	public void updateEndSqrContents(Move move) {
		char pieceToPut = move.getPiece();
		if (move.isPromotion()) {
			pieceToPut = move.getPromoteTo();
		}
		setSqr(pieceToPut, move.getEndSqr());
	}
	
	/**
	 * Returns the total value of all the pieces on the board in centipawns. Black pieces have 
	 * negative value, so this number represents which color has more piece value.
	 * 
	 * @return sum of the board pieces' values
	 */
	public int getTotalPieceValues() {
		int totalPieceValues = 0;
		for (int i = 0; i < sqrs.length(); i++) {
			totalPieceValues += getPieceValue(getSqr(i), i);
		}
		return totalPieceValues;
	}
	
	/**
	 * Returns an integer based on the type and location of a piece (in centipawns).
	 * This integer is positive if the piece is white and negative if black.
	 * 
	 * 
	 * @param piece character representing the piece
	 * @param sqr int index of the square the piece is on
	 * @return an integer which is the value of the piece based on type and location
	 */
	public int getPieceValue(char piece, int sqr) {
		switch (piece) {
		case Chess.WH_PAWN:
			return Engine.PAWN_VALUES[sqr];
		case Chess.BK_PAWN:
			return Engine.PAWN_VALUES[Engine.PAWN_VALUES.length - 1 - sqr] * -1;
		case Chess.WH_ROOK:
			return Engine.ROOK_VALUES[sqr];
		case Chess.BK_ROOK:
			return Engine.ROOK_VALUES[Engine.ROOK_VALUES.length - 1 - sqr] * -1;
		case Chess.WH_KNIGHT:
			return Engine.KNIGHT_VALUES[sqr];
		case Chess.BK_KNIGHT:
			return Engine.KNIGHT_VALUES[Engine.KNIGHT_VALUES.length - 1 - sqr] * -1;
		case Chess.WH_BISHOP:
			return Engine.BISHOP_VALUES[sqr];
		case Chess.BK_BISHOP:
			return Engine.BISHOP_VALUES[Engine.BISHOP_VALUES.length - 1 - sqr] * -1;
		case Chess.WH_QUEEN:
			return Engine.QUEEN_VALUES[sqr];
		case Chess.BK_QUEEN:
			return Engine.QUEEN_VALUES[Engine.QUEEN_VALUES.length - 1 - sqr] * -1;
		case Chess.WH_KING:
			return Engine.KING_VALUES[sqr];
		case Chess.BK_KING:
			return Engine.KING_VALUES[Engine.KING_VALUES.length - 1 - sqr] * -1;
		default:
			return 0;
		}
	}
	
	/**
	 * Returns one of the kings' squares.
	 *
	 * @param findWhiteKing <code>boolean</code> whether white is the color of the
	 *                      king to be found
	 * @return int value where the king is on the board
	 */
	public int findKingSqr(boolean findWhiteKing) {
		for (int i = 0; i < sqrs.length(); i++) {
			if ((findWhiteKing && (getSqr(i) == Chess.WH_KING)) || (!findWhiteKing && (getSqr(i) == Chess.BK_KING))) {
				return i;
			}
		}
		throw new NoSuchElementException("King couldn't be found");
	}
	
	/**
	 * Returns true if the pawn is on its starting square and the 2 squares ahead are empty.
	 * 
	 * @param pawnSqr int index of the square that the pawn is on
	 * @param movementVector int direction vector of the pawn's movement
	 * @return <code>true</code> if this pawn can make the special 2 square move;
	 *         <code>false</code> otherwise.
	 */
	public boolean pawnCanMove2SqrsAhead(int pawnSqr, int movementVector) {
		if (hasExceededAnEdge(pawnSqr + movementVector + movementVector, movementVector))
			return false;
		if (!isEmptySqr(pawnSqr + movementVector) || !isEmptySqr(pawnSqr + movementVector + movementVector))
			return false;
		return ((getSqr(pawnSqr) == Chess.WH_PAWN) && (isRank2Sqr(pawnSqr)))
				|| ((getSqr(pawnSqr) == Chess.BK_PAWN) && (isRank7Sqr(pawnSqr)));
	}
	
	/**
	 * Returns a new <code>Move</code>.
	 * 
	 * @param startSqr int index of the square that the piece originated from
	 * @param endSqr int index of the square that the piece ends on
	 * @return the new <code>Move</code>
	 */
	public Move constructNonPromotionMove(int startSqr, int endSqr) {
		return new Move(getSqr(startSqr), startSqr, endSqr, getSqr(endSqr));
	}
	
	/**
	 * Returns a <code>LinkedList</code> with 4 moves (one for each type of promotion). 
	 * The moves will be identical except for the promotion type.
	 * 
	 * @param move the <code>Move</code> which is promotion
	 * @return the <code>LinkedList</code> with the promotion moves
	 */
	public LinkedList<Move> constructPromotionMove(Move move){
		LinkedList<Move> moveInList = new LinkedList<>();
		char[] promoteTypes;
		if (move.getPiece() == Chess.WH_PAWN) {
			promoteTypes = Chess.WH_PROMOTING_TYPES;
		} else {
			promoteTypes = Chess.BK_PROMOTING_TYPES;
		}
		for (int i = 0; i < promoteTypes.length; i++) {
			moveInList.add(move.clone());
			moveInList.get(i).setPromoteTo(promoteTypes[i]);
		}
		return moveInList;
	}
	
	/**
	 * Returns true if the move puts a pawn into a position where it can be captured
	 * en passant.
	 * 
	 * @param move the Move to test
	 * @return <code>true</code> if the move is a pawn advancing 2 squares and
	 *         allowing itself to be captured en passant;
	 *         <code>false</code> otherwise.
	 */
	public boolean isAllowsEnPassant(Move move) {
		if ((move.getPiece() == Chess.WH_PAWN) || (move.getPiece() == Chess.BK_PAWN)) {
	        char east1SqrContents = 0;
	        char west1SqrContents = 0;
	        
	        if (!Board.isFileHSqr(move.getEndSqr())) {
	            east1SqrContents = getSqr(move.getEndSqr() + Chess.EAST_1);
	        }
	        if (!Board.isFileASqr(move.getEndSqr())) {
	            west1SqrContents = getSqr(move.getEndSqr() + Chess.WEST_1);
	        }
	        return (((move.getStartSqr() + Chess.NORTH_2) == move.getEndSqr())
	                && ((east1SqrContents == Chess.BK_PAWN) || (west1SqrContents == Chess.BK_PAWN)))
	                || (((move.getStartSqr() + Chess.SOUTH_2) == move.getEndSqr())
	                && ((east1SqrContents == Chess.WH_PAWN) || (west1SqrContents == Chess.WH_PAWN)));
	    }
	    return false;
	}
	
	/**
	 * Returns true if there is no piece at this square.
	 *
	 * @param sqr int index of the square to test
	 * @return <code>true</code> if this square has no piece on it;
	 *         <code>false</code> otherwise.
	 */
	public boolean isEmptySqr(int sqr) {
		return getSqr(sqr) == Chess.EMPTY;
	}
	
	/**
	 * Returns true if there is a piece at this square that can be captured by the color to play.
	 * 
	 * @param sqr int index of the square to test
	 * @param whiteToPlay boolean whether white is to play
	 * @return <code>true</code> if this square has a piece that can currently be captured;
	 *         <code>false</code> otherwise.
	 */
	public boolean isCapturableSqr(int sqr, boolean whiteToPlay) {
		return (whiteToPlay && Chess.isBlackPiece(getSqr(sqr))) 
				|| (!whiteToPlay && Chess.isWhitePiece(getSqr(sqr)));
	}
	
	/**
	 * Returns the board laid out in a 8x8 grid.
	 * 
	 * @return String representation of the squares of the board 
	 */
	@Override
	public String toString() {
		String printBoard = "";
		for (int i = 0; i < sqrs.length(); i++) {
			printBoard += getSqr(i) + " ";
			if (Board.isFileHSqr(i)) {
				printBoard += "\n";
			}
		}
		return printBoard;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Board other = (Board) obj;
		return sqrs.equals(other.sqrs);
	}

	/**
	 * Returns true if this vector put this square over the edge of the board.
	 * 
	 * @param sqr int index of the square to test
	 * @param vector the direction vector that was used to find test square 
	 * @return <code>true</code> if this square has gone over any of the edges of the board; <code>false</code>
	 *         otherwise.
	 */
	public static boolean hasExceededAnEdge(int sqr, int vector) {
		if (!isOnTheBoard(sqr))
			return true;
		return (Chess.containsEast1Direction(vector) && isFileHSqr(sqr + Chess.WEST_1))
				|| (Chess.containsWest1Direction(vector) && isFileASqr(sqr + Chess.EAST_1))
				|| (Chess.containsEast2Direction(vector) && (isFileHSqr(sqr + Chess.WEST_2) || isFileGSqr(sqr + Chess.WEST_2)))
				|| (Chess.containsWest2Direction(vector) && (isFileASqr(sqr + Chess.EAST_2) || isFileBSqr(sqr + Chess.EAST_2)));
	}
	
	/**
	 * Returns true if this square is on the board.
	 * 
	 * @param sqr int index of the square to be tested
	 * @return <code>true</code> if this square is one of the 64 squares on the board; <code>false</code>
	 *         otherwise.
	 */
	public static boolean isOnTheBoard(int sqr) {
		return (sqr >= A8_SQR) && (sqr <= H1_SQR);
	}
	
	/**
	 * Returns true if the square is on the 1st rank of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on the 1st rank;
	 *         <code>false</code> otherwise.
	 */
	public static boolean isRank1Sqr(int sqr) {
		return (sqr >= A1_SQR) && (sqr <= H1_SQR);
	}

	/**
	 * Returns true if the square is on the 2nd rank of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on the 2nd rank;
	 *         <code>false</code> otherwise.
	 */
	public static boolean isRank2Sqr(int sqr) {
		return (sqr >= A2_SQR) && (sqr <= H2_SQR);
	}

	/**
	 * Returns true if the square is on the 7th rank of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on the 7th rank;
	 *         <code>false</code> otherwise.
	 */
	public static boolean isRank7Sqr(int sqr) {
		return (sqr >= A7_SQR) && (sqr <= H7_SQR);
	}

	/**
	 * Returns true if the square is on the 8th rank of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on the 8th rank;
	 *         <code>false</code> otherwise.
	 */
	public static boolean isRank8Sqr(int sqr) {
		return (sqr >= A8_SQR) && (sqr <= H8_SQR);
	}

	/**
	 * Returns true if the square is on file A of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on file A; <code>false</code>
	 *         otherwise.
	 */
	public static boolean isFileASqr(int sqr) {
		return sqr % WIDTH == 0;
	}

	/**
	 * Returns true if the square is on file B of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on file B; <code>false</code>
	 *         otherwise.
	 */
	public static boolean isFileBSqr(int sqr) {
		return (sqr + Chess.WEST_1) % WIDTH == 0;
	}

	/**
	 * Returns true if the square is on file G of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on file G; <code>false</code>
	 *         otherwise.
	 */
	public static boolean isFileGSqr(int sqr) {
		return (sqr + Chess.EAST_2) % WIDTH == 0;
	}

	/**
	 * Returns true if the square is on file H of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on file H; <code>false</code>
	 *         otherwise.
	 */
	public static boolean isFileHSqr(int sqr) {
		return (sqr + Chess.EAST_1) % WIDTH == 0;
	}
	
}
