package chessengine.system;

/**
 * Stores the information for a single chess position
 * and can assemble all the legal moves that can be made in that position.
 * 
 * @author Darcy McCoy
 * @version %I%
 * @since 1.0
 */
public class Position {
	
	/** Whether white is the color to play in the current position. */
	private boolean whiteToPlay;
	
	/** The board of the position stored as a 64 character <code>String</code>. */
	private String board;

	// Cardinal and ordinal direction vector
	/** North 1 square from white's perspective of the board. */
	private static final int NORTH_1 = -8;
	/** North 2 squares from white's perspective of the board. */
	private static final int NORTH_2 = NORTH_1 + NORTH_1;
	/** North east 1 square from white's perspective of the board. */
	private static final int NORTH_1_EAST_1 = -7;
	/** East 1 square from white's perspective of the board. */
	private static final int EAST_1 = 1;
	/** East 2 squares from white's perspective of the board. */
	private static final int EAST_2 = EAST_1 + EAST_1;
	/** East 3 squares from white's perspective of the board. */
	private static final int EAST_3 = EAST_1 + EAST_1 + EAST_1;
	/** South east 1 square from white's perspective of the board. */
	private static final int SOUTH_1_EAST_1 = 9;
	/** South 1 square from white's perspective of the board. */
	private static final int SOUTH_1 = 8;
	/** South 2 squares from white's perspective of the board. */
	private static final int SOUTH_2 = SOUTH_1 + SOUTH_1;
	/** South west 1 square from white's perspective of the board. */
	private static final int SOUTH_1_WEST_1 = 7;
	/** West 1 square from white's perspective of the board. */
	private static final int WEST_1 = -1;
	/** West 2 squares from white's perspective of the board. */
	private static final int WEST_2 = WEST_1 + WEST_1;
	/** West 3 squares from white's perspective of the board. */
	private static final int WEST_3 = WEST_1 + WEST_1 + WEST_1;
	/** West 4 squares from white's perspective of the board. */
	private static final int WEST_4 = WEST_1 + WEST_1 + WEST_1 + WEST_1;
	/** North west 1 square from white's perspective of the board. */
	private static final int NORTH_1_WEST_1 = -9;

	// specific squares
	/** The index of the A8 square on the board */
	private static final int A8_SQR = 0;
	/** The index of the E8 square on the board */
	private static final int E8_SQR = 4;
	/** The index of the H8 square on the board */
	private static final int H8_SQR = 7;
	/** The index of the A7 square on the board */
	private static final int A7_SQR = 8;
	/** The index of the H7 square on the board */
	private static final int H7_SQR = 15;
	/** The index of the A2 square on the board */
	private static final int A2_SQR = 48;
	/** The index of the H2 square on the board */
	private static final int H2_SQR = 55;
	/** The index of the A1 square on the board */
	private static final int A1_SQR = 56;
	/** The index of the E1 square on the board */
	private static final int E1_SQR = 60;
	/** The index of the H1 square on the board */
	private static final int H1_SQR = 63;
	
	/**
	 * Default constructor (standard starting chess position, white is to play).
	 */
	public Position() {
		this(true,
				"rnbq2bnr" + "pppppppp" + "--------" + "--------" + "--------" + "--------" + "PPPPPPPP" + "RNBQ5BNR");
	}
	
	/**
	 * Parameterized constructor specifying the board and which color is to play.
	 * 
	 * @param whiteToPlay boolean whether white is currently to play 
	 * @param board string 64 characters long where each character is a 
	 * square on the board starting from A8 and moving left and down
	 */
	public Position(boolean whiteToPlay, String board) {
		this.whiteToPlay = whiteToPlay;
		this.board = board;
	}
	
	/**
	 * Copy constructor to copy a <code>Position</code>.
	 * 
	 * @param otherPosition the <code>Position</code> to copy
	 */
	public Position(Position otherPosition) {
		this(otherPosition.whiteToPlay, otherPosition.board);
	}
	
	/**
	 * Returns a copy of this position.
	 * 
	 * @return <code>Position</code> that is a copy of this
	 */
	public Position clone() {
		return new Position(this);
	}
	
	/**
	 * Returns an array of all legal moves that the color to move can make
	 * 
	 * @return int[] each <code>int</code> is a move
	 * @throws NoLegalMovesException if the color to play has no legal moves in this <code>Position</code> 
	 */
	public int[] findLegalMoves() throws NoLegalMovesException {
		int[] possibleMoves = findPossibleMoves();
		int[] legalMoves = new int[possibleMoves.length];
		int numberOfLegalMoves = 0;

		for (int i = 0; i < possibleMoves.length; i++) {
			if (!isSelfCheckMove(possibleMoves[i])
					&& (!isCastling(atSqr(possibleMoves[i] / 100), possibleMoves[i]) || !isCheck())) {
				legalMoves[numberOfLegalMoves++] = possibleMoves[i];
			}
		}
		legalMoves = removeElementsThatAreZero(legalMoves, numberOfLegalMoves);
		if (!(legalMoves.length == 0)) {
			return legalMoves;
		} else {
			throw new NoLegalMovesException("There are no legal moves for the current player in this position");
		}
	}
	
