package Keyboard;

import Handler.KeyboardHandler;
import Listener.implemented.ImplementedKeyboardListener;


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
public class KeyboardManager {
	private final ImplementedKeyboardListener listener = new ImplementedKeyboardListener();

	public void setListener(KeyboardHandler keyboardHandler) {
		listener.setListener(keyboardHandler);
	}
}
