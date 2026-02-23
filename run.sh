#!/bin/bash
# Use Java 17 from Downloads so Gradle runs correctly (AGP requires Java 17).
export JAVA_HOME="/Users/mohebabdelmasih/Downloads/jdk-17.0.18+8/Contents/Home"
exec ./gradlew "$@"
