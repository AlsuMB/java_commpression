chcp 65001
ECHO OFF
ECHO Алгоритм BWT и с последующим MTF (алгоритм "стопки книг")
ECHO Автор: Кузоватов Дамир. 11-901
ECHO выполняется компиляция программы...
call mvn clean > nul
call mvn compile > nul
ECHO Введите cледующие 4 строки:
ECHO 1. Текст, который нужно закодировать.
set /P in="> ":
ECHO 2. Путь для сохранения закодированного BWT текста
set /P encodedBWT="> ":
ECHO 3. Путь для сохранения закодированного MTF BWT кода
set /P encodedMTF="> ":
ECHO 4. Путь для сохранения декодированного MTF BWT кода
set /P decodedMTF="> ":
ECHO 5. Путь для сохранения декодированного BWT текста
set /P decodedBWT="> ":
ECHO 6. Путь для сохранения алфавита для MTF
set /p alphabet="> ":
call mvn -e exec:java -Dexec.mainClass=ru.itis.algorithms.classes.launchers.MainBwtMtf -Dexec.args="%in% %encodedBWT% %encodedMTF% %decodedMTF% %decodedBWT% %alphabet%"
PAUSE