	/**
	 * Updates isWhiteToPlay and the board so that the move has been played.
	 * Accounts for castling, en passant and promotion.
	 * 
	 * @param move int, the move to be made
	 */
	public void makeMove(int move) {
		char pieceToPut = atSqr(move / 100);

		if (isCastling(pieceToPut, move)) {

			if ((move / 100) == ((move % 100) + WEST_2)) {
				// Kingside castling
				updateSqr(atSqr(move % 100 + EAST_1), move % 100 + WEST_1);
				updateSqr('-', move % 100 + EAST_1);
			} else {
				// Queenside castling
				updateSqr(atSqr(move % 100 + WEST_2), move % 100 + EAST_1);
				updateSqr('-', move % 100 + WEST_2);
			}

		} else if (isEnPassant(pieceToPut, move)) {

			if (whiteToPlay)
				updateSqr('-', move % 100 + SOUTH_1);
			else
				updateSqr('-', move % 100 + NORTH_1);

		} else if (isAllowsEnPassant(pieceToPut, move)) {

			if (whiteToPlay)
				pieceToPut = 'E';
			else
				pieceToPut = 'e';
			
		} else if (isPromotion(pieceToPut, move)) {

			if (whiteToPlay)
				pieceToPut = 'Q';
			else
				pieceToPut = 'q';
		}
		
		if ((atSqr(move / 100) == '5') || (atSqr(move / 100) == '4') || (atSqr(move / 100) == '3')) {
			// Updating white king for castling ability for king moves
			pieceToPut = 'K';
		} else if ((atSqr(move / 100) == '2') || (atSqr(move / 100) == '1') || (atSqr(move / 100) == '0')) {
			// Updating black king for castling ability for king moves
			pieceToPut = 'k';
		} else {
			// Updating king castling ability for non-king moves
			updateKingCastlingAbility(move);
		}
		removeEnPassantAbility();
		
		updateSqr('-', move / 100);
		updateSqr(pieceToPut, move % 100);
		whiteToPlay = !whiteToPlay;
	}
	
	/**
	 * Returns true when the move is legal for this position. Works for both impossible and pseudo legal moves.
	 * 
	 * @param move int describing the move
	 * @return <code>true</code> if the move is legal for this position; 
	 *         <code>false</code> otherwise.
	 */
	public boolean isLegalMove(int move) {
		if(((move / 100) <= H1_SQR) && ((move % 100) <= H1_SQR) && ((move / 100) >= A8_SQR) && ((move % 100) >= A8_SQR)) {
			int[] tempMoves = findPossiblePieceMoves(atSqr(move / 100), move / 100);
			for (int i = 0; i < tempMoves.length; i++) {
				if (move == tempMoves[i])
					return !isSelfCheckMove(move) && (!isCastling(atSqr(move / 100), move) || !isCheck());
			}
		}
		return false;
	}
	
	/**
	 * Returns an array of all pseudo legal moves and legal moves that the color to move can make.
	 * This can include illegal moves (such as self check moves, castling out of check).
	 * 
	 * @return int[] each <code>int</code> is a move
	 */
	public int[] findPossibleMoves() {
		int[] possibleMoves = new int[218];
		int[] possiblePieceMoves = new int[0];
		int numberOfPossibleMoves = 0;
		
		for (int i = 0; i < board.length(); i++) {
			if ((isEmptySqr(i)) || (isOtherColorAtSqr(i)))
				continue;
			
			possiblePieceMoves = findPossiblePieceMoves(atSqr(i), i);
			for (int j = 0; j < possiblePieceMoves.length; j++) {
				possibleMoves[numberOfPossibleMoves++] = possiblePieceMoves[j];
			}
		}
		return removeElementsThatAreZero(possibleMoves, numberOfPossibleMoves);
	}
	
	/**
	 * Returns an array of pseudo legal moves that the piece can make. 
	 * 
	 * @param piece character representing the piece
	 * @param pieceSqr int value where the piece is on the board
	 * @return int[] each <code>int</code> is a move
	 */
	public int[] findPossiblePieceMoves(char piece, int pieceSqr) {
		switch (piece) {
		case 'P':
		case 'p':
		case 'e':
		case 'E':
			return findPawnMoves(pieceSqr);

		case 'R':
		case 'r':
			return findStraightMoves(pieceSqr);

		case 'N':
		case 'n':
			return findKnightMoves(pieceSqr);

		case 'B':
		case 'b':
			return findDiagonalMoves(pieceSqr);

		case 'Q':
		case 'q':
			return findQueenMoves(pieceSqr);

		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case 'k':
		case 'K':
			return findKingMoves(pieceSqr);
			
		default:
			return new int[0];
		}
	}
	
