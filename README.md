# ssmix2-jpcore-gateway

`ssmix2-jpcore-gateway` は、SS-MIX2 標準化ストレージの限定的な入力を、日本向け FHIR R4 成果物へ変換するための、読み取り専用で意図的にスコープを絞った OSS ゲートウェイです。出力は実用上可能な範囲で JP Core に合わせます。

## 目的

- SS-MIX2 から FHIR への変換について、保守しやすいリファレンス実装を提供する
- パース、canonical modeling、mapping、validation を明確に分離する
- MVP として理解しやすく、テストしやすいファイルベース取り込みを提供する

## MVP の対象範囲

- 入力: 非常に限定した SS-MIX2 風 fixture データをファイルベースで取り込む
- 出力: `Patient`、`Encounter`、`Observation`、`MedicationRequest`、`DocumentReference` を含む FHIR R4 `Bundle`
- 読み取り専用の変換のみ
- HAPI FHIR ベースの validation を組み込みで提供する

## 非対象

- 完全な EMR の振る舞い
- 双方向同期や write-back
- リアルタイム変換パイプライン
- SMART on FHIR や本番向けの認証・認可
- SS-MIX2 全体の網羅
- すべての日本向けプロファイルへの対応
- 本番投入前提の汎用コンバータ

## アーキテクチャ

このリポジトリは Gradle のマルチモジュール monorepo です。

```text
.
├── app
│   └── Spring Boot REST API、取り込みエンドポイント、監査ログ、設定、ヘルスチェック
├── core
│   └── parser 契約、canonical model、mapping 契約、validation wrapper
├── profiles-jp
│   └── JP Core mapping 定義、fixtures、conformance matrix、出力例
└── deploy
    └── Docker Compose、環境変数テンプレート、ローカル開発用スクリプト、コンテナイメージ
```

変換フローは次のとおりです。

1. `Ssmix2Parser` がディスク上の制約付き入力レイアウトを読む
2. `CanonicalModelAssembler` が canonical intermediate model を組み立てる
3. `FhirBundleMapper` が FHIR R4 `Bundle` を生成する
4. `FhirValidationService` が生成結果を validation する

## canonical modeling を置く理由

canonical modeling は、SS-MIX2 側の source 固有事情を、出力先固有事情から切り離すための層です。これにより core のドメインモデルを FHIR クラスから独立させたまま、欠損項目、ローカルコード、未解決 mapping、raw source text を明示的に保持できます。変換時の仮定を見えやすくし、後続の target 別 serializer を差し替えやすくする狙いがあります。

## この scaffold で使う限定 fixture 形式

この最初の実装では、実際の SS-MIX2 パースはまだ行いません。代わりに、resource type ごとのディレクトリ配下に置いた簡易 fixture を読み込みます。

```text
sample-001/
├── patient/PAT-001.txt
├── encounter/ENC-001.txt
├── observation/OBS-001.txt
├── medication-request/MED-001.txt
└── document-reference/DOC-001.txt
```

各ファイルの中身は `key=value` 形式です。これはパイプラインを明示的かつテスト可能に保つための意図的な制限です。実際の SS-MIX2 標準化ストレージ解析は今後の TODO としています。

## ローカルセットアップ

### 方法 1: Docker Compose

1. `deploy/env/.env.example` を `deploy/env/.env` にコピーする
2. スタックを起動する

   ```bash
   ./deploy/scripts/dev-up.sh
   ```

3. ヘルスチェックを確認する

   ```bash
   curl http://localhost:8080/health
   ```

4. サンプル取り込みを実行する

   ```bash
   curl -X POST http://localhost:8080/ingest/ssmix2 \
     -H 'Content-Type: application/json' \
     -d '{
       "bundleId": "sample-001",
       "facilityId": "demo-hospital",
       "sourcePath": "/fixtures/ssmix2/sample-001"
     }'
   ```

5. 生成された bundle を取得する

   ```bash
   curl http://localhost:8080/fhir/Bundle/sample-001
   ```

### 方法 2: Gradle

ビルド対象は Java 21 で、Gradle wrapper を使います。

```bash
./gradlew test
./gradlew :app:bootRun
```

## 前提と明示的なギャップ

- parser が扱えるのは、現時点では非常に小さなファイルベース入力だけ
- mapping ロジックは意図的に最小構成で、非自明な前提は明示的な TODO として残す
- validation は HAPI FHIR の基本的な R4 validation に接続している。JP Core package ベース validation は今後の課題
- bundle 永続化は現時点ではメモリ内のみ。次段階のために `deploy/` には PostgreSQL を用意している

## Validation

validation は bundle 生成後に実行します。現在の MVP では、生成された `Bundle` と各 entry resource を、HAPI FHIR validator tooling を薄く包んだ validation service abstraction 経由で検証します。validation 結果は `severity`、`location`、`message`、`profile` を持つ小さな共通形式に正規化します。`profile` は resource metadata から分かる場合のみ設定します。

意図的にスコープを絞っている点は次のとおりです。

- まずは HAPI ベースの構造 validation を使う
- validation service 自体は差し替え可能な interface にしておく
- JP Core package 読み込みや強い terminology validation は今後の課題にする

## API placeholder

- `POST /ingest/ssmix2`
- `GET /fhir/Bundle/{id}`
- `GET /health`

## テスト

現時点の初期実装には、core parser、canonical assembler、conversion pipeline abstraction、FHIR mapping、validation formatting に対する JUnit 5 テストを含めています。
