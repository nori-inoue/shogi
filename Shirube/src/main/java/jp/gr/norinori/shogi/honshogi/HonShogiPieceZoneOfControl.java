package jp.gr.norinori.shogi.honshogi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.gr.norinori.core.collection.NumberingHashMap;
import jp.gr.norinori.core.collection.NumberingMap;
import jp.gr.norinori.shogi.EffectiveRange;
import jp.gr.norinori.shogi.Piece;
import jp.gr.norinori.shogi.PieceMove;
import jp.gr.norinori.shogi.PieceZoneOfControl;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.Timer;
import jp.gr.norinori.shogi.honshogi.piece.Ou;

/**
 * ZOC情報
 *
 * @author nori
 */
public class HonShogiPieceZoneOfControl implements PieceZoneOfControl {
	private List<PieceMove> list;
	private List<PieceMove> ouList;
	private NumberingMap<Piece, EffectiveRange> effectiveRanges;
	private Map<Point, Integer> effectiveRangePoint;
	private NumberingMap<Piece, EffectiveRange> line;
	private List<PieceMove> outeList;
	private List<PieceMove> outeLine;
	private int effectiveRangeSize;

	// コンストラクタ===========================================================
	/**
	 * 移動可能範囲のインスタンスを生成する
	 */
	public HonShogiPieceZoneOfControl() {
		this.list = new ArrayList<>();
		this.ouList = new ArrayList<>();
		this.effectiveRanges = new NumberingHashMap<>(100);
		this.effectiveRangePoint = new HashMap<>(100);
		this.effectiveRangeSize = 0;
		this.line = new NumberingHashMap<>(100);
		this.outeList = new ArrayList<>();
		this.outeLine = new ArrayList<>();
	}

	/**
	 * 移動可能情報リストを取得する
	 *
	 * @return 移動可能情報リスト
	 */
	public List<PieceMove> getList() {
		return this.list;
	}

	/**
	 * 王の移動可能情報リストを取得する
	 *
	 * @return 王の移動可能情報リスト
	 */
	public List<PieceMove> getOuList() {
		return this.ouList;
	}

	/**
	 * 王手のみの移動可能情報リストを取得する
	 *
	 * @return 王手のみの移動可能情報リスト
	 */
	public List<PieceMove> getOuteList() {
		return this.outeList;
	}

	/**
	 * 移動可能情報を追加する
	 *
	 * @param pieceMove 移動可能情報
	 */
	public void add(PieceMove pieceMove) {
		this.list.add(pieceMove);
		if (pieceMove.fromPiece.type instanceof Ou) {
			this.ouList.add(pieceMove);
		}
	}

	/**
	 * 王手を追加する
	 *
	 * @param pieceMove 移動可能情報
	 */
	public void addOute(PieceMove pieceMove) {
		this.outeList.add(pieceMove);
	}

	/**
	 * 移動可能情報から除外する
	 *
	 * @param pieceMove 移動可能情報
	 */
	public void remove(PieceMove pieceMove) {
		this.list.remove(pieceMove);
		this.ouList.remove(pieceMove);
		this.outeList.remove(pieceMove);
	}

	/**
	 * 移動可能な駒が存在するかを判定する
	 *
	 * @return true:存在する / false:存在しない
	 */
	public boolean isEmpty() {
		return this.list.isEmpty();
	}

	/**
	 * 影響範囲を取得する
	 *
	 * @return 影響範囲
	 */
	public NumberingMap<Piece, EffectiveRange> getEffectiveRanges() {
		return this.effectiveRanges;
	}

	/**
	 * 影響範囲の件数を取得する
	 *
	 * @return 影響範囲の件数
	 */
	public int getEffectiveRangeSize() {
		return this.effectiveRangeSize;
	}

	/**
	 * 影響範囲の件数を取得する
	 *
	 * @return 影響範囲の件数
	 */
	public Map<Point, Integer> getEffectiveRangePoint() {
		return this.effectiveRangePoint;
	}

	/**
	 * 影響範囲を追加する
	 *
	 * @param point 位置
	 * @param piece 駒
	 */
	public void addEffectiveRange(Point point, Piece piece) {
		Timer.start("get", "addEffectiveRange");
		EffectiveRange effectiveRange = this.effectiveRanges.get(piece);
		if (effectiveRange == null) {
			effectiveRange = new EffectiveRange();
		}
		Timer.stop("get");
		Timer.start("add", "addEffectiveRange");
		effectiveRange.add(point);
		Timer.stop("add");
		Timer.start("put", "addEffectiveRange");
		this.effectiveRanges.put(piece, effectiveRange);
		Timer.stop("put");
		this.effectiveRangeSize++;

		Timer.start("effectiveRangePoint", "addEffectiveRange");
		Integer count = Integer.valueOf(1);
		if (this.effectiveRangePoint.containsKey(point)) {
			count = this.effectiveRangePoint.get(point);
			count++;
		}
		this.effectiveRangePoint.put(point, count);
		Timer.stop("effectiveRangePoint");
	}

