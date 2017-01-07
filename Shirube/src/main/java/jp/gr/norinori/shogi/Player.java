package jp.gr.norinori.shogi;

import java.util.List;

import jp.gr.norinori.core.collection.NumberingMap;


public interface Player {
	/**
	 * プレイヤーIDを取得する.
	 *
	 * @return プレイヤーID
	 */
	public int getId();

	/**
	 *
	 * @param playerid
	 */
	public void setId(int playerid);

	/**
	 * プレイヤー名を取得する.
	 *
	 * @return プレイヤー名
	 */
	public String getName();

	/**
	 * プレイヤー名を設定する.
	 *
	 * @param name
	 */
	public void setName(String name);

	/**
	 *
	 * @return
	 */
	public PieceLocations getPieceLocations();

	/**
	 *
	 * @param point
	 * @param piece
	 */
	public void addPiece(Point point, Piece piece);

	/**
	 *
	 * @param point
	 * @return
	 */
	public Piece removePiece(Point point);

	/**
	 *
	 * @param point
	 * @return
	 */
	public Piece getPiece(Point point);

	/**
	 *
	 * @param piece
	 */
	public void addStock(PieceType pieceType);

	/**
	 *
	 * @param pieceType
	 */
	public void removeStock(PieceType pieceType);

	/**
	 *
	 * @return
	 */
	public List<PieceType> getStockList();

	/**
	 *
	 * @return
	 */
	public NumberingMap<PieceType, Integer> getStocks();

	/**
	 *
	 * @param direction
	 */
	public void setDirection(Direction direction);

	/**
	 *
	 * @return
	 */
	public Direction getDirection();

	/**
	 *
	 * @param actionEngine
	 */
	public void setActionEngine(ActionEngine actionEngine);

	/**
	 *
	 * @return
	 */
	public ActionEngine getActionEngine();

	/**
	 *
	 * @return
	 */
	public Player clone();
}