	/**
	 * Returns an array of moves that the knight can make
	 * 
	 * @param knightSqr int value where the piece is on the board
	 * @return int[] each <code>int</code> is a move
	 */
	public int[] findKnightMoves(int knightSqr) {
		int[] knightMoves = new int[8];
		int numberOfMoves = 0;
		int[] inspectMoves = { NORTH_2 + EAST_1, NORTH_1 + EAST_2, SOUTH_1 + EAST_2, SOUTH_2 + EAST_1, 
				SOUTH_2 + WEST_1, SOUTH_1 + WEST_2, NORTH_1 + WEST_2, NORTH_2 + WEST_1 };

		// Assessing rank square
		if (isRank8Sqr(knightSqr)) {
			inspectMoves[7] = 0;
			inspectMoves[6] = 0;
			inspectMoves[1] = 0;
			inspectMoves[0] = 0;
		} else if (isRank7Sqr(knightSqr)) {
			inspectMoves[7] = 0;
			inspectMoves[0] = 0;
		} else if (isRank1Sqr(knightSqr)) {
			inspectMoves[5] = 0;
			inspectMoves[4] = 0;
			inspectMoves[3] = 0;
			inspectMoves[2] = 0;
		} else if (isRank2Sqr(knightSqr)) {
			inspectMoves[4] = 0;
			inspectMoves[3] = 0;
		}

		// Assessing file square
		if (isFileHSqr(knightSqr)) {
			inspectMoves[3] = 0;
			inspectMoves[2] = 0;
			inspectMoves[1] = 0;
			inspectMoves[0] = 0;
		} else if (isFileGSqr(knightSqr)) {
			inspectMoves[2] = 0;
			inspectMoves[1] = 0;
		} else if (isFileASqr(knightSqr)) {
			inspectMoves[7] = 0;
			inspectMoves[6] = 0;
			inspectMoves[5] = 0;
			inspectMoves[4] = 0;
		} else if (isFileBSqr(knightSqr)) {
			inspectMoves[6] = 0;
			inspectMoves[5] = 0;
		}
		for (int inspectMove : inspectMoves) {
			if ((inspectMove != 0) && (isOtherColorAtSqr(knightSqr + inspectMove)
					|| isEmptySqr(knightSqr + inspectMove))) {
				knightMoves[numberOfMoves++] = knightSqr * 100 + knightSqr + inspectMove;
			}
		}
		return removeElementsThatAreZero(knightMoves, numberOfMoves);
	}
	
	/**
	 * Returns an array of moves that the king can make
	 * 
	 * @param kingSqr int value where the piece is on the board
	 * @return int[] each <code>int</code> is a move
	 */
	public int[] findKingMoves(int kingSqr) {
		char piece = atSqr(kingSqr);
		int[] kingMoves = new int[10];
		int numberOfMoves = 0;
		int[] inspectMoves = { NORTH_1, NORTH_1_EAST_1, EAST_1, SOUTH_1_EAST_1, SOUTH_1, SOUTH_1_WEST_1, WEST_1, NORTH_1_WEST_1 };

		if (isFileHSqr(kingSqr)) {
			inspectMoves[1] = 0;
			inspectMoves[2] = 0;
			inspectMoves[3] = 0;
		} else if (isFileASqr(kingSqr)) {
			inspectMoves[5] = 0;
			inspectMoves[6] = 0;
			inspectMoves[7] = 0;
		}
		if (isRank1Sqr(kingSqr)) {
			inspectMoves[3] = 0;
			inspectMoves[4] = 0;
			inspectMoves[5] = 0;
		} else if (isRank8Sqr(kingSqr)) {
			inspectMoves[0] = 0;
			inspectMoves[1] = 0;
			inspectMoves[7] = 0;
		}
		for (int inspectMove : inspectMoves) {
			if ((inspectMove != 0) && (isOtherColorAtSqr(kingSqr + inspectMove)
					|| isEmptySqr(kingSqr + inspectMove))) {
				kingMoves[numberOfMoves++] = kingSqr * 100 + kingSqr + inspectMove;
			}
		}

		// Kingside castling
		if (((((piece == '5') || (piece == '4')) && (atSqr(kingSqr + EAST_3) == 'R')) 
				|| (((piece == '2') || (piece == '1')) && (atSqr(kingSqr + EAST_3) == 'r')))
				&& (isEmptySqr(kingSqr + EAST_1)) && (isEmptySqr(kingSqr + EAST_2))) {
			kingMoves[numberOfMoves++] = kingSqr * 100 + kingSqr + EAST_2;
		}
		// Queenside castling
		if (((((piece == '5') || (piece == '3')) && (atSqr(kingSqr + WEST_4) == 'R')) 
				|| (((piece == '2') || (piece == '0')) && (atSqr(kingSqr + WEST_4) == 'r')))
				&& (isEmptySqr(kingSqr + WEST_1)) && (isEmptySqr(kingSqr + WEST_2)) && (isEmptySqr(kingSqr + WEST_3))) {
			kingMoves[numberOfMoves++] = kingSqr * 100 + kingSqr + WEST_2;
		}
		return removeElementsThatAreZero(kingMoves, numberOfMoves);
	}
	
