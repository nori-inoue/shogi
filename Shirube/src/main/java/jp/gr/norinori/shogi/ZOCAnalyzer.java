package jp.gr.norinori.shogi;

public interface ZOCAnalyzer {

	/**
	 * 移動可能範囲を局面から解析する
	 *
	 * @param scene 局面F
	 */
	public void analyzePieceZoneOfControl(Scene scene);
}
