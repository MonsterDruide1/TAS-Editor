REM building source to .class files
javac.exe -d buildout -cp src/main/java/;lib/flatlaf.jar -sourcepath src/main/java src/main/java/io/github/jadefalke2/TAS.java

REM unpacking library in buildout dir
cd buildout
jar.exe -xf ../lib/flatlaf.jar
cd ..

REM build the final jar
jar.exe -cvfm test.jar .\src\main\java\META-INF\MANIFEST.MF -C buildout .

PAUSE