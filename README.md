# ShareOneMouse

同一ネットワーク上のPCまたはAndroid端末とマウス操作を共有するためのアプリケーションです。PC版はJavaで実装されており、UDPで接続先を探索した後、TCPで操作データを送受信します。

## 動作環境

- Windows
- JDK 21
- Gradle Wrapper
- 接続する端末が同じLAN内に存在すること
- TCP/UDPポート `5000` を利用できること

## 起動方法

VS Codeでは `.vscode/launch.json` の `AppLauncher` 構成を選択して実行します。

ターミナルから起動する場合は次を実行します。

```powershell
.\gradlew.bat run
```

起動クラスは `common.main.AppLauncher` です。

## ClientとServerの役割

現在の役割は次のようになっています。

| 種類 | 役割 |
| --- | --- |
| Client | 手元のマウスを監視し、移動・クリック・ホイール操作を送信する側 |
| Server | 受信した操作を実行し、カーソルを動かされる側 |

PC同士で利用する場合は、操作するPCでClient、操作されるPCでServerを選択します。

Android版はUDP/TCPのServerとして待ち受けるため、Androidへ接続するPCではClientを選択します。

## 接続とマウス移行の流れ

1. ServerがUDPおよびTCPの待ち受けを開始します。
2. ClientがUDPでServerを探索します。
3. 接続先を検出するとTCP接続を確立します。
4. Clientでカーソルを移行させる画面端を選択します。
5. 選択した端へカーソルが到達すると、Clientが `TOUCH_WALL` を送信します。
6. Clientは透明ウィンドウを表示し、ローカルカーソルを画面中央へ固定します。
7. 中央からの移動量を `MOVE` としてServerへ送信します。
8. Serverは受信した移動量、クリック、ホイール操作を実行します。
9. 反対側の画面端へ到達すると `TOUCH_WALL` を返し、操作をClientへ戻します。

対応する画面端は `NORTH`、`SOUTH`、`WEST`、`EAST` の4方向です。

## 現在の完成状況

| 機能 | 状況 | 補足 |
| --- | --- | --- |
| AppLauncherからの起動 | 実装済み | VS CodeおよびGradleから起動可能 |
| Client／Server選択GUI | 実装済み | 起動後に役割を選択 |
| AppTypeと共通処理の分離 | 実装済み | `client`、`server`、`common` に分割 |
| UDPによるServer探索 | 実装済み | ポート5000を使用 |
| TCPによるJSON送受信 | 実装済み | 1行ごとのJSONとして送受信 |
| 壁方向の選択と通知 | 実装済み | 4方向に対応 |
| マウス移動量の送信 | 実装済み | Clientで中央固定後に `dx`、`dy` を送信 |
| PC側でのマウス移動 | 実装済み | `Robot` を使用 |
| 左・右・中央クリック | 実装済み | Server側で受信して実行 |
| ホイール操作 | 実装済み | Server側で受信して実行 |
| Androidとのマウス共有 | 実装済み・確認継続中 | Android側のAccessibility Serviceが必要 |
| Win + Escによる強制終了 | 実装済み | Windowsのグローバルホットキーを使用 |
| Keyboard入力の共有 | 未完成 | 基礎クラスと入力欄判定のみ存在 |
| 自動テスト | 未実装 | 実機を使った回帰確認が中心 |
| 接続認証・暗号化 | 未実装 | 現状は信頼できるLAN内での利用を想定 |

## 既知の制約

- 主画面のサイズを基準としているため、マルチモニター環境は未対応です。
- 不正な外側JSONを受信すると、現在のTCP受信ループは終了します。
- Clientはマウス移行中、移動量が0の場合も一定間隔で `MOVE` を送信します。
- 自動再接続、接続相手の認証、通信の暗号化は未実装です。
- Androidとの接続では、Android側でAccessibility Serviceを有効にする必要があります。

## Keyboard入力の現在地

Keyboard関連のパッケージは用意されていますが、ネットワークを通して文字入力を共有する一連の処理はまだ接続されていません。

現在実装されているものは次のとおりです。