	/**
	 * 影響範囲のソースを設定する
	 *
	 * @param point 位置
	 * @param piece 駒
	 */
	public void setEffectiveRangeSource(Point point, Piece piece) {
		Timer.start("get", "setEffectiveRangeSource");
		EffectiveRange effectiveRange = this.effectiveRanges.get(piece);
		Timer.stop("get");
		if (effectiveRange == null) {
			Timer.start("new", "setEffectiveRangeSource");
			effectiveRange = new EffectiveRange();
			Timer.stop("new");
			Timer.start("put", "setEffectiveRangeSource");
			this.effectiveRanges.put(piece, effectiveRange);
			Timer.stop("put");
		}
		effectiveRange.source = point;
	}

	/**
	 * 王手軌道を取得する
	 *
	 * @return 軌道
	 */
	public List<PieceMove> getOuteLine() {
		return this.outeLine;
	}

	/**
	 * 軌道を取得する
	 *
	 * @return 軌道
	 */
	public NumberingMap<Piece, EffectiveRange> getLine() {
		return this.line;
	}

	/**
	 * 王手軌道を追加する
	 *
	 * @param pieceMove 移動可能情報
	 */
	public void addOuteLine(PieceMove pieceMove) {
		this.outeLine.add(pieceMove);
	}

	/**
	 * 軌道を追加する
	 *
	 * @param point 位置
	 * @param piece 駒
	 */
	public void addLine(Point point, Piece piece) {
		EffectiveRange effectiveRange;
		if (this.line.containsKey(piece)) {
			effectiveRange = this.line.get(piece);
		} else {
			effectiveRange = new EffectiveRange();
		}
		effectiveRange.add(point);
		this.line.put(piece, effectiveRange);
	}

	/**
	 * 軌道のソースを設定する
	 *
	 * @param point 位置
	 * @param piece 駒
	 */
	public void setLineSource(Point point, Piece piece) {
		EffectiveRange effectiveRange;
		if (this.line.containsKey(piece)) {
			effectiveRange = this.line.get(piece);
		} else {
			effectiveRange = new EffectiveRange();
		}
		effectiveRange.source = point;
		this.line.put(piece, effectiveRange);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" ==== Movable Point ===");
		sb.append(System.lineSeparator());
		for (PieceMove pieceMove : getList()) {
			sb.append(pieceMove);
			sb.append(System.lineSeparator());
		}
		sb.append(" ==== Effective Range ===");
		sb.append(System.lineSeparator());
		for (Entry<Piece, EffectiveRange> en : this.effectiveRanges.entrySet()) {
			sb.append(en.getKey() + " >> ");
			for (Point point : en.getValue().list) {
				sb.append(", " + point);
			}
			sb.append(System.lineSeparator());
		}
		sb.append(" ==== Line ===");
		sb.append(System.lineSeparator());
		for (Entry<Piece, EffectiveRange> en : this.line.entrySet()) {
			sb.append(en.getKey() + " >> ");
			for (Point point : en.getValue().list) {
				sb.append(", " + point);
			}
			sb.append(System.lineSeparator());
		}
		if (!this.outeLine.isEmpty()) {
			sb.append(" ==== Oute Line ===");
			sb.append(System.lineSeparator());
			for (PieceMove pieceMove : this.outeLine) {
				sb.append(pieceMove);
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString();
	}

	public HonShogiPieceZoneOfControl clone() {
		HonShogiPieceZoneOfControl newPieceZoneOfControl = new HonShogiPieceZoneOfControl();
		newPieceZoneOfControl.list = clonePieceMoveList(this.list);
		newPieceZoneOfControl.ouList = clonePieceMoveList(this.ouList);
		newPieceZoneOfControl.effectiveRanges = cloneEffectiveRangeMap(this.effectiveRanges);
		newPieceZoneOfControl.effectiveRangePoint = new HashMap<>(this.effectiveRangePoint);
		newPieceZoneOfControl.line = cloneEffectiveRangeMap(this.line);
		newPieceZoneOfControl.outeList = clonePieceMoveList(this.outeList);
		newPieceZoneOfControl.outeLine = clonePieceMoveList(this.outeLine);
		newPieceZoneOfControl.effectiveRangeSize = this.effectiveRangeSize;

		return newPieceZoneOfControl;
	}

	private List<PieceMove> clonePieceMoveList(List<PieceMove> pieceMoveList) {
		List<PieceMove> list = new ArrayList<>();
		for (PieceMove pieceMove : pieceMoveList) {
			list.add(pieceMove.clone());
		}
		return list;
	}

	private NumberingMap<Piece, EffectiveRange> cloneEffectiveRangeMap(
			NumberingMap<Piece, EffectiveRange> effectiveRangeMap) {
		NumberingMap<Piece, EffectiveRange> newMap = new NumberingHashMap<>(effectiveRangeMap.size());

		for (Entry<Piece, EffectiveRange> en : effectiveRangeMap.entrySet()) {
			newMap.put(en.getKey().clone(), en.getValue().clone());
		}
		return newMap;
	}

}