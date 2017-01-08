package jp.gr.norinori.shogi.honshogi;

import java.util.ArrayList;
import java.util.List;

import jp.gr.norinori.core.element.KeyValuePair;
import jp.gr.norinori.shogi.Direction;
import jp.gr.norinori.shogi.Piece;
import jp.gr.norinori.shogi.PieceLocations;
import jp.gr.norinori.shogi.PieceMove;
import jp.gr.norinori.shogi.Point;
import jp.gr.norinori.shogi.Timer;
import jp.gr.norinori.shogi.honshogi.piece.Fu;
import jp.gr.norinori.shogi.honshogi.piece.Keima;
import jp.gr.norinori.shogi.honshogi.piece.Kyo;
import jp.gr.norinori.shogi.honshogi.piece.Ou;

/**
 *
 * 本将棋 Zone Of Control 管理
 *
 * @author nori
 */
public class PieceZoneOfControlManager {
	// メンバ===================================================================
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
	private List<Point> outeBlockLine;
	private boolean isOuteLine = false;
	private boolean existsOuteLineBlock = false;

	// コンストラクタ===========================================================
	/**
	 * 本将棋ZOC管理のインスタンスを生成する
	 *
	 * @param honShogeiScene 本将棋局面
	 * @param player プレイヤー
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

	// メソッド=================================================================
	/**
	 * 軌道を追加する
	 */
	public void addLine(Point newPoint) {
		// 盤の範囲内の場合は追加する
		if (!HonShogiField.isRange(newPoint)) {
			// 王手軌道が既にある場合は追加
			if (this.isOuteLine && this.outeLine != null) {
				for (Point outeLinePoint : this.outeLine) {
					PieceMove outePieceMove = new PieceMove(this.point, outeLinePoint, this.piece, this.piece);
					this.pieceZoneOfControl.addOuteLine(outePieceMove);
				}
			}

			this.outeLine = new ArrayList<>();
			this.outeBlockLine = new ArrayList<>();
			this.isOuteLine = false;
			this.existsOuteLineBlock = false;
			return;
		}

		this.pieceZoneOfControl.addLine(newPoint, this.piece);

		// 王への軌道がある場合、王手軌道を設定(自駒が間にある場合はthis.line=null)
		if (this.outeLine != null || this.outeBlockLine != null) {

			if (newPoint.equals(this.otherOuPoint)) {
				PieceMove outePieceMove;

				if (this.outeLine != null) {
					for (Point outeLinePoint : this.outeLine) {
						outePieceMove = new PieceMove(this.point, outeLinePoint, this.piece, this.piece);
						this.pieceZoneOfControl.addOuteLine(outePieceMove);
					}
					outePieceMove = new PieceMove(this.point, newPoint, this.piece, this.piece);
					this.pieceZoneOfControl.addOuteLine(outePieceMove);

					this.isOuteLine = true;
				}

				if (this.outeBlockLine != null) {
					for (Point outeLinePoint : this.outeBlockLine) {
						outePieceMove = new PieceMove(this.point, outeLinePoint, this.piece, this.piece);
						this.pieceZoneOfControl.addOuteBlockLine(outePieceMove);
					}
				}

				this.outeLine = new ArrayList<>();
				this.outeBlockLine = null;
				this.existsOuteLineBlock = false;
			} else {
				if (this.outeLine != null) {
					this.outeLine.add(newPoint);
				}

				if (this.outeBlockLine != null) {
					this.outeBlockLine.add(newPoint);
				}

				// 王手軌道のブロックを確認
				checkBlock(newPoint);
			}
		}
	}

	// 王手軌道のブロックを確認
	private void checkBlock(Point newPoint) {
		// 自分の駒にぶつかる場合
		if (this.pieceLocations.existsPiece(newPoint)) {

			if (this.isOuteLine) {
				for (Point outeLinePoint : this.outeLine) {
					PieceMove outePieceMove = new PieceMove(this.point, outeLinePoint, this.piece, this.piece);
					this.pieceZoneOfControl.addOuteLine(outePieceMove);
				}
			}
			this.isOuteLine = false;
			this.existsOuteLineBlock = false;
			this.outeLine = null;
			this.outeBlockLine = null;
			return;
		}

		// 相手の駒にぶつかる場合
		if (this.isOuteLine == false && this.otherPieceLocations.existsPiece(newPoint)) {
			// 既にブロックしている駒が存在する場合、王手軌道なし
			if (this.existsOuteLineBlock) {
				this.outeBlockLine = null;
			} else {
				this.existsOuteLineBlock = true;
			}
			this.outeLine = null;
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

		// 駒の移動位置判定
		if (newPiece.type instanceof Fu) {
			if (this.player.getDirection() == Direction.UP) {
				if (newPoint.y == 0) {
					return false;
				}
			} else {
				if (newPoint.y == HonShogiField.MAX_Y) {
					return false;
				}
			}
		} else if (newPiece.type instanceof Kyo) {
			if (this.player.getDirection() == Direction.UP) {
				if (newPoint.y == 0) {
					return false;
				}
			} else {
				if (newPoint.y == HonShogiField.MAX_Y) {
					return false;
				}
			}
		} else if (newPiece.type instanceof Keima) {
			if (this.player.getDirection() == Direction.UP) {
				if (newPoint.y <= 1) {
					return false;
				}
			} else {
				if (newPoint.y >= HonShogiField.MAX_Y - 1) {
					return false;
				}
			}
		}
		// 盤面位置の判定
		if (!HonShogiField.isRange(newPoint)) {
			return false;
		}

		// 影響範囲に追加
		this.pieceZoneOfControl.addEffectiveRange(newPoint, newPiece);

		// 自分の駒にぶつかる場合、影響範囲、移動可能範囲終了
		if (this.pieceLocations.existsPiece(newPoint)) {
			this.isOuteLine = false;
			this.existsOuteLineBlock = false;
			this.outeLine = null;
			this.outeBlockLine = null;
			return false;
		}

		// 移動可能範囲に追加
		PieceMove pieceMove = new PieceMove(this.point, newPoint, this.piece, newPiece);
		this.pieceZoneOfControl.add(pieceMove);

		// 相手の駒にぶつかる場合、影響範囲、移動可能範囲終了
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
		this.outeBlockLine = new ArrayList<>();
	}
}
