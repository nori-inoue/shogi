package jp.gr.norinori.shogi;

import java.util.List;

import jp.gr.norinori.core.collection.NumberingMap;

/**
 * 局面
 *
 * @author nori
 */
public interface Scene {
	/**
	 * 手番を設定する
	 *
	 * @param player プレイヤー
	 */
	public void setInitiative(Player player);

	/**
	 *
	 * @return
	 */
	public Player getInitiativePlayer();

	/**
	 * プレイヤーリストを取得する
	 *
	 * @return プレイヤーリスト
	 */
	public NumberingMap<Integer, Player> getPlayers();

	/**
	 * プレイヤーリストにプレイヤーを追加する
	 *
	 * @param player プレイヤー
	 */
	public void addPlayer(Player player);

	/**
	 *
	 * @return 手番以外のプレイヤーリスト
	 */
	public List<Player> getOtherPlayerList();

	/**
	 *
	 * @return
	 */
	public GameInformation getGameInformation();

	/**
	 *
	 * @return
	 */
	public Field getField();

	/**
	 *
	 * @return
	 */
	public PieceLocations getPieceLocations();

	/**
	 *
	 * @param player
	 * @param pieceZoneOfControl
	 */
	public void setPieceZoneOfControl(Player player, PieceZoneOfControl pieceZoneOfControl);

	/**
	 *
	 * @return
	 */
	public PieceZoneOfControl getPieceZoneOfControl(Player player);

	/**
	 *
	 * @return
	 */
	public Scene clone();

	/**
	 *
	 * @return
	 */
	public String getHash();

	/**
	 *
	 * @param point
	 * @return true:盤面に駒が存在する false:盤面に駒が存在しない
	 */
	public boolean existsPiece(Point point);
}
