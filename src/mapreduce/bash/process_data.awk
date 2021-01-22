#!/usr/bin/env awk -f
BEGIN { FS=" "  }
NF { a[$1] += $2 }
END { for (i in a) print i, a[i] }
