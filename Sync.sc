import os._

def files(d: Path): IndexedSeq[(SubPath, Long)] =
  walk.attrs(d).map { case (p, i) => (p.relativeTo(d).asSubPath, i.size) }

@main
def sync(src: Path, dest: Path) = {
  val srcFileNameAndSizes = files(src)
  val srcFileNames = srcFileNameAndSizes.map(_._1)
  val destFileNameAndSizes = files(dest)
  val destFileNames = destFileNameAndSizes.map(_._1)

  val toAdd: Seq[SubPath] =
    srcFileNameAndSizes.diff(destFileNameAndSizes).map(_._1)
  println("New files (add to/override over dest)")
  pprint.pprintln(toAdd)
  val toDel: Seq[SubPath] = destFileNames.diff(srcFileNames)
  println("Old files (remove from dest)")
  pprint.pprintln(toDel)
  toDel.foreach(f => remove(dest / f))
  toAdd.foreach(f => copy.over(src / f, dest / f))
}

def prepareTestData(src: Path, dest: Path): Unit = {
  remove.all(src)
  remove.all(dest)
  makeDir.all(src)
  makeDir.all(dest)

  val data1 = Map("abc" -> """ABC01""", "def" -> """DEF002""")
  val data2 = Map("abc" -> """ABC01x""", "ghj" -> """GHJ000002""")

  data1.foreach { case (f, c) => write.over(src / f, c) }
  makeDir.all(src / "subdir1")
  write.over(src / "subdir1" / "xyz", "XYZasdf")
  data2.foreach { case (f, c) => write.over(dest / f, c) }
  makeDir.all(dest / "subdir1")
  write.over(dest / "subdir1" / "xyz", "XYZasdfx")
}

@main
def runTest(): Unit = {
  val wd = os.temp.dir(deleteOnExit = true)
  val src = wd / "src"
  val dest = wd / "dest"
  prepareTestData(src, dest)

  def dumpDir(dirPath: Path) =
    walk.attrs(dirPath).toList.sortBy(_._1.toString).foreach {
      case (p, stats) =>
        println(
          p.relativeTo(dirPath)
            .toString + " " + stats.size + " " + stats.fileType
        )
    }
  println("-- before")
  dumpDir(wd)
  sync(src, dest)
  println("-- after")
  dumpDir(wd)
  val s = files(src).toList.sortBy(_._1.toString())
  val d = files(dest).toList.sortBy(_._1.toString())
  assert(s.equals(d), "content of folders [src] and [dest] are not equal")
}
