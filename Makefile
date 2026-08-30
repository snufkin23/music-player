.PHONY: build release bundle test clean lint

build:
	./gradlew assembleDebug

release:
	./gradlew assembleRelease

bundle:
	./gradlew bundleRelease

test:
	./gradlew test

clean:
	./gradlew clean

lint:
	./gradlew lint