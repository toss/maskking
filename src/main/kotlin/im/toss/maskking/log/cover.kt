package im.toss.maskking.log

fun cover(f: () -> Unit): () -> Unit {
    f.invoke()
    return f
}
