package jp.gr.norinori.shogi.honshogi;

import java.util.ArrayList;
import java.util.List;

import jp.gr.norinori.core.element.KeyValuePair;
import jp.gr.norinori.shogi.Piece;
import jp.gr.norinori.shogi.PieceLocations;
import jp.gr.norinori.shogi.PieceMove;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.Timer;
import jp.gr.norinori.shogi.honshogi.piece.Ou;

/**
 *
 * 本将棋 Zone Of Control 管理
 *
 * @author nori
 */
public class PieceZoneOfControlManager {
	public HonShogiScene honShogeiScene;
	public HonShogiPieceZoneOfControl pieceZoneOfControl;
	public Point ouPoint;
	public Point otherOuPoint;
	public PieceLocations pieceLocations;
	public PieceLocations otherPieceLocations;

	public HonShogiPlayer player;
	public HonShogiPlayer otherPlayer;

	public Point point;
	public Piece piece;

	private List<Point> outeLine;
	private boolean isOuteLine = false;

	/**
	 *
	 * @param honShogeiScene
	 * @param player
	 */
	PieceZoneOfControlManager(HonShogiScene honShogeiScene, HonShogiPlayer player, HonShogiPlayer otherPlayer) {
		this.pieceZoneOfControl = new HonShogiPieceZoneOfControl();

		this.honShogeiScene = honShogeiScene;

		this.player = player;
		this.otherPlayer = otherPlayer;

		this.pieceLocations = player.getPieceLocations();
		this.otherPieceLocations = otherPlayer.getPieceLocations();

		for (KeyValuePair<Point, Piece> p : this.otherPieceLocations) {
			if (p.getValue().type instanceof Ou) {
				this.otherOuPoint = p.getKey();
				break;
			}
		}
	}

	/**
	 * 軌道を追加する
	 */
	public void addLine(Point newPoint) {
		this.pieceZoneOfControl.addLine(newPoint, this.piece);

		// 王への軌道がある場合、王手軌道を設定(自駒が間にある場合はthis.line=null)
		if (this.outeLine != null) {
			if (newPoint.equals(this.otherOuPoint)) {
				PieceMove outePieceMove;

				for (Point outeLinePoint : this.outeLine) {
					outePieceMove = new PieceMove(this.point, outeLinePoint, this.piece, this.piece);
					this.pieceZoneOfControl.addOuteLine(outePieceMove);
				}
				outePieceMove = new PieceMove(this.point, newPoint, this.piece, this.piece);
				this.pieceZoneOfControl.addOuteLine(outePieceMove);

				this.outeLine = new ArrayList<>();
				this.isOuteLine = true;
			} else {
				this.outeLine.add(newPoint);
			}
		}
	}

	/**
	 * 移動可能範囲確認
	 *
	 * @return true:移動可能 / false:移動終了
	 */
	public boolean validateAndAdd(Point newPoint) {
		return validateAndAdd(newPoint, this.piece);
	}

	/**
	 * 移動可能範囲確認
	 *
	 * @return true:移動可能 / false:移動終了
	 */
	public boolean validateAndAdd(Point newPoint, Piece newPiece) {
		if (!HonShogiField.isRange(newPoint)) {
			// 王手軌道が既にある場合は追加
			if (this.isOuteLine && this.outeLine != null) {
				for (Point outeLinePoint : this.outeLine) {
					PieceMove outePieceMove = new PieceMove(this.point, outeLinePoint, this.piece, this.piece);
					this.pieceZoneOfControl.addOuteLine(outePieceMove);
				}
			}

			this.outeLine = new ArrayList<>();
			this.isOuteLine = false;
			return false;
		}

		Timer.start("addEffectiveRange", "addPieceLocations");
		this.pieceZoneOfControl.addEffectiveRange(newPoint, newPiece);
		Timer.stop("addEffectiveRange");

		// 自分の駒にぶつかる場合
		if (this.pieceLocations.existsPiece(newPoint)) {
			// 王手軌道が既にある場合は追加
			if (this.isOuteLine && this.outeLine != null) {
				for (Point outeLinePoint : this.outeLine) {
					PieceMove outePieceMove = new PieceMove(this.point, outeLinePoint, this.piece, this.piece);
					this.pieceZoneOfControl.addOuteLine(outePieceMove);
				}
			}
			this.outeLine = null;
			return false;
		}

		PieceMove pieceMove = new PieceMove(this.point, newPoint, this.piece, newPiece);
		this.pieceZoneOfControl.add(pieceMove);

		// 相手の駒にぶつかる場合
		if (this.otherPieceLocations.existsPiece(newPoint)) {
			// 相手駒が王の場合は、王手駒として登録
			if (newPoint.equals(this.otherOuPoint)) {
				this.pieceZoneOfControl.addOute(pieceMove);
			}
			return false;
		}

		// 自分の駒にぶつからない場合（空いている場合）
		return true;
	}

	/**
	 * 位置と駒を設定する
	 *
	 * @param point
	 * @param piece
	 */
	public void setCurrent(Point point, Piece piece) {
		this.point = point;
		this.piece = piece;

		Timer.start("setEffectiveRangeSource", "setCurrent");
		this.pieceZoneOfControl.setEffectiveRangeSource(point, piece);
		Timer.stop("setEffectiveRangeSource");
		Timer.start("setLineSource", "setCurrent");
		this.pieceZoneOfControl.setLineSource(point, piece);
		Timer.stop("setLineSource");

		if (this.piece.type instanceof Ou) {
			this.ouPoint = point;
		}

		this.outeLine = new ArrayList<>();
	}
}
