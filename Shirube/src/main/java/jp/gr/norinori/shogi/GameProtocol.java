package jp.gr.norinori.shogi;

/**
 * ゲームプロトコルインタフェース
 *
 * @author nori
 */
public interface GameProtocol {
	/**
	 * 初期局面の取得
	 *
	 * @param gameInformation
	 * @return 初期局面
	 */
	public Scene initializeScene(GameInformation gameInformation);

	/**
	 * 局面の移動可能範囲を解析する
	 *
	 * @param 局面
	 * @return アクション結果状態
	 */
	public ActionStatus analyzeScene(Scene scene);

	/**
	 * アクション結果判定
	 *
	 * @param scene 局面
	 * @param lastAction 最終アクション
	 * @return アクション判定結果
	 */
	public ActionResult judge(Scene scene, Action lastAction);

	/**
	 * 次局面を設定する
	 *
	 * @param scene 局面
	 * @param action アクション
	 * @return アクション結果状態
	 */
	public ActionStatus nextPhage(Scene scene, Action action);

	/**
	 * 局面を表示する
	 *
	 * @param scene 局面
	 * @param action アクション
	 */
	public void displayScene(Scene scene, Action action);
}
