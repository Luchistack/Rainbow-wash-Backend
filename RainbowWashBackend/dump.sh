find src -type f -name "*.java" | sort | while read f; do echo; echo "===== $f ====="; cat "$f"; done
