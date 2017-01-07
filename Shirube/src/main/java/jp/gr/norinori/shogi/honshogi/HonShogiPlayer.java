package jp.gr.norinori.shogi.honshogi;

import java.util.List;

import jp.gr.norinori.core.collection.NumberingHashMap;
import jp.gr.norinori.core.collection.NumberingMap;
import jp.gr.norinori.shogi.ActionEngine;
import jp.gr.norinori.shogi.Direction;
import jp.gr.norinori.shogi.Piece;
import jp.gr.norinori.shogi.PieceLocations;
import jp.gr.norinori.shogi.PieceType;
import jp.gr.norinori.shogi.Player;
import jp.gr.norinori.shogi.Point;

public class HonShogiPlayer implements Player {
	public final static int SENTE = 1;
	public final static int GOTE = 2;

	private int playerid;
	private String name;
	private PieceLocations pieceLocations;
	private NumberingMap<PieceType, Integer> stocks;
	private Direction direction;
	private ActionEngine actionEngine;

	public HonShogiPlayer(int playerid) {
		this.pieceLocations = new PieceLocations();
		this.stocks = new NumberingHashMap<>();
		this.playerid = playerid;
	}

	protected HonShogiPlayer() {
	}

	@Override
	public int getId() {
		return this.playerid;
	}

	@Override
	public void setId(int playerid) {
		this.playerid = playerid;
	}

	@Override
	public PieceLocations getPieceLocations() {
		return this.pieceLocations;
	}

	@Override
	public void addPiece(Point point, Piece piece) {
		this.pieceLocations.put(point, piece);
	}

	@Override
	public Piece removePiece(Point point) {
		return this.pieceLocations.remove(point);
	}

	@Override
	public Piece getPiece(Point point) {
		return this.pieceLocations.get(point);
	}

	@Override
	public void addStock(PieceType pieceType) {
		int number = 0;
		if (this.stocks.containsKey(pieceType)) {
			number = this.stocks.get(pieceType);
		}
		number++;
		this.stocks.put(pieceType, number);
	}

	@Override
	public void removeStock(PieceType pieceType) {
		int number = this.stocks.get(pieceType);
		number--;
		if (number == 0) {
			this.stocks.remove(pieceType);
		} else {
			this.stocks.put(pieceType, number);
		}
	}

	@Override
	public List<PieceType> getStockList() {
		return this.stocks.keyList();
	}

	@Override
	public NumberingMap<PieceType, Integer> getStocks() {
		return this.stocks;
	}

	@Override
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	@Override
	public Direction getDirection() {
		return this.direction;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setActionEngine(ActionEngine actionEngine) {
		this.actionEngine = actionEngine;
	}

	@Override
	public ActionEngine getActionEngine() {
		return this.actionEngine;
	}

	public Player clone() {
		HonShogiPlayer newPlayer = new HonShogiPlayer();
		newPlayer.playerid = this.playerid;
		newPlayer.name = this.name;
		newPlayer.pieceLocations = this.pieceLocations.clone();
		newPlayer.stocks = new NumberingHashMap<>(this.stocks);
		newPlayer.direction = this.direction;
		newPlayer.actionEngine = this.actionEngine;

		return newPlayer;
	}
}
