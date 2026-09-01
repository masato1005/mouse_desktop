package common.Keyboard;

import common.Listener.KeyboardEventListener;
import common.Listener.implemented.ImplementedKeyboardListener;
import common.main.ErrorHandle;
import common.main.ErrorListener;


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

inputEnabled = true
    ↓
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
public class KeyboardManager implements ErrorHandle {
	private final ImplementedKeyboardListener listener = new ImplementedKeyboardListener();
    private ErrorListener errorListener;

	public void setEventListener(KeyboardEventListener eventListener) {
		listener.setListener(eventListener);
	}



    @Override
    public void errorHandle() {
        
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }
}