	/**
	 * Returns an array of moves that the queen can make
	 * 
	 * @param queenSqr int value where the piece is on the board
	 * @return int[] each <code>int</code> is a move
	 */
	public int[] findQueenMoves(int queenSqr) {
		int[] tempStraightMoves = findStraightMoves(queenSqr);
		int[] tempDiagonalMoves = findDiagonalMoves(queenSqr);
		int[] queenMoves = new int[tempStraightMoves.length + tempDiagonalMoves.length];

		for (int i = 0; i < tempStraightMoves.length; i++) {
			queenMoves[i] = tempStraightMoves[i];
		}
		for (int i = 0; i < tempDiagonalMoves.length; i++) {
			queenMoves[i + tempStraightMoves.length] = tempDiagonalMoves[i];
		}
		return queenMoves;
	}
	
	/**
	 * Returns an array of moves that the pawn can make.
	 * 
	 * @param pawnSqr int value where the piece is on the board
	 * @return int[] each <code>int</code> is a move
	 */
	public int[] findPawnMoves(int pawnSqr) {
		int[] pawnMoves = new int[12];
		int numberOfMoves = 0;

		if (whiteToPlay) {
			// White pawns
			if (isEmptySqr(pawnSqr + NORTH_1)) {
				pawnMoves[numberOfMoves++] = (pawnSqr * 100) + (pawnSqr + NORTH_1);
				if ((isRank2Sqr(pawnSqr)) && (isEmptySqr(pawnSqr + NORTH_2))) {
					pawnMoves[numberOfMoves++] = (pawnSqr * 100) + (pawnSqr + NORTH_2);
				}
			}
			if (isFileASqr(pawnSqr)) {
				if ((isOtherColorAtSqr(pawnSqr + NORTH_1_EAST_1)) || (atSqr(pawnSqr + EAST_1) == 'e')) {
					pawnMoves[numberOfMoves++] = (pawnSqr * 100) + (pawnSqr + NORTH_1_EAST_1);
				}
			} else if (isFileHSqr(pawnSqr)) {
				if ((isOtherColorAtSqr(pawnSqr + NORTH_1_WEST_1)) || (atSqr(pawnSqr + WEST_1) == 'e')) {
					pawnMoves[numberOfMoves++] = (pawnSqr * 100) + (pawnSqr + NORTH_1_WEST_1);
				}
			} else {
				if ((isOtherColorAtSqr(pawnSqr + NORTH_1_EAST_1)) || (atSqr(pawnSqr + EAST_1) == 'e')) {
					pawnMoves[numberOfMoves++] = (pawnSqr * 100) + (pawnSqr + NORTH_1_EAST_1);
				}
				if ((isOtherColorAtSqr(pawnSqr + NORTH_1_WEST_1)) || (atSqr(pawnSqr + WEST_1) == 'e')) {
					pawnMoves[numberOfMoves++] = (pawnSqr * 100) + (pawnSqr + NORTH_1_WEST_1);
				}
			}
		} else {
			// Black pawns
			if (isEmptySqr(pawnSqr + SOUTH_1)) {
				pawnMoves[numberOfMoves++] = (pawnSqr * 100) + (pawnSqr + SOUTH_1);
				if ((isRank7Sqr(pawnSqr)) && (isEmptySqr(pawnSqr + SOUTH_2))) {
					pawnMoves[numberOfMoves++] = (pawnSqr * 100) + (pawnSqr + SOUTH_2);
				}
			}
			if (isFileASqr(pawnSqr)) {
				if ((isOtherColorAtSqr(pawnSqr + SOUTH_1_EAST_1)) || (atSqr(pawnSqr + EAST_1) == 'E')) {
					pawnMoves[numberOfMoves++] = (pawnSqr * 100) + (pawnSqr + SOUTH_1_EAST_1);
				}
			} else if (isFileHSqr(pawnSqr)) {
				if ((isOtherColorAtSqr(pawnSqr + SOUTH_1_WEST_1)) || (atSqr(pawnSqr + WEST_1) == 'E')) {
					pawnMoves[numberOfMoves++] = (pawnSqr * 100) + (pawnSqr + SOUTH_1_WEST_1);
				}
			} else {
				if ((isOtherColorAtSqr(pawnSqr + SOUTH_1_EAST_1)) || (atSqr(pawnSqr + EAST_1) == 'E')) {
					pawnMoves[numberOfMoves++] = (pawnSqr * 100) + (pawnSqr + SOUTH_1_EAST_1);
				}
				if ((isOtherColorAtSqr(pawnSqr + SOUTH_1_WEST_1)) || (atSqr(pawnSqr + WEST_1) == 'E')) {
					pawnMoves[numberOfMoves++] = (pawnSqr * 100) + (pawnSqr + SOUTH_1_WEST_1);
				}
			}
		}
		return removeElementsThatAreZero(pawnMoves, numberOfMoves);
	}
	
