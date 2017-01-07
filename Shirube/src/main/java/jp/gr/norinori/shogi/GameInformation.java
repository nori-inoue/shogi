package jp.gr.norinori.shogi;

/**
 * ゲーム情報
 *
 * @author nori
 *
 */
public class GameInformation {
	private GameProtocol gameProtocol;

	// コンストラクタ===========================================================
	/**
	 * ゲーム情報のインスタンスを生成する
	 */
	public GameInformation() {
	}

	// メソッド=============================================================
	/**
	 * ゲームプロトコルを設定する
	 *
	 * @param gameProtocol
	 */
	public void setGameProtocol(GameProtocol gameProtocol) {
		this.gameProtocol = gameProtocol;
	}

	/**
	 * ゲームプロトコルを取得する
	 *
	 * @return ゲームプロトコル
	 */
	public GameProtocol getGameProtocol() {
		return this.gameProtocol;
	}

}