- `KeyboardManager`、`KeyboardHandler`、`KeyboardListener` の基本構造
- `DataType.KEYBOARD`
- `KeyboardEventListener.INPUT_ENABLE`
- Windows UI Automationを利用した `CheckInputtable`
- フォーカス中の要素が編集可能かを判定する処理
- UI Automationの開始、フォーカス変更監視、COMリソース解放

`CheckInputtable` は、フォーカス中のWindows要素を次の状態へ分類できます。

- `EDITABLE`: ValuePatternによって編集可能と判断
- `LIKELY_EDITABLE`: Edit／Document要素でTextPatternを利用可能
- `NOT_EDITABLE`: 入力対象ではない
- `UNKNOWN`: UI Automationのエラーなどで判定不能

一方、次の処理は未実装または未接続です。

- `KeyboardManager`の起動・終了処理
- `CheckInputtable`と`KeyboardManager`の接続
- PCキーボードのグローバル入力取得
- キー押下・解放用データの定義
- 修飾キーの状態管理
- Windows IME／TSFからの変換中文字列取得
- KeyboardデータのJSON変換と送受信
- 受信側でのキー入力または文字列入力
- Android側の入力欄検出、変換中表示、文字列確定
- 切断時に押下中のキーを解放する処理

## 今後のKeyboard入力設計

想定している最終的な処理は次のとおりです。

```text
Androidで入力欄を選択
        ↓
Accessibility ServiceがINPUT_ENABLEをPCへ送信
        ↓
PCがキーボード入力の転送を開始
        ↓
Windows IME／TSFから変換中文字列を取得
        ↓
COMPOSITION_UPDATEをAndroidへ送信
        ↓
Androidで変換中文字列を表示
        ↓
PCで変換を確定
        ↓
COMMITをAndroidへ送信し、実際の入力欄へ反映
```

実装は次の順序で進める予定です。

### 1. Keyboard通信仕様を定義する

マウスとは別に `KeyboardData` を追加し、少なくとも次のイベントを扱います。

- `INPUT_ENABLE`
- `INPUT_DISABLE`
- `KEY_DOWN`
- `KEY_UP`
- `COMPOSITION_UPDATE`
- `COMMIT`
- `CANCEL`

キーイベントにはキーコード、文字列、修飾キー、繰り返し入力の有無を含めます。PC版とAndroid版で列挙値とJSON項目名を一致させる必要があります。

### 2. KeyboardManagerへ状態管理を追加する

`KeyboardManager`が次の状態を管理します。

- 接続中か
- 相手側の入力欄が有効か
- PC側の入力欄へ入力中ではないか
- IME変換中か
- 押下中のキーと修飾キー

相手側の入力欄が無効な場合や、PC自身の入力欄へ入力している場合は転送を停止します。

### 3. PCキーボード入力を取得する

Windowsの低レベルキーボードフックを専用スレッドで動作させ、`KEY_DOWN` と `KEY_UP` を取得します。フックのコールバックでは重い処理を行わず、ネットワーク送信用のキューへ渡します。

### 4. IME／TSFへ対応する

日本語入力では単純なキーコード転送だけでは変換結果が一致しないため、TSFから変換中文字列と確定文字列を取得します。

- 変換中: `COMPOSITION_UPDATE`
- 確定時: `COMMIT`
- 取消時: `CANCEL`

まず英数字のキー入力を完成させ、その後にIME対応を追加すると切り分けやすくなります。

### 5. 受信側へ文字列を反映する

PCを受信側にする場合は、`Robot`によるキー入力と文字列貼り付けのどちらを採用するかを決めます。

Androidを受信側にする場合は、Accessibility Serviceからフォーカス中の入力要素へ `ACTION_SET_TEXT` などを実行します。変換中文字列は確定前に実入力せず、オーバーレイ表示として保持する方法を想定しています。

### 6. 異常終了とテストを追加する

- 切断時に押下中のキーをすべて解放
- 同じキーの連続押下と長押し
- Shift、Ctrl、Alt、Windowsキーの組み合わせ
- 日本語IMEの変換、確定、取消
- 接続切断後の再接続
- PC入力欄とAndroid入力欄の切り替え

## 開発時の確認

Javaコードのコンパイル:

```powershell
.\gradlew.bat compileJava
```

Gitへ追加する前に、`build/` と `.gradle/` が含まれていないことを確認してください。これらは `.gitignore` で除外されています。
