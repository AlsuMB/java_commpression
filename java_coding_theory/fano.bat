chcp 65001
ECHO OFF
ECHO Алгоритм сжатия Фано
ECHO Автор: Кузоватов Дамир. 11-901
ECHO выполняется компиляция программы...
call mvn clean > nul
call mvn compile > nul
ECHO Введите cледующие 4 строки:
ECHO 1. Текст, который нужно закодировать.
set /P in="> ":
ECHO 2. Путь для сохранения закодированного сообщения.
set /P encoded="> ":
ECHO 3. Путь для сохранения декодированного файла.
set /P decoded="> ":
ECHO 4. Путь для сохранения вероятностей (лучше в формате .json)
set /P probs="> ":
call mvn -e exec:java -Dexec.mainClass=ru.itis.algorithms.classes.launchers.MainFano -Dexec.args="%in% %encoded% %decoded% %probs%"
PAUSE