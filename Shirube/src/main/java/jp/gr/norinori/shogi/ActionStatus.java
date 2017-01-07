package jp.gr.norinori.shogi;

import java.util.List;

/**
 * アクション結果状態
 *
 * @author nori
 *
 */
public abstract class ActionStatus {

	public boolean isEnd = false;
	public String message = "";
	public List<Player> winners;;

	@Override
	public String toString() {
		return "ActionStatus [message=" + message + "]";
	}

}
