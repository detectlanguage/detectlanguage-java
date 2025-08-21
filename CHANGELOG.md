# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## v2.0.2

### Fixed
- User agent header version resolution

## v2.0.1

### Fixed
- Maven source control url

## v2.0.0

### Added
- `getLanguages()` method to get supported languages
- `getAccountStatus()` method to get account status

### Changed
- Switched to v3 API which uses updated language detection model
- ⚠️ `detect()` method result fields are `language` and `score`
- `simpleDetect()` renamed to `detectCode()`

### Removed
- Secure mode configuration. HTTPS is used by default.
