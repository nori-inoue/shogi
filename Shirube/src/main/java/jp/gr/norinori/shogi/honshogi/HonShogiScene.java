package jp.gr.norinori.shogi.honshogi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.gr.norinori.core.collection.NumberingHashMap;
import jp.gr.norinori.core.collection.NumberingMap;
import jp.gr.norinori.shogi.Direction;
import jp.gr.norinori.shogi.GameInformation;
import jp.gr.norinori.shogi.PieceLocations;
import jp.gr.norinori.shogi.PieceMove;
import jp.gr.norinori.shogi.PieceZoneOfControl;
import jp.gr.norinori.shogi.Player;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.Scene;
import jp.gr.norinori.shogi.honshogiengine.RandomActionEngine;

/**
 *
 * 本将棋局面
 *
 * @author nori
 *
 */
public class HonShogiScene implements Scene {
	// メンバ===================================================================
	private HonShogiField honShogiField;

	private GameInformation gameInformation;
	private HonShogiPlayer initiativePlayer;
	private HonShogiPlayer otherPlayer;
	private NumberingMap<Integer, Player> players;
	private Map<Player, HonShogiPieceZoneOfControl> pieceZoneOfControls;
	private Map<Player, HonShogiPieceZoneOfControl> outeEscapePieceZoneOfControls;
	private boolean[] pieceLocations;

	// コンストラクタ===========================================================
	/**
	 * 本将棋局面のインスタンスを生成する
	 *
	 * @param gameInformation
	 */
	public HonShogiScene(GameInformation gameInformation) {
		this.gameInformation = gameInformation;
		this.pieceZoneOfControls = new HashMap<>();
		this.outeEscapePieceZoneOfControls = new HashMap<>();
		this.honShogiField = new HonShogiField();

		HonShogiPlayer sente = new HonShogiPlayer(HonShogiPlayer.SENTE);
		sente.setName("先手");
		sente.setDirection(Direction.UP);
		sente.setActionEngine(new RandomActionEngine());

		HonShogiPlayer gote = new HonShogiPlayer(HonShogiPlayer.GOTE);
		gote.setName("後手");
		gote.setDirection(Direction.DOWN);
		gote.setActionEngine(new RandomActionEngine());

		this.players = new NumberingHashMap<>();
		this.players.put(HonShogiPlayer.SENTE, sente);
		this.players.put(HonShogiPlayer.GOTE, gote);

		clearExistsPiece();
	}

	/**
	 * 本将棋局面のインスタンスを生成する
	 */
	protected HonShogiScene() {
		this.pieceZoneOfControls = new HashMap<>();
		this.outeEscapePieceZoneOfControls = new HashMap<>();
	}

	// メソッド=================================================================
	/**
	 * 盤面を取得する
	 */
	public HonShogiField getField() {
		return this.honShogiField;
	}

	@Override
	public void setInitiative(Player player) {
		this.initiativePlayer = (HonShogiPlayer) player;
		if (player.getId() == HonShogiPlayer.SENTE) {
			this.otherPlayer = (HonShogiPlayer) this.players.get(HonShogiPlayer.GOTE);
		} else {
			this.otherPlayer = (HonShogiPlayer) this.players.get(HonShogiPlayer.SENTE);
		}
	}

	@Override
	public HonShogiPlayer getInitiativePlayer() {
		return this.initiativePlayer;
	}

	@Override
	public GameInformation getGameInformation() {
		return this.gameInformation;
	}

	@Override
	public PieceLocations getPieceLocations() {
		PieceLocations pieceLocations = new PieceLocations();
		for (Player player : this.players.valueList()) {
			pieceLocations.putAll(player.getPieceLocations());
		}
		return pieceLocations;
	}

	@Override
	public List<Player> getOtherPlayerList() {
		List<Player> otherPlayerList = new ArrayList<>();
		otherPlayerList.add(this.otherPlayer);

		return otherPlayerList;
	}

	/**
	 * 相手番プレイヤーを取得する
	 *
	 * @return 相手番プレイヤー
	 */
	public HonShogiPlayer getOtherPlayer() {
		return this.otherPlayer;
	}

	/**
	 * プレイヤーリストを取得する
	 *
	 * @return プレイヤーリスト
	 */
	public NumberingMap<Integer, Player> getPlayers() {
		return this.players;
	}

	@Override
	public void setPieceZoneOfControl(Player player, PieceZoneOfControl pieceZoneOfControl) {
		this.pieceZoneOfControls.put(player, (HonShogiPieceZoneOfControl) pieceZoneOfControl);
	}