	/**
	 * Returns an array of moves along straight directions.
	 * 
	 * @param pieceSqr int value where the piece is on the board
	 * @return int[] each <code>int</code> is a move
	 */
	public int[] findStraightMoves(int pieceSqr) {
		int[] straightMoves = new int[28];
		int numberOfMoves = 0;

		// Direction north
		for (int inspectSqr = (pieceSqr + NORTH_1); inspectSqr >= A8_SQR; inspectSqr += NORTH_1) {
			if (isEmptySqr(inspectSqr)) {
				straightMoves[numberOfMoves++] = (pieceSqr * 100) + inspectSqr;
			} else if (isOtherColorAtSqr(inspectSqr)) {
				straightMoves[numberOfMoves++] = (pieceSqr * 100) + inspectSqr;
				break;
			} else {
				break;
			}
		}

		// Direction south
		for (int inspectSqr = (pieceSqr + SOUTH_1); inspectSqr <= H1_SQR; inspectSqr += SOUTH_1) {
			if (isEmptySqr(inspectSqr)) {
				straightMoves[numberOfMoves++] = (pieceSqr * 100) + inspectSqr;
			} else if (isOtherColorAtSqr(inspectSqr)) {
				straightMoves[numberOfMoves++] = (pieceSqr * 100) + inspectSqr;
				break;
			} else {
				break;
			}
		}

		// Direction east
		for (int inspectSqr = (pieceSqr + EAST_1); !isFileHSqr(inspectSqr + WEST_1); inspectSqr += EAST_1) {
			if (isEmptySqr(inspectSqr)) {
				straightMoves[numberOfMoves++] = (pieceSqr * 100) + inspectSqr;
			} else if (isOtherColorAtSqr(inspectSqr)) {
				straightMoves[numberOfMoves++] = (pieceSqr * 100) + inspectSqr;
				break;
			} else {
				break;
			}
		}

		// Direction west
		for (int inspectSqr = (pieceSqr + WEST_1); !isFileASqr(inspectSqr + EAST_1); inspectSqr += WEST_1) {
			if (isEmptySqr(inspectSqr)) {
				straightMoves[numberOfMoves++] = (pieceSqr * 100) + inspectSqr;
			} else if (isOtherColorAtSqr(inspectSqr)) {
				straightMoves[numberOfMoves++] = (pieceSqr * 100) + inspectSqr;
				break;
			} else {
				break;
			}
		}
		return removeElementsThatAreZero(straightMoves, numberOfMoves);
	}
	
	/**
	 * Returns an array of moves along diagonal directions
	 * 
	 * @param pieceSqr int value where the piece is on the board
	 * @return int[] each <code>int</code> is a move
	 */
	public int[] findDiagonalMoves(int pieceSqr) {
		int[] diagonalMoves = new int[28];
		int numberOfMoves = 0;
		// Direction north-east
		for (int inspectSqr = (pieceSqr + NORTH_1_EAST_1); (inspectSqr >= A8_SQR)
				&& (!isFileHSqr(inspectSqr + SOUTH_1_WEST_1)); inspectSqr += NORTH_1_EAST_1) {
			if (isEmptySqr(inspectSqr)) {
				diagonalMoves[numberOfMoves++] = (pieceSqr * 100) + inspectSqr;
			} else if (isOtherColorAtSqr(inspectSqr)) {
				diagonalMoves[numberOfMoves++] = (pieceSqr * 100) + inspectSqr;
				break;
			} else {
				break;
			}
		}
		// Direction south-east
		for (int inspectSqr = (pieceSqr + SOUTH_1_EAST_1); (inspectSqr <= H1_SQR)
				&& (!isFileHSqr(inspectSqr + NORTH_1_WEST_1)); inspectSqr += SOUTH_1_EAST_1) {
			if (isEmptySqr(inspectSqr)) {
				diagonalMoves[numberOfMoves++] = (pieceSqr * 100) + inspectSqr;
			} else if (isOtherColorAtSqr(inspectSqr)) {
				diagonalMoves[numberOfMoves++] = (pieceSqr * 100) + inspectSqr;
				break;
			} else {
				break;
			}
		}
		// Direction south-west
		for (int inspectSqr = (pieceSqr + SOUTH_1_WEST_1); (inspectSqr <= H1_SQR)
				&& (!isFileASqr(inspectSqr + NORTH_1_EAST_1)); inspectSqr += SOUTH_1_WEST_1) {
			if (isEmptySqr(inspectSqr)) {
				diagonalMoves[numberOfMoves++] = (pieceSqr * 100) + inspectSqr;
			} else if (isOtherColorAtSqr(inspectSqr)) {
				diagonalMoves[numberOfMoves++] = (pieceSqr * 100) + inspectSqr;
				break;
			} else {
				break;
			}
		}
		// Direction north-west
		for (int inspectSqr = (pieceSqr + NORTH_1_WEST_1); (inspectSqr >= A8_SQR)
				&& (!isFileASqr(inspectSqr + SOUTH_1_EAST_1)); inspectSqr += NORTH_1_WEST_1) {
			if (isEmptySqr(inspectSqr)) {
				diagonalMoves[numberOfMoves++] = (pieceSqr * 100) + inspectSqr;
			} else if (isOtherColorAtSqr(inspectSqr)) {
				diagonalMoves[numberOfMoves++] = (pieceSqr * 100) + inspectSqr;
				break;
			} else {
				break;
			}
		}
		return removeElementsThatAreZero(diagonalMoves, numberOfMoves);
	}
	
