# 書籍管理システム API (Book Management System API)

このプロジェクトは、Kotlin、Spring Boot、および jOOQ を使用して構築された書籍管理システムのバックエンド API です。

## 目次
- [主な機能](#主な機能)
- [技術スタック](#技術スタック)
- [環境構築](#環境構築)
    - [前提条件](#前提条件)
    - [データベースの起動 (PostgreSQL with Docker)](#データベースの起動-postgresql-with-docker)
    - [マイグレーションの実行 (Flyway)](#マイグレーションの実行-flyway)
    - [jOOQ コード生成](#jooq-コード生成)
    - [アプリケーションの起動](#アプリケーションの起動)
- [API エンドポイント](#api-エンドポイント)
    - [著者 (Author) API](#著者-author-api)
    - [書籍 (Book) API](#書籍-book-api)
- [テスト](#テスト)

## 主な機能
- 書籍および著者情報の登録・更新。
- 特定の著者に紐づく書籍一覧の取得。

## 技術スタック
- **言語**: Kotlin
- **フレームワーク**: Spring Boot, jOOQ
- **データベース**: PostgreSQL
- **マイグレーション**: Flyway
- **ビルドツール**: Gradle
- **テスト**: JUnit 5, Mockito

## 環境構築

### 前提条件
- Java Development Kit (JDK) 21 以上
- Docker および Docker Compose
- Gradle (通常は Spring Boot プロジェクトに同梱されている `./gradlew` を使用)

### データベースの起動 (PostgreSQL with Docker)
Docker Compose を使用して PostgreSQL データベースを起動します。

1.  ターミナルでプロジェクトのルートディレクトリに移動します。
2.  以下のコマンドで PostgreSQL コンテナを起動します：
    ```sh
    docker compose up -d
    ```
    これにより、`localhost:5432` で PostgreSQL が起動します。
    (DB名: `mydatabase`, ユーザー: `myuser`, パスワード: `secret`)

### マイグレーションの実行 (Flyway)
Flyway を使用してデータベースのスキーマ管理を行います。

1.  PostgreSQL コンテナが起動していることを確認します。
2.  以下のコマンドを実行してテーブルを作成します：
    ```sh
    ./gradlew flywayMigrate
    ```
    これにより、`src/main/resources/db/migration/V1__init.sql` が実行され、`authors`、`books`、`book_authors` テーブルが作成されます。

### jOOQ コード生成
jOOQ はデータベーススキーマから Kotlin クラスを生成し、型安全なデータアクセスを提供します。

1.  データベースが起動し、マイグレーションが完了していることを確認します。
2.  以下のコマンドでコードを生成します：
    ```sh
    ./gradlew generateJooq
    ```
    生成されたコードは `build/generated-src/jooq/main` に出力されます。
3.  生成後、IDE で Gradle の同期（Sync）を行い、生成されたソースを認識させてください。

### アプリケーションの起動
IDE または Gradle コマンドから起動できます。

-   **IDE の場合**: `src/main/kotlin/com/example/demo/DemoApplication.kt` を開き、`main` 関数を実行します。
-   **Gradle の場合**:
    ```sh
    ./gradlew bootRun
    ```
    起動後、API は `http://localhost:8080` で利用可能になります。

## API エンドポイント

### 著者 (Author) API
ベース URL: `/api/authors`

#### 1. 著者の新規登録
-   **エンドポイント**: `POST /api/authors`
-   **説明**: 新しい著者をシステムに登録します。
-   **リクエストボディ**: `AuthorRequest` (JSON)
    ```json
    {
      "name": "夏目漱石",
      "birthDate": "1867-02-09"
    }
    ```
-   **レスポンス**: `201 Created` / `AuthorResponse` (JSON)

#### 2. 著者情報の更新
-   **エンドポイント**: `PUT /api/authors/{id}`
-   **説明**: 既存の著者情報を更新します。
-   **成功レスポンス**: `200 OK`
-   **エラーレスポンス**: 著者が見つからない場合やバリデーションエラー時は `400 Bad Request`。

---

### 書籍 (Book) API
ベース URL: `/api`

#### 1. 書籍の新規登録
-   **エンドポイント**: `POST /api/books`
-   **説明**: 新しい書籍を登録し、著者を紐付けます。
-   **リクエストボディ**: `BookRequest` (JSON)
    ```json
    {
      "title": "吾輩は猫である",
      "price": 1200,
      "publicationStatus": "UNPUBLISHED",
      "authorIds": [1]
    }
    ```
-   **エラーレスポンス**: `400 Bad Request` (価格がマイナス、著者未指定、存在しない著者IDなど)

#### 2. 書籍情報の更新
-   **エンドポイント**: `PUT /api/books/{id}`
-   **説明**: 既存の書籍情報を更新します。
-   **制約**: 公開ステータスを `PUBLISHED` (公開済み) から `UNPUBLISHED` (未公開) に戻すことはできません。

#### 3. 著者別書籍一覧の取得
-   **エンドポイント**: `GET /api/authors/{authorId}/books`
-   **説明**: 特定の著者が執筆した書籍のリストを取得します。
-   **成功レスポンス**: `200 OK` / `BookResponse` の配列

## テスト
- Service レイヤーを中心にユニットテストを実装しています。
- 全テストの実行：
    ```sh
    ./gradlew test
    ```