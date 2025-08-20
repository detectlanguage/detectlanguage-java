# Detect Language API Java Client

[![Maven metadata URI](https://img.shields.io/maven-central/v/com.detectlanguage/detectlanguage)](https://mvnrepository.com/artifact/com.detectlanguage/detectlanguage)
[![Build Status](https://github.com/detectlanguage/detectlanguage-java/actions/workflows/main.yml/badge.svg)](https://github.com/detectlanguage/detectlanguage-java/actions)

Detects language of the given text. Returns detected language codes and scores.


## Installation

### Maven

Add this dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.detectlanguage</groupId>
    <artifactId>detectlanguage</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Gradle

Add this dependency to your `build.gradle`:

```gradle
repositories {
	mavenCentral()
}

dependencies {
	compile 'com.detectlanguage:detectlanguage:2.0.0'
}
```

## Usage

```java
import com.detectlanguage.DetectLanguage;
```

### Configuration

Before using Detect Language API client you have to setup your personal **API key**. You can get it by signing up at https://detectlanguage.com

```java
DetectLanguage.apiKey = "YOURAPIKEY";
```

### Language detection

```java
List<Result> results = DetectLanguage.detect("Hello world");

Result result = results.get(0);

System.out.println("Language: " + result.language);
System.out.println("Score: " + result.score);
```

### Language code detection

```java
String language = DetectLanguage.detectCode("Hello world");
```

### Batch detection

```java
String[] texts = {
	"Hello world",
	"Labas rytas"
};

List<List<Result>> results = DetectLanguage.detect(texts);
```

### Getting your account status

```java
AccountStatusResponse accountStatus = DetectLanguage.getAccountStatus();
```

### Getting list supported languages

```java
LanguageInfo[] languages = DetectLanguage.getLanguages();
```

## Requirements

- [gson](http://code.google.com/p/google-gson/)

Which you can download to `target/dependency` using:

    mvn dependency:copy-dependencies

## Issues

Please use appropriately tagged github [issues](https://github.com/detectlanguage/detectlanguage-java/issues) to request features or report bugs.

## Testing

    mvn test

## Publishing

[Sonatype OSS repository](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide).

### Snapshot

    mvn clean deploy

### Stage Release

    mvn release:clean
    mvn release:prepare
    mvn release:perform

### Release

Done using the [Sonatype Nexus UI](https://oss.sonatype.org/).