	/**
	 * Returns a new array without the elements which are zeroes. 
	 * The numberNonZeroElements must match the number of non zero elements in the array.
	 * 
	 * @param arrayToUpdate the integer array to be updated
	 * @param numberNonZeroElements int number of elements that aren't zeroes
	 * @return int[] without any elements that are zero
	 */
	public int[] removeElementsThatAreZero(int[] arrayToUpdate, int numberNonZeroElements) {
		int[] newArray = new int[numberNonZeroElements];
		for (int i = 0, j = 0; j < newArray.length; i++, j++) {
			if (arrayToUpdate[i] != 0)
				newArray[j] = arrayToUpdate[i];
			else
				j--;
		}
		return newArray;
	}
	
	/**
	 * Returns true if the move is promotion.
	 * 
	 * @param piece character representing the piece
	 * @param move int, move the piece is making
	 * @return <code>true</code> if the move is a pawn promoting; 
	 *         <code>false</code> otherwise.
	 */
	public boolean isPromotion(char piece, int move) {
		if (piece == 'P')
			return (isRank8Sqr(move % 100));
		else if (piece == 'p')
			return (isRank1Sqr(move % 100));
		else
			return false;
	}
	
	/**
	 * Returns true if the move is castling.
	 * 
	 * @param piece character representing the piece
	 * @param move int, move the piece is making
	 * @return <code>true</code> if the move is a king castling; 
	 *         <code>false</code> otherwise.
	 */
	public boolean isCastling(char piece, int move) {
		return ((piece == 'K') || (piece == '5') || (piece == '4') || (piece == '3') || (piece == 'k') || (piece == '2')
				|| (piece == '1') || (piece == '0'))
				&& (((move / 100) == ((move % 100) + EAST_2)) || ((move / 100) == ((move % 100) + WEST_2)));
	}
	
	/**
	 * Returns true if the move is en passant.
	 * 
	 * @param piece character representing the piece
	 * @param move int, move the piece is making
	 * @return <code>true</code> if the move is a pawn capturing en passant; 
	 *         <code>false</code> otherwise.
	 */
	public boolean isEnPassant(char piece, int move) {
		if (piece == 'P')
			return atSqr((move % 100) + SOUTH_1) == 'e';
		else if (piece == 'p')
			return atSqr((move % 100) + NORTH_1) == 'E';
		else
			return false;
	}
	
	/**
	 * Returns true if the move puts a pawn into a position where it can be captured en passant.
	 * 
	 * @param piece character representing the piece
	 * @param move int, move the piece is making
	 * @return <code>true</code> if the move is a pawn advancing 2 squares and potentially allowing itself
	 * 			to be captured en passant; <code>false</code> otherwise.
	 */
	public boolean isAllowsEnPassant(char piece, int move) {
		if (piece == 'P')
			return (((move / 100) + NORTH_2) == (move % 100)) && ((atSqr((move % 100) + EAST_1) == 'p') || (atSqr((move % 100) + WEST_1) == 'p'));
		else if (piece == 'p')
			return (((move / 100) + SOUTH_2) == (move % 100)) && ((atSqr((move % 100) + EAST_1) == 'P') || (atSqr((move % 100) + WEST_1) == 'P'));
		else
			return false;
	}
	
	/**
	 * Returns true if the color to move is in check.
	 * 
	 * @return <code>true</code> if this position has the color to move's king attacked; 
	 *         <code>false</code> otherwise.
	 */
	public boolean isCheck() {
		return isAttackedSqr(findKingSqr(whiteToPlay), !whiteToPlay);
	}
	
