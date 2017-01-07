package jp.gr.norinori.shogi;


/**
 * アクションエンジン
 *
 * @author nori
 */
public interface ActionEngine {
	/**
	 * アクションを取得
	 *
	 * @param scene
	 * @return アクション
	 */
	public Action action(Scene scene);
}
