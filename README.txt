RedTeam Test Client - открытый тестовый клиент для обучения античита (Fabric 1.21.11)

СБОРКА (один раз):
1. Установи JDK 21 (например, Temurin 21) и Gradle 9.2+ (или открой папку в IntelliJ IDEA - он всё сделает сам).
2. В папке проекта выполни:  gradle wrapper --gradle-version 9.2.0   (один раз)
3. Затем:  ./gradlew build        (Windows: gradlew.bat build)
4. Готовый мод: build/libs/redteam-client-0.1.0.jar

УСТАНОВКА:
- Положи jar в папку mods клиента Fabric 1.21.11 (нужен Fabric API).
- Заходи на СВОЙ тестовый сервер. Меню - Right Shift (клавиша меняется в настройках управления).

ЛОГ:
- В папке игры появится redteam_cheatlog.csv: время UTC в мс; модуль; событие (ON/OFF/MODE/ATTACK/SWAP); детали.
- Серверные данные сопоставляй с логом по времени (синхронизируй часы машин).

ПОД ДРУГУЮ ВЕРСИЮ: поменяй minecraft_version, yarn_mappings, fabric_version в gradle.properties
(актуальные значения: https://fabricmc.net/develop) и "minecraft" в fabric.mod.json; в коде могут потребоваться мелкие правки.