	/**
	 * Returns true if the move puts the king (of the color who makes that move)
	 * into check or castles that king through check.
	 * 
	 * @param move int, move to be assessed
	 * @return <code>true</code> if this move puts the color that made it into check; 
	 *         <code>false</code> otherwise.
	 */
	public boolean isSelfCheckMove(int move) {
		Position tempPosition = new Position(this);
		tempPosition.makeMove(move);
		return tempPosition.isAttackedSqr(tempPosition.findKingSqr(!tempPosition.whiteToPlay), tempPosition.whiteToPlay)
				|| (isCastling(atSqr(move / 100), move)
						&& tempPosition.isAttackedSqr(((move / 100) + (move % 100)) / 2, tempPosition.whiteToPlay));
	}
	
	/**
	 * Returns true if the square is attacked by a piece of the corresponding color.
	 * 
	 * @param sqr int, the square to be assessed
	 * @param whiteIsAttacking boolean whether white is the color to be assessed on it's attacking of the square
	 * @return <code>true</code> if this square is attacked by the chosen color; 
	 *         <code>false</code> otherwise.
	 */
	public boolean isAttackedSqr(int sqr, boolean whiteIsAttacking) {
		Position tempPosition = clone();
		if ((isWhitePiece(atSqr(sqr)) || (isEmptySqr(sqr))) && whiteIsAttacking)
			tempPosition.updateSqr('q', sqr);
		else if ((isBlackPiece(atSqr(sqr)) || (isEmptySqr(sqr))) && !whiteIsAttacking)
			tempPosition.updateSqr('Q', sqr);
		
		if (whiteToPlay != whiteIsAttacking)
			tempPosition.whiteToPlay = whiteIsAttacking;
		
		int[] tempMoves = tempPosition.findPossibleMoves();
		for (int i = 0; i < tempMoves.length; i++) {
			if ((tempMoves[i] % 100) == sqr)
				return true;
		}
		return (atSqr(sqr) == 'E') || (atSqr(sqr) == 'e');
	}
	
	/**
	 * Returns one of the kings' squares.
	 * 
	 * @param whiteKingColor <code>boolean</code> whether white is the color of the king to be found
	 * @return int value where the king is on the board
	 */
	public int findKingSqr(boolean whiteKingColor) {
		for (int i = 0; i < board.length(); i++) {
			if ((whiteKingColor && ((atSqr(i) == 'K') || (atSqr(i) == '5') || (atSqr(i) == '4') || (atSqr(i) == '3')))
					|| (!whiteKingColor
							&& ((atSqr(i) == 'k') || (atSqr(i) == '2') || (atSqr(i) == '1') || (atSqr(i) == '0'))))
				return i;
		}
		return -1;
	}
	
	/**
	 * Returns true if the square is on the 1st rank of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on the 1st rank; <code>false</code> otherwise.
	 */
	public boolean isRank1Sqr(int sqr) {
		return (sqr >= A1_SQR) && (sqr <= H1_SQR);
	}
	
	/**
	 * Returns true if the square is on the 2nd rank of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on the 2nd rank; <code>false</code> otherwise.
	 */
	public boolean isRank2Sqr(int sqr) {
		return (sqr >= A2_SQR) && (sqr <= H2_SQR);
	}
	
	/**
	 * Returns true if the square is on the 7th rank of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on the 7th rank; <code>false</code> otherwise.
	 */
	public boolean isRank7Sqr(int sqr) {
		return (sqr >= A7_SQR) && (sqr <= H7_SQR);
	}
	
	/**
	 * Returns true if the square is on the 8th rank of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on the 8th rank; <code>false</code> otherwise.
	 */
	public boolean isRank8Sqr(int sqr) {
		return (sqr >= A8_SQR) && (sqr <= H8_SQR);
	}
	
	/**
	 * Returns true if the square is on file A of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on file A; <code>false</code> otherwise.
	 */
	public boolean isFileASqr(int sqr) {
		return sqr % 8 == 0;
	}
	
	/**
	 * Returns true if the square is on file B of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on file B; <code>false</code> otherwise.
	 */
	public boolean isFileBSqr(int sqr) {
		return (sqr + WEST_1) % 8 == 0;
	}
	
	/**
	 * Returns true if the square is on file G of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on file G; <code>false</code> otherwise.
	 */
	public boolean isFileGSqr(int sqr) {
		return (sqr + EAST_2) % 8 == 0;
	}
	
	/**
	 * Returns true if the square is on file H of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on file H; <code>false</code> otherwise.
	 */
	public boolean isFileHSqr(int sqr) {
		return (sqr + EAST_1) % 8 == 0;
	}
	
	/**
	 * Returns true if the piece at this square is the opposite color of the color who is to play.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if there is a piece of the opposite color at this square; <code>false</code> otherwise.
	 */
	public boolean isOtherColorAtSqr(int sqr) {
		if (whiteToPlay)
			return isBlackPiece(atSqr(sqr));
		else
			return isWhitePiece(atSqr(sqr));
	}
	
