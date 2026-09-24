package common.keyboard;

import common.keyboard.listener.KeyboardListener;
import common.main.ErrorHandle;
import common.main.ErrorListener;
import common.robot.KeyboardRobotExecutor;


/*
┌──────────── スマホ ────────────┐

入力欄を選択
    ↓
AccessibilityService
    ↓
INPUT_ENABLE
    │
    │
    ▼
└───── TCP通信 ──────────────────┘
    │
    ▼
┌──────────── PC ────────────────┐
PCキーボード入力
    ↓
Windows IME
    ↓
TSF
    ↓
CompositionState生成
    ↓
COMPOSITION_UPDATE
    │
    ▼
└───── TCP通信 ──────────────────┘
    │
    ▼
┌──────────── スマホ ────────────┐

変換中文字列を
オーバーレイ表示

[第3] [正規] [系]

└───────────────────────────────┘


PCで変換確定
    ↓
COMMIT "第3正規系"
    ↓
TCP
    ↓
スマホ
    ↓
オーバーレイ削除
    ↓
実際の入力欄へ入力
 */
public abstract class KeyboardManager implements ErrorHandle {
	protected KeyboardListener listener;
    private ErrorListener errorListener;
    protected KeyboardRobotExecutor robotExecuter;

	public void setEventListener(KeyboardListener listener) {
		this.listener = listener;
	}

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public abstract void start();

    public abstract void close();

    @Override
    public abstract void errorHandle();
}
