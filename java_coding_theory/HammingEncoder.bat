chcp 65001
ECHO OFF
ECHO Код Хэмминга. Кодировщик.
ECHO Текстовый файл должен содержать двоичный код
ECHO Автор: Кузоватов Дамир. 11-901
ECHO выполняется компиляция программы...
call mvn clean > nul
call mvn compile > nul
ECHO Введите cледующие 2 строки:
ECHO 1. Двоичный код, который нужно закодировать.
set /P in="> ":
ECHO 2. Путь для сохранения закодированного кода
set /P encoded="> ":
call mvn -e exec:java -Dexec.mainClass=ru.itis.algorithms.classes.launchers.MainHammingEncoder -Dexec.args="%in% %encoded%"
PAUSE