	/**
	 * Returns true if there is no piece at this square.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square has no piece on it; <code>false</code> otherwise.
	 */
	public boolean isEmptySqr(int sqr) {
		return atSqr(sqr) == '-';
	}
	
	/**
	 * Updates this square on the board to either empty or to the piece that is passed.
	 * 
	 * @param charToPut character representing the piece or '-' for an empty square
	 * @param sqr int value of the square to be updated
	 */
	public void updateSqr(char charToPut, int sqr) {
		board = board.substring(A8_SQR, sqr) + charToPut + board.substring(sqr + 1);
	}
	
	/**
	 * Updates the king's castling ability for non king moves.
	 * 
	 * @param move int, move the (non king) piece is making
	 */
	public void updateKingCastlingAbility(int move) {
		// Updating white king for castling ability
		if ((atSqr(E1_SQR) == '5') && (((move / 100) == H1_SQR) || ((move % 100) == H1_SQR)))
			updateSqr('3', E1_SQR);
		else if ((atSqr(E1_SQR) == '5') && (((move / 100) == A1_SQR) || ((move % 100) == A1_SQR)))
			updateSqr('4', E1_SQR);
		else if (((atSqr(E1_SQR) == '4') && (((move / 100) == H1_SQR) || ((move % 100) == H1_SQR))) 
				|| ((atSqr(E1_SQR) == '3') && (((move / 100) == A1_SQR) || ((move % 100) == A1_SQR))))
			updateSqr('K', E1_SQR);
		// Updating black king for castling ability
		else if ((atSqr(E8_SQR) == '2') && (((move / 100) == H8_SQR) || ((move % 100) == H8_SQR)))
			updateSqr('0', E8_SQR);
		else if ((atSqr(E8_SQR) == '2') && (((move / 100) == A8_SQR) || ((move % 100) == A8_SQR)))
			updateSqr('1', E8_SQR);
		else if (((atSqr(E8_SQR) == '1') && (((move / 100) == H8_SQR) || ((move % 100) == H8_SQR)))
				|| ((atSqr(E8_SQR) == '0') && (((move / 100) == A8_SQR) || ((move % 100) == A8_SQR))))
			updateSqr('k', E8_SQR);
	}
	
	/**
	 * Updates the board so that pawns that could have been captured en passant become regular pawns.
	 */
	public void removeEnPassantAbility() {
		for (int i = 0; i < board.length(); i++) {
			if (atSqr(i) == 'E')
				updateSqr('P', i);
			else if (atSqr(i) == 'e')
				updateSqr('p', i);
		}
	}
	
	/**
	 * Returns the contents of a square.
	 * 
	 * @param sqr int value the square 
	 * @return character contents of the square
	 */
	public char atSqr(int sqr) {
		return board.charAt(sqr);
	}
	
	/**
	 * Returns true if the piece is white.
	 * 
	 * @param piece character corresponding to this piece
	 * @return <code>true</code> if this is a white piece; <code>false</code> otherwise.
	 */
	public boolean isWhitePiece(char piece) {
		return ((piece == 'R') || (piece == 'N') || (piece == 'B') || (piece == 'Q') || (piece == '5') || (piece == '4')
				|| (piece == '3') || (piece == 'K') || (piece == 'P') || (piece == 'E'));
	}
	
	/**
	 * Returns true if the piece is black.
	 * 
	 * @param piece character corresponding to this piece
	 * @return <code>true</code> if this is a black piece; <code>false</code> otherwise.
	 */
	public boolean isBlackPiece(char piece) {
		return ((piece == 'r') || (piece == 'n') || (piece == 'b') || (piece == 'q') || (piece == '2') || (piece == '1')
				|| (piece == '0') || (piece == 'k') || (piece == 'p') || (piece == 'e'));
	}
	
	/**
	 * Returns a string with the player who is to move and the board laid out in a 8x8 grid.
	 * 
	 * @return String with the color who is to move and representation of the board 
	 */
	public String toString() {
		String printPosition = "";
		if (whiteToPlay)
			printPosition = "White to move:\n";
		else
			printPosition = "Black to move:\n";

		for (int i = 0; i < board.length(); i++) {
			printPosition += atSqr(i) + " ";
			if (isFileHSqr(i))
				printPosition += "\n";
		}
		return printPosition;
	}
	
	/**
	 * @return <code>true</code> if white is to play; <code>false</code> otherwise.
	 */
	public boolean isWhiteToPlay() {
		return whiteToPlay;
	}
	
	/**
	 * @param whiteToPlay <code>boolean</code> whether to set white as the color to play
	 */
	public void setWhiteToPlay(boolean whiteToPlay) {
		this.whiteToPlay = whiteToPlay;
	}
	
	/**
	 * @return String, the board as 64 characters starting from square A8
	 */
	public String getBoard() {
		return board;
	}
	
	/**
	 * @param board <code>String</code>, the new board
	 */
	public void setBoard(String board) {
		this.board = board;
	}

}
