# Language Detection API Java Client

[![Maven metadata URI](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/com/detectlanguage/detectlanguage/maven-metadata.xml.svg)](https://mvnrepository.com/artifact/com.detectlanguage/detectlanguage)
[![Build Status](https://secure.travis-ci.org/detectlanguage/detectlanguage-java.svg)](http://travis-ci.org/detectlanguage/detectlanguage-java)

Detects language of given text. Returns detected language codes and scores.


## Installation

### Maven

Add this dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.detectlanguage</groupId>
    <artifactId>detectlanguage</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Gradle

Add this dependency to your `build.gradle`:

```gradle
repositories {
	mavenCentral()
}

dependencies {
	compile 'com.detectlanguage:detectlanguage:1.1.0'
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

// Enable secure mode (SSL) if passing sensitive information
// DetectLanguage.ssl = true;
```

### Language detection

```java
List<Result> results = DetectLanguage.detect("Hello world");

Result result = results.get(0);

System.out.println("Language: " + result.language);
System.out.println("Is reliable: " + result.isReliable);
System.out.println("Confidence: " + result.confidence);
```

### Simple detection

```java
String language = DetectLanguage.simpleDetect("Hello world");
```

### Batch detection

```java
String[] texts = {
	"Hello world",
	"Labas rytas"
};

List<List<Result>> results = DetectLanguage.detect(texts);
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

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Write your code **and tests**
4. Ensure all [tests](#testing) still pass
5. Commit your changes (`git commit -am 'Add some feature'`)
6. Push to the branch (`git push origin my-new-feature`)
7. Create new pull request
