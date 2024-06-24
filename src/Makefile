all: run

clean:
	rm -f out/*.jar

out/PerfectNumberCounter.jar: out/parcs.jar src/main/java/org/example/PerfectNumberCounter.java src/main/java/org/example/PerfectNumberChecker.java
	@javac -cp out/parcs.jar --release 17 -d out src/main/java/org/example/PerfectNumberCounter.java src/main/java/org/example/PerfectNumberChecker.java
	@jar cf out/PerfectNumberCounter.jar -C out org/example/PerfectNumberCounter.class -C out org/example/PerfectNumberChecker.class
	@rm -f out/org/example/PerfectNumberCounter.class out/org/example/PerfectNumberChecker.class

build: out/PerfectNumberCounter.jar

run: out/PerfectNumberCounter.jar
	@cd out && java -cp 'parcs.jar:PerfectNumberCounter.jar' org.example.Main

out/parcs.jar:
	cp /path/to/your/libs/parcs.jar out/parcs.jar