	@Override
	public PieceZoneOfControl getPieceZoneOfControl(Player player) {
		return this.pieceZoneOfControls.get(player);
	}

	/**
	 * 王手ZOC情報をクリアする
	 */
	public void clearOuteEscapePieceZoneOfControls() {
		this.outeEscapePieceZoneOfControls = new HashMap<>();
	}

	/**
	 * 王手ZOC情報を追加する
	 *
	 * @param player
	 * @param pieceMove
	 */
	public void addOuteEscapePieceZoneOfControl(Player player, PieceMove pieceMove) {
		HonShogiPieceZoneOfControl pieceZoneOfControl;
		if (this.outeEscapePieceZoneOfControls.containsKey(player)) {
			pieceZoneOfControl = getOuteEscapePieceZoneOfControls(player);
		} else {
			pieceZoneOfControl = new HonShogiPieceZoneOfControl();
		}
		pieceZoneOfControl.add(pieceMove);

		this.outeEscapePieceZoneOfControls.put(player, pieceZoneOfControl);
	}

	/**
	 * 王手ZOC情報を取得する
	 *
	 * @param player
	 * @return 王手ZOC情報
	 */
	public HonShogiPieceZoneOfControl getOuteEscapePieceZoneOfControls(Player player) {
		return this.outeEscapePieceZoneOfControls.get(player);
	}

	@Override
	public String getHash() {
		return HonShogiSceneHash.getHash(this);
	}

	@Override
	public boolean existsPiece(Point point) {
		int location = point.x * HonShogiField.MAX_X + point.y;
		return this.pieceLocations[location];
	}

	/**
	 * 駒が存在する位置を設定
	 *
	 * @param point 駒が存在する位置
	 */
	public void setExistsPiece(Point point) {
		int location = point.x * HonShogiField.MAX_X + point.y;
		this.pieceLocations[location] = true;
	}

	/**
	 * 駒が存在する位置をクリア
	 */
	public void clearExistsPiece() {
		this.pieceLocations = new boolean[(HonShogiField.MAX_X + 1) * (HonShogiField.MAX_Y + 1)];
		for (int i = 0; i < this.pieceLocations.length; i++) {
			this.pieceLocations[i] = false;
		}
	}

	/**
	 * 局面のクローン
	 */
	public Scene clone() {
		HonShogiScene honShogeiScene = new HonShogiScene();
		honShogeiScene.gameInformation = this.gameInformation;
		honShogeiScene.honShogiField = this.honShogiField;
		honShogeiScene.initiativePlayer = (HonShogiPlayer) this.initiativePlayer.clone();
		honShogeiScene.otherPlayer = (HonShogiPlayer) this.otherPlayer.clone();

		honShogeiScene.players = new NumberingHashMap<>();
		if (honShogeiScene.initiativePlayer.getId() == HonShogiPlayer.SENTE) {
			honShogeiScene.players.put(HonShogiPlayer.SENTE, honShogeiScene.initiativePlayer);
			honShogeiScene.players.put(HonShogiPlayer.GOTE, honShogeiScene.otherPlayer);
		} else {
			honShogeiScene.players.put(HonShogiPlayer.SENTE, honShogeiScene.otherPlayer);
			honShogeiScene.players.put(HonShogiPlayer.GOTE, honShogeiScene.initiativePlayer);
		}

		honShogeiScene.pieceZoneOfControls = clonePieceZoneOfControlMap(this.pieceZoneOfControls,
				honShogeiScene.initiativePlayer, honShogeiScene.otherPlayer);
		honShogeiScene.outeEscapePieceZoneOfControls = clonePieceZoneOfControlMap(this.outeEscapePieceZoneOfControls,
				honShogeiScene.initiativePlayer, honShogeiScene.otherPlayer);

		honShogeiScene.pieceLocations = this.pieceLocations;

		return honShogeiScene;
	}

	private Map<Player, HonShogiPieceZoneOfControl> clonePieceZoneOfControlMap(
			Map<Player, HonShogiPieceZoneOfControl> pieceZoneOfControlMap, Player initiativePlayer,
			Player otherPlayer) {
		Map<Player, HonShogiPieceZoneOfControl> newMap = new HashMap<>(pieceZoneOfControlMap.size());

		for (Entry<Player, HonShogiPieceZoneOfControl> en : pieceZoneOfControlMap.entrySet()) {
			Player player;
			if (en.getKey().getId() == initiativePlayer.getId()) {
				player = initiativePlayer;
			} else {
				player = otherPlayer;
			}
			newMap.put(player, en.getValue().clone());
		}
		return newMap;
	}

}
