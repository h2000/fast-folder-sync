# fast-folder-sync

Sync a folder to an other, based on filename (path) and size differences.

It only works in one direction, from source folder to target folder.

## Requirements

- working installation of ammonite shell (see <https://ammonite.io/#Ammonite-Shell>)

## Run

```scala
amm Sync.sc sync ./source ./destination
```

## Test / dev

```scala
amm -w Sync.sc runTest
